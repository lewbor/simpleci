<?php

namespace AppBundle\System\Twig;


use AppBundle\Model\Entity\Build;
use AppBundle\Model\Entity\Settings\Repository\GithubRepository;
use AppBundle\Model\Entity\Settings\Repository\GitlabRepository;
use AppBundle\Model\Entity\Settings\Repository\Repository;
use AppBundle\Model\Type\OperationStatus;

class TwigExtension extends \Twig_Extension
{
    private static $boostrapClasses = [
        OperationStatus::PENDING => 'warning',
        OperationStatus::STOPPED => 'info',
        OperationStatus::RUNNING => 'info',
        OperationStatus::FINISHED_SUCCESS => 'success',
        OperationStatus::FAILED => 'danger',
    ];

    public function getFilters()
    {
        return array(
            new \Twig_SimpleFilter('bool_label', array($this, 'boolLabel'),  ['is_safe' => ['html']]),
            new \Twig_SimpleFilter('bootstrap_build_class', array($this, 'bootstrapBuildClass')),
            new \Twig_SimpleFilter('json_decode', array($this, 'jsonDecode')),
            new \Twig_SimpleFilter('simple_time_diff', array($this, 'minutesTimeDiff')),
            new \Twig_SimpleFilter('build_commit_link', array($this, 'buildCommitLink'), ['is_safe' => ['html']]),
            new \Twig_SimpleFilter('branch_link', array($this, 'branchLink'), ['is_safe' => ['html']]),
            new \Twig_SimpleFilter('repository_icon', array($this, 'repositoryIcon'), ['is_safe' => ['html']]),
        );
    }

    public function boolLabel($value)
    {
        if ($value) {
            return '<span class="label label-success">yes</span>';
        } else {
            return '<span class="label label-danger">no</span>';
        }
    }

    public function repositoryIcon(Repository $repository)
    {
        if ($repository instanceof GithubRepository) {
            return '<i class="fa fa-github"></i>';
        } elseif ($repository instanceof GitlabRepository) {
            return '<i class="fa fa-gitlab"></i>';
        } else {
            return '';
        }
    }

    public function buildCommitLink(Build $build)
    {
        $repository = $build->getProject()->getRepository();
        if ($repository instanceof GithubRepository) {
            $commit = $build->getCommit();
            $shortCommit = substr($commit, 0, 7);
            $url = sprintf('%s/%s/commit/%s', $repository->getUrl(), $build->getProject()->getServerIdentity(),
                $commit);

            return sprintf('<a href="%s">%s</a>', $url, $shortCommit);
        } elseif ($repository instanceof GitlabRepository) {
            $commit = $build->getCommit();
            $shortCommit = substr($commit, 0, 7);
            $url = sprintf('%s/%s/commit/%s', $repository->getUrl(), $build->getProject()->getServerIdentity(),
                $commit);

            return sprintf('<a href="%s">%s</a>', $url, $shortCommit);
        } else {
            return $build->getCommit();
        }
    }

    public function branchLink(Build $build)
    {
        $repository = $build->getProject()->getRepository();
        if ($repository instanceof GithubRepository) {
            $url = sprintf('%s/%s/tree/%s', $repository->getUrl(), $build->getProject()->getServerIdentity(),
                $build->getBranch());
            return sprintf('<a href="%s">%s</a>', $url, $build->getBranch());
        } elseif ($repository instanceof GitlabRepository) {
            $url = sprintf('%s/%s/tree/%s', $repository->getUrl(), $build->getProject()->getServerIdentity(),
                $build->getBranch());

            return sprintf('<a href="%s">%s</a>', $url, $build->getBranch());
        } else {
            return $build->getBranch();
        }
    }

    public function minutesTimeDiff(\DateTime $from, \DateTime $to)
    {
        $diff = $to->diff($from);

        $minutes = $diff->days * 24 * 60;
        $minutes += $diff->h * 60;
        $minutes += $diff->i;
        if ($minutes == 0) {
            return sprintf('%s sec', $diff->s);
        } else {
            return sprintf('%s min %s sec', $minutes, $diff->s);
        }

    }

    public function bootstrapBuildClass($status)
    {
        if (!isset(self::$boostrapClasses[$status])) {
            throw new \Exception(sprintf("Unknown build status %s", $status));
        }
        return self::$boostrapClasses[$status];

    }

    public function jsonDecode($data)
    {
        return json_decode($data, true);
    }

    public function getName()
    {
        return 'simpleci_extension';
    }

}
