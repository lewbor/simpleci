<?php
namespace AppBundle\Frontend\Webhook;

use AppBundle\Model\Entity\Project;
use AppBundle\Model\Service\BuildCreator;
use AppBundle\System\Api\GitlabApi;
use Doctrine\ORM\EntityManager;
use Symfony\Bridge\Monolog\Logger;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class GitlabWebHookProcessor
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
        $event = $request->headers->get('X-Gitlab-Event', 'Push Hook');

        switch ($event) {
            case 'Push Hook':
            case 'Tag Push Hook':
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
            $this->logger->error("Commit count is 0");
            return;
        }

        try {
            $lastCommit = $message['commits'][$commitCount - 1];
            $buildDescription = $this->createBuildDescription($project, $lastCommit, $message);
            $this->buildCreator->createBuild($project, $buildDescription);
        } catch (\Exception $e) {
            $this->logger->error($e->getMessage());
        }
    }

    private function createBuildDescription(Project $project, array $commit, array $message)
    {
        $api = GitlabApi::createApi($project->getRepository());

        $ref = $this->parseRef($message['ref']);
        $buildConfig = $api->fetchConfig($message['project_id'], $commit['id']);
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