<?php

namespace AppBundle\Model\Entity\Settings\Repository;

use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert;
use Symfony\Bridge\Doctrine\Validator\Constraints as DoctrineConstraint;

/**
 * @ORM\Entity()
 * @ORM\Table(name="github_repository")
 * @DoctrineConstraint\UniqueEntity("url")
 */
class GithubRepository extends Repository
{
    /**
     * @var string
     * @ORM\Column(name="token", type="string")
     * @Assert\NotBlank()
     */
    protected $token;

    public function getToken()
    {
        return $this->token;
    }

    public function setToken($token)
    {
        $this->token = $token;
        return $this;
    }
}