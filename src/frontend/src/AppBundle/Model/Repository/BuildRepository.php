<?php

namespace AppBundle\Model\Repository;


use AppBundle\Model\Entity\Project;
use Doctrine\ORM\EntityRepository;


class BuildRepository extends EntityRepository
{

    public function projectBuildsQuery(Project $project)
    {
        return $this->createQueryBuilder('build')
            ->select('build', 'job')
            ->leftJoin('build.jobs', 'job')
            ->where('build.project = :project')
            ->setParameter('project', $project)
            ->orderBy('build.id', 'DESC');
    }

    public function projectLastBuilds(Project $project, $buildCount = 5)
    {
        return $this->createQueryBuilder('build')
            ->where('build.project = :project')
            ->setParameter('project', $project)
            ->orderBy('build.id', 'DESC')
            ->setFirstResult(0)
            ->setMaxResults($buildCount)
            ->getQuery()
            ->getResult();
    }

    public function maxBuildNumber(Project $project)
    {
        $result = $this->createQueryBuilder('build')
            ->select('MAX(build.number)')
            ->where('build.project = :project')
            ->setParameter('project', $project)
            ->getQuery()
            ->getSingleScalarResult();
        if (null === $result) {
            $result = 0;
        }
        return $result;
    }

}