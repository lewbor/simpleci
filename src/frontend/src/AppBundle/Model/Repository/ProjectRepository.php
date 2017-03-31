<?php

namespace AppBundle\Model\Repository;


use Doctrine\ORM\EntityRepository;

class ProjectRepository extends EntityRepository {

    public function searchByIdentity($serverIdentity) {
        return $this->createQueryBuilder('project')
            ->where('project.serverIdentity = :serverIdentity')
            ->setParameter('serverIdentity', $serverIdentity)
            ->getQuery()
            ->getSingleResult();
    }

    public function allProjects() {
        return $this->createQueryBuilder('project')
            ->getQuery()
            ->getResult();
    }
}