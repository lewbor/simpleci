<?php

namespace AppBundle\Model\Entity;


use Doctrine\ORM\Mapping as ORM;

/**
 * @ORM\Entity(repositoryClass="AppBundle\Model\Repository\JobRepository")
 * @ORM\Table(name="job")
 */
class Job
{
    /**
     * @var integer
     * @ORM\Id
     * @ORM\Column(type="integer")
     * @ORM\GeneratedValue(strategy="AUTO")
     */
    protected $id;

    /**
     * Number of job in current build
     * @var int
     * @ORM\Column(name="number", type = "integer" )
     */
    protected $number;

    /**
     * @var Build
     * @ORM\ManyToOne(targetEntity="Build", inversedBy="jobs")
     * @ORM\JoinColumn(name="build_id", referencedColumnName="id")
     */
    protected $build;

    /**
     * @var \DateTime
     * @ORM\Column(name="created_at", type = "datetime",  nullable=false)
     */
    private $createdAt;

    /**
     * @var \DateTime
     * @ORM\Column(name="started_at", type="datetime",  nullable=true)
     */
    private $startedAt;

    /**
     * @var \DateTime
     * @ORM\Column(name="ended_at", type="datetime",  nullable=true)
     */
    private $endedAt;

    /**
     * @var string
     * @ORM\Column(name="status", type = "OperationStatus")
     */
    private $status;

    /**
     * @var string
     * @ORM\Column(name="stage", type = "string")
     */
    private $stage;

    /**
     * @var string
     * @ORM\Column(name="config", type = "text")
     */
    private $config;

    /**
     * @var string
     * @ORM\Column(name="log", type="text")
     */
    protected $log = '';

    public function getId()
    {
        return $this->id;
    }

    public function setId($id)
    {
        $this->id = $id;
        return $this;
    }

    public function getStartedAt()
    {
        return $this->startedAt;
    }

    public function setStartedAt($startedAt)
    {
        $this->startedAt = $startedAt;
        return $this;
    }

    public function getEndedAt()
    {
        return $this->endedAt;
    }

    public function setEndedAt($endedAt)
    {
        $this->endedAt = $endedAt;
        return $this;
    }

    public function getStatus()
    {
        return $this->status;
    }

    public function setStatus($status)
    {
        $this->status = $status;
        return $this;
    }

    public function getLog()
    {
        return $this->log;
    }

    public function setLog($log)
    {
        $this->log = $log;
        return $this;
    }

    public function getBuild()
    {
        return $this->build;
    }

    public function setBuild(Build $build)
    {
        $this->build = $build;
        return $this;
    }

    public function getStage()
    {
        return $this->stage;
    }

    public function setStage($stage)
    {
        $this->stage = $stage;
        return $this;
    }

    public function getConfig()
    {
        return $this->config;
    }

    public function setConfig($config)
    {
        $this->config = $config;
        return $this;
    }

    public function getNumber()
    {
        return $this->number;
    }

    public function setNumber($number)
    {
        $this->number = $number;
        return $this;
    }

    public function getCreatedAt()
    {
        return $this->createdAt;
    }

    public function setCreatedAt($createdAt)
    {
        $this->createdAt = $createdAt;
        return $this;
    }


}