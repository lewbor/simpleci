<?php

namespace AppBundle\Frontend\Webhook;


use AppBundle\Model\Entity\Project;
use AppBundle\Model\Entity\Settings\Repository\GithubRepository;
use AppBundle\Model\Entity\Settings\Repository\GitlabRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bridge\Monolog\Logger;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class WebHookController
{
    private $em;
    private $logger;
    private $gitlabWebHookProcessor;
    private $githubWebHookProcessor;

    public function __construct(
        EntityManagerInterface $em,
        Logger $logger,
        GitlabWebHookProcessor $gitlabWebHookProcessor,
        GithubWebHookProcessor $githubWebHookProcessor)
    {
        $this->em = $em;
        $this->logger = $logger;
        $this->gitlabWebHookProcessor = $gitlabWebHookProcessor;
        $this->githubWebHookProcessor = $githubWebHookProcessor;
    }

    public function hookAction(Request $request, $id)
    {
        $message = json_decode($request->getContent(), true);
        if (null === $message) {
            $this->logger->error(sprintf("Message is empty or incorrect json, project=%d", $id));
            return new Response();
        }

        $project = $this->em->getRepository(Project::class)->find($id);
        if ($project === null) {
            $this->logger->error(sprintf("Project does not exists, project=%d", $id));
            return new Response();
        }

        if ($project->getRepository() === null) {
            $this->logger->error(sprintf("Project has no repository, project=%d", $id));
            return new Response();
        }

        if ($project->getRepository() instanceof GitlabRepository) {
            $this->gitlabWebHookProcessor->processHook($request, $project, $message);
        } elseif ($project->getRepository() instanceof GithubRepository) {
            $this->githubWebHookProcessor->processHook($request, $project, $message);
        }

        return new Response();
    }

}