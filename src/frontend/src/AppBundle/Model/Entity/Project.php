<?php

namespace AppBundle\Model\Entity;

use AppBundle\Model\Entity\Settings\Cache\Cache;
use AppBundle\Model\Entity\Settings\Provider\Provider;
use AppBundle\Model\Entity\Settings\Repository\Repository;
use AppBundle\Model\Entity\Settings\SshKey;
use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\Common\Collections\Collection;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert;

/**
 * @ORM\Entity(repositoryClass="AppBundle\Model\Repository\ProjectRepository")
 * @ORM\Table(name="project")
 */
class Project
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
     * @ORM\Column(name="name", type="string")
     */
    protected $name;

    /**
     * @var string
     * @ORM\Column(name="description", type="text", nullable=true)
     */
    protected $description;

    /**
     * @var string
     * @ORM\Column(name="server_identity", type="string")
     */
    protected $serverIdentity;

    /**
     * @var string
     * @ORM\Column(name="repository_url", type="string")
     */
    protected $repositoryUrl;

    /**
     * @var Repository
     * @ORM\ManyToOne(targetEntity="AppBundle\Model\Entity\Settings\Repository\Repository")
     * @ORM\JoinColumn(name="repository_id", referencedColumnName="id", nullable=false)
     * @Assert\NotNull()
     */
    protected $repository;

    /**
     * @var SshKey
     * @ORM\ManyToOne(targetEntity="AppBundle\Model\Entity\Settings\SshKey")
     * @ORM\JoinColumn(name="ssh_key_id", referencedColumnName="id", nullable=true)
     */
    protected $sshKey;

    /**
     * @var Cache
     * @ORM\ManyToOne(targetEntity="AppBundle\Model\Entity\Settings\Cache\Cache")
     * @ORM\JoinColumn(name="cache_id", referencedColumnName="id", nullable=true)
     */
    protected $cache;

    /**
     * @var Provider
     * @ORM\ManyToOne(targetEntity="AppBundle\Model\Entity\Settings\Provider\Provider")
     * @ORM\JoinColumn(name="provider_id", referencedColumnName="id", nullable=true)
     */
    protected $provider;

    /**
     * @var Collection|Build[]
     * @ORM\OneToMany(targetEntity="Build", mappedBy="project", orphanRemoval=true)
     */
    protected $builds;

    public function __construct()
    {
        $this->builds = new ArrayCollection();
    }

    public function getSshKey()
    {
        return $this->sshKey;
    }

    public function setSshKey(SshKey $sshKey = null)
    {
        $this->sshKey = $sshKey;
        return $this;
    }

    public function getCache()
    {
        return $this->cache;
    }

    public function setCache(Cache $cache = null)
    {
        $this->cache = $cache;
        return $this;
    }

    public function getProvider()
    {
        return $this->provider;
    }

    public function setProvider($provider)
    {
        $this->provider = $provider;
    }

    public function getRepository()
    {
        return $this->repository;
    }

    public function setRepository($repository)
    {
        $this->repository = $repository;
        return $this;
    }

    /**
     * @return mixed
     */
    public function getId()
    {
        return $this->id;
    }

    /**
     * @param mixed $id
     */
    public function setId($id)
    {
        $this->id = $id;
    }

    /**
     * @return mixed
     */
    public function getName()
    {
        return $this->name;
    }

    /**
     * @param mixed $name
     * @return $this
     */
    public function setName($name)
    {
        $this->name = $name;
        return $this;
    }

    /**
     * @return mixed
     */
    public function getDescription()
    {
        return $this->description;
    }

    /**
     * @param mixed $description
     * @return $this
     */
    public function setDescription($description)
    {
        $this->description = $description;
        return $this;
    }

    /**
     * @return string
     */
    public function getServerIdentity()
    {
        return $this->serverIdentity;
    }

    /**
     * @param string $serverIdentity
     * @return $this
     */
    public function setServerIdentity($serverIdentity)
    {
        $this->serverIdentity = $serverIdentity;
        return $this;
    }


    /**
     * @return string
     */
    public function getRepositoryUrl()
    {
        return $this->repositoryUrl;
    }

    /**
     * @param string $repositoryUrl
     * @return $this
     */
    public function setRepositoryUrl($repositoryUrl)
    {
        $this->repositoryUrl = $repositoryUrl;
        return $this;
    }


    /**
     * @return Collection|Build[]
     */
    public function getBuilds()
    {
        return $this->builds;
    }

    /**
     * @param Collection|Build[] $builds
     * @return $this
     */
    public function setBuilds($builds)
    {
        $this->builds = $builds;
        return $this;
    }

    /**
     * @return string
     */
    public function getRepositoryType()
    {
        return $this->repositoryType;
    }

    /**
     * @param string $repositoryType
     * @return $this
     */
    public function setRepositoryType($repositoryType)
    {
        $this->repositoryType = $repositoryType;
        return $this;
    }


}