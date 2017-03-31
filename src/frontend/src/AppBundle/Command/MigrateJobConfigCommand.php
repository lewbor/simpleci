<?php

namespace AppBundle\Command;


use AppBundle\Model\Entity\Job;
use Symfony\Bundle\FrameworkBundle\Command\ContainerAwareCommand;
use Symfony\Component\Console\Input\InputInterface;
use Symfony\Component\Console\Output\OutputInterface;

class MigrateJobConfigCommand extends ContainerAwareCommand
{
    protected function configure()
    {
        $this
            ->setName('migrate:job_config');
    }
    protected function execute(InputInterface $input, OutputInterface $output)
    {
        $em = $this->getContainer()->get('doctrine.orm.entity_manager');

        $query = $em->createQuery('Select entity from '. Job::class.  ' entity');
        $iterator = $query->iterate();
        foreach ($iterator as $row) {
            /** @var Job $job */
            $job = $row[0];
            $config = json_decode($job->getConfig(), true);
            if(isset($config['container'])) {
                continue;
            }

            $env = isset($config['env']) ? $config['env'] :
                (isset($config['matrix_env']) ? $config['matrix_env'] : []);
            $newConfig = [
                'container' => [
                    'image' => [
                        'name' => $config['image'],
                        'tag' => 'latest'],
                    'environment' => $this->parseEnv($env),
                    'privileged' => isset($config['privileged']) ? $config['privileged'] : false
                ]];

            $job->setConfig(json_encode($newConfig));

            $em->persist($job);
            $em->flush();
            $em->clear();
        }
    }


    private function parseEnv($env)
    {
        if (is_array($env)) {
            $result = [];
            foreach ($env as $valueStr) {
                list($key, $value) = explode('=', $valueStr, 2);
                $result[] = ['name' => $key, 'value' => $value];
            }
            return $result;
        } elseif (is_string($env)) {
            $env = explode(' ', $env);
            $env = array_map('trim', $env);

            $result = [];
            foreach ($env as $valueStr) {
                list($key, $value) = explode('=', $valueStr, 2);
                $result[] = ['name' => $key, 'value' => $value];
            }
            return $result;
        } else {
            return [];
        }
    }
}