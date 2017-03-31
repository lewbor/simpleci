<?php

namespace AppBundle\System\Api;


use AppBundle\Model\Entity\Settings\Repository\GitlabRepository;
use Gitlab\Client;
use Gitlab\Model\Commit;

class GitlabApi
{
    const API_VERSION = 'v3';
    /** @var Client */
    private $client;

    public function __construct(Client $client)
    {
        $this->client = $client;
    }

    public function projects()
    {
        $projects = $this->client->api('projects')->all();
        return $projects;
    }

    public function listBranchesAndCommits($serverIdentity)
    {
        $projectApi = new \Gitlab\Model\Project($serverIdentity, $this->client);
        $branches = $projectApi->branches();
        $result = [];
        foreach ($branches as $branch) {
            $commits = $projectApi->commits(0, 10, $branch->name);
            $result[$branch->name] = array_map(function (Commit $commit) {
                return [
                    'id' => $commit->id,
                    'short_id' => substr($commit->id, 0, 7),
                    'author' => $commit->author_name,
                    'author_email' => $commit->author_email,
                    'date' => $commit->created_at,
                    'message' => isset($commit->message) ? $commit->message : $commit->title ];
            }, $commits);
        }

        return $result;
    }

    public function commitInfo($serverIdentity, $sha)
    {

        $projectApi = new \Gitlab\Model\Project($serverIdentity, $this->client);
        $commitInfo = $projectApi->commit($sha);
        return [
            'sha' => $commitInfo->id,
            'author' => $commitInfo->author_name,
            'author_email' => $commitInfo->author_email,
            'message' => $commitInfo->message,
            'commited_date' => $commitInfo->committed_date];
    }

    public function lastCommitInBranch($serverIdentity, $branch)
    {
        $projectApi = new \Gitlab\Model\Project($serverIdentity, $this->client);

        $branchApi = $projectApi->branch($branch);
        $commitInfo = $branchApi->commit;
        return $commitInfo;
    }

    public function fetchConfig($serverIdentity, $commit)
    {
        $projectApi = new \Gitlab\Model\Project($serverIdentity, $this->client);
        $config = $projectApi->blob($commit, '.simpleci.yml');
        return $config;
    }

    public function addDeployKeyToRepository($serverIdentity, $keyName, $keyValue)
    {
        $gitlabProject = (new \Gitlab\Model\Project($serverIdentity, $this->client))->show();

        $existingKeys = $gitlabProject->keys();
        foreach ($existingKeys as $key) {
            if ($key->key == $keyValue) {
                $gitlabProject->removeKey($key->id);
            }
        }
        $gitlabProject->addKey($keyName, $keyValue);
    }

    public static function createApi(GitlabRepository $repository)
    {
        $client = (new Client(sprintf('%s/api/%s/', $repository->getUrl(), self::API_VERSION)))
            ->authenticate($repository->getToken(), Client::AUTH_URL_TOKEN);
        return new self($client);
    }
}