<?php

namespace AppBundle\Model\Repository;


use AppBundle\Model\Entity\Job;
use Doctrine\ORM\EntityRepository;

class JobRepository extends EntityRepository
{

    /**
     * @return Job
     * @throws \Doctrine\ORM\NoResultException
     * @throws \Doctrine\ORM\NonUniqueResultException
     */
    public function latestJob()
    {
        return $this->createQueryBuilder('job')
            ->select('job')
            ->orderBy('job.id', 'DESC')
            ->getQuery()
            ->setMaxResults(1)
            ->getOneOrNullResult();
    }


}