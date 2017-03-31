<?php

namespace AppBundle\Model\Entity\Settings\Provider;

use AppBundle\Model\Entity\Settings\GoogleCloudAccount;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert;
use Symfony\Bridge\Doctrine\Validator\Constraints as DoctrineConstraint;

/**
 * @ORM\Entity()
 * @ORM\Table(name="provider")
 * @ORM\InheritanceType("SINGLE_TABLE")
 * @ORM\DiscriminatorColumn(name="discr", type="string")
 * @ORM\DiscriminatorMap({"gcp" = "GoogleComputeProvider"})
 * @DoctrineConstraint\UniqueEntity("name")
 */
abstract class Provider
{
    /**
     * @var integer
     * @ORM\Id
     * @ORM\Column(type="integer")
     * @ORM\GeneratedValue(strategy="AUTO")
     */
    protected $id;

    /**
     * @var string
     * @ORM\Column(name="name", type="string", unique=true)
     * @Assert\NotBlank()
     */
    protected $name;

    public function getId()
    {
        return $this->id;
    }

    public function setId($id)
    {
        $this->id = $id;
        return $this;
    }


    public function getName()
    {
        return $this->name;
    }

    public function setName($name)
    {
        $this->name = $name;
        return $this;
    }
}