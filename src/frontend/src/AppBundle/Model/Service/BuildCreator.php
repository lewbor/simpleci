<?php

namespace AppBundle\Model\Service;

use AppBundle\Model\Entity\Build;
use AppBundle\Model\Entity\Project;
use AppBundle\Model\Type\OperationStatus;
use Doctrine\ORM\EntityManager;
use OldSound\RabbitMqBundle\RabbitMq\Producer;
use Symfony\Bridge\Monolog\Logger;
use Symfony\Component\Yaml\Exception\ParseException;
use Symfony\Component\Yaml\Yaml;

class BuildCreator
{
    private $em;
    private $buildProducer;
    private $logger;

    public function __construct(
        EntityManager $em,
        Producer $dispatcherProducer,
        Logger $logger)
    {
        $this->em = $em;
        $this->logger = $logger;
        $this->buildProducer = $dispatcherProducer;
    }

    /**
     * @param Project $project
     * @param $buildInfo array
     * @return \AppBundle\Model\Entity\Build
     */
    public function createBuild(Project $project, array $buildInfo)
    {
        $build = (new Build())
            ->setProject($project)
            ->setNumber($this->getLastBuildNumber($project) + 1)
            ->setCreatedAt(new \DateTime())
            ->setStatus(OperationStatus::PENDING)
            ->setCommit($buildInfo['commit'])
            ->setCommitRange($buildInfo['commit_range'])
            ->setBranch($buildInfo['branch'])
            ->setTag(isset($buildInfo['tag']) ? $buildInfo['tag'] : null)
            ->setAuthorName($buildInfo['author_name'])
            ->setAuthorEmail($buildInfo['author_email'])
            ->setMessage($buildInfo['commit_message'])
            ->setCommittedDate($buildInfo['commit_date']);
        try {
            $config = json_encode(Yaml::parse($buildInfo['build_config']));
            $build->setConfig($config);
        } catch( ParseException $e) {
            $build
                ->setStatus(OperationStatus::FAILED)
                ->setErrorMessage($e->getMessage());
        }


        $this->em->persist($build);
        $this->em->flush();

        if($build->getStatus() == OperationStatus::PENDING) {
            $this->sendNewBuildMessage($build);
        }

        return $build;
    }

    private function sendNewBuildMessage(Build $build)
    {
        $newBuildMessage = [
            'type' => 'build_request',
            'build_id' => $build->getId()];
        $this->buildProducer->publish(json_encode($newBuildMessage));
    }

    private function getLastBuildNumber(Project $project)
    {
        return $this->em->getRepository(Build::class)
            ->maxBuildNumber($project);
    }

}