<?php

namespace AppBundle\System\Api;


use AppBundle\Model\Entity\Settings\Repository\GithubRepository;
use Github\Api\Organization;
use Github\Client;

class GithubApi
{
    /** @var Client Github\Client */
    private $client;

    public function __construct(Client $client)
    {
        $this->client = $client;
    }

    public function projects()
    {
        $repos = $this->client->api('me')->repositories();

        /** @var Organization[] $orgs */
        $orgs = $this->client->api('me')->organizations();
        foreach ($orgs as $org) {
            $orgRepos = $this->client->api('organizations')->repositories($org['login']);
            $repos = array_merge($repos, $orgRepos);
        }
        return $repos;
    }

    public function listBranchesAndCommits($serverIdentity)
    {
        list($user, $repo) = explode('/', $serverIdentity);
        $branches = $this->client->api('repos')->branches($user, $repo);
        $result = [];
        foreach ($branches as $branch) {
            $commits = $this->listBranchCommits($serverIdentity, $branch['name']);
            $result[$branch['name']] = array_map(function ($commit) {
                return [
                    'id' => $commit['sha'],
                    'author' => $commit['commit']['author']['name'],
                    'author_email' => $commit['commit']['author']['email'],
                    'date' => $commit['commit']['author']['date'],
                    'message' => $commit['commit']['message']
                ];
            }, $commits);
        }
        return $result;
    }

    public function commitInfo($serverIdentity, $sha)
    {
        list($user, $repo) = explode('/', $serverIdentity);
        $commit = $this->client->api('repos')->commits()->show($user, $repo, $sha);
        return [
            'sha' => $commit['sha'],
            'author' => $commit['commit']['author']['name'],
            'author_email' => $commit['commit']['author']['email'],
            'message' => $commit['commit']['message'],
            'commited_date' => $commit['commit']['author']['date'],
            'parents' => array_map(function($parentCommit){ return $parentCommit['sha']; }, $commit['parents'] )
        ];
    }

    public function fetchConfig($serverIdentity, $commit)
    {
        list($user, $repo) = explode('/', $serverIdentity);
        $config = $this->client->api('repo')->contents()->download($user, $repo, '.simpleci.yml', $commit);
        return $config;
    }


    public function addDeployKeyToRepository($serverIdentity, $keyName, $keyValue)
    {
        list($user, $repo) = explode('/', $serverIdentity);
        $key = $this->client->api('repo')
            ->keys()
            ->create($user, $repo, array('title' => $keyName, 'key' => $keyValue));
    }

    private function listBranchCommits($serverIdentity, $branch)
    {
        list($user, $repo) = explode('/', $serverIdentity);
        /** @var \Github\Api\Repo $repoApi */
        $repoApi = $this->client->api('repos');
        $commits = $repoApi->commits()->all($user, $repo, array('sha' => $branch));
        return $commits;
    }

    public static function createApi(GithubRepository $repository)
    {
        $client = new Client();
        $client->authenticate($repository->getToken(), null, Client::AUTH_HTTP_TOKEN);

        return new self($client);
    }


}