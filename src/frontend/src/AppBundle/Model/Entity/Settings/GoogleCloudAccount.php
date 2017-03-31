<?php

namespace AppBundle\Model\Entity\Settings;

use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert;
use Symfony\Bridge\Doctrine\Validator\Constraints\UniqueEntity;

/**
 * @ORM\Entity()
 * @ORM\Table(name="google_cloud_account")
 * @UniqueEntity("name")
 */
class GoogleCloudAccount
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

    /**
     * @var string
     * @ORM\Column(name="service_account", type="text")
     * @Assert\NotBlank()
     */
    public $serviceAccount;

    public function getId()
    {
        return $this->id;
    }


    public function setId($id)
    {
        $this->id = $id;
    }


    public function getName()
    {
        return $this->name;
    }


    public function setName($name)
    {
        $this->name = $name;
    }


    public function getServiceAccount()
    {
        return $this->serviceAccount;
    }


    public function setServiceAccount($serviceAccount)
    {
        $this->serviceAccount = $serviceAccount;
    }

}