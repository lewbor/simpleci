<?php

namespace AppBundle\Frontend\Webhook;


use AppBundle\Model\Entity\Project;
use AppBundle\Model\Repository\ProjectRepository;
use AppBundle\Model\Service\BuildCreator;
use AppBundle\System\Api\GithubApi;
use Doctrine\ORM\EntityManager;
use Symfony\Bridge\Monolog\Logger;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class GithubWebHookProcessor
{
    private $logger;
    private $buildCreator;

    public function __construct(
        Logger $logger,
        BuildCreator $buildCreator)
    {
        $this->logger = $logger;
        $this->buildCreator = $buildCreator;
    }

    public function processHook(Request $request, Project $project, $message)
    {
        $event = $request->headers->get('X-Github-Event', 'push');

        switch ($event) {
            case 'ping':
                return new Response('pong');
            case 'push':
                $this->processPushHook($project, $message);
                break;
            default:
                break;
        }

        return new Response();
    }

    private function processPushHook(Project $project, array $message)
    {
        $commitCount = count($message['commits']);
        if ($commitCount == 0) {
            $lastCommit = $message['head_commit'];
        } else {
            $lastCommit = $message['commits'][$commitCount - 1];
        }

        try {
            $buildDescription = $this->createBuildDescription($project, $lastCommit, $message);
            $this->buildCreator->createBuild($project, $buildDescription);
        } catch (\Exception $e) {
            $this->logger->error($e->getMessage());
        }
    }

    private function createBuildDescription(Project $project, array $commit, array $message)
    {
        $api = GithubApi::createApi($project->getRepository());

        $ref = $this->parseRef($message['ref']);
        $buildConfig = $api->fetchConfig($message['repository']['full_name'], $commit['id']);
        return [
            'commit' => $commit['id'],
            'commit_range' => sprintf('%s...%s', $message['before'], $message['after']),
            'branch' => $ref[2],
            'tag' => $ref[1] == 'tags' ? $ref[2] : null,
            'author_name' => $commit['author']['name'],
            'author_email' => $commit['author']['email'],
            'commit_message' => $commit['message'],
            'commit_date' => new \DateTime($commit['timestamp']),
            'build_config' => $buildConfig];
    }

    private function parseRef($ref)
    {
        return explode('/', $ref, 3);
    }

}