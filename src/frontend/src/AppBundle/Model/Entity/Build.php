<?php


namespace AppBundle\Model\Entity;

use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\Common\Collections\Collection;
use Doctrine\ORM\Mapping as ORM;

/**
 * @ORM\Entity(repositoryClass="AppBundle\Model\Repository\BuildRepository")
 * @ORM\Table(name="build")
 */
class Build
{
    /**
     * @var integer
     * @ORM\Id
     * @ORM\Column(type="integer")
     * @ORM\GeneratedValue(strategy="AUTO")
     */
    protected $id;

    /**
     * @var int
     * @ORM\Column(name="number", type = "integer" )
     */
    protected $number;

    /**
     * @var Project
     * @ORM\ManyToOne(targetEntity="Project", inversedBy="builds")
     * @ORM\JoinColumn(name="project_id", referencedColumnName="id")
     */
    protected $project;

    /**
     * @var \DateTime
     * @ORM\Column(name="created_at", type = "datetime" )
     */
    private $createdAt;

    /**
     * @var \DateTime
     * @ORM\Column(name="started_at", type = "datetime",  nullable = true)
     */
    private $startedAt;

    /**
     * @var \DateTime
     * @ORM\Column(name="ended_at", type = "datetime",  nullable = true)
     */
    private $endedAt;

    /**
     * @var string
     * @ORM\Column(name="status", type = "OperationStatus")
     */
    private $status;

    /**
     * @var string
     * @ORM\Column(name="commit", type="string")
     */
    protected $commit;

    /**
     * @var string
     * @ORM\Column(name="commit_range", type="string")
     */
    protected $commitRange;

    /**
     * @var string
     * @ORM\Column(name="branch", type="string")
     */
    protected $branch;

    /**
     * @var string
     * @ORM\Column(name="tag", type="string", nullable=true)
     */
    protected $tag;


    /**
     * @var string
     * @ORM\Column(name="message", type="string")
     */
    protected $message;

    /**
     * @var string
     * @ORM\Column(name="author_name", type="string")
     */
    protected $authorName;

    /**
     * @var string
     * @ORM\Column(name="author_email", type="string")
     */
    protected $authorEmail;

    /**
     * @var \DateTime
     * @ORM\Column(name="committed_date", type = "datetime" )
     */
    protected $committedDate;

    /**
     * @var  string
     * @ORM\Column(name="config", type="text")
     */
    protected $config;

    /**
     * @var  string
     * @ORM\Column(name="error_message", type="text", nullable=true)
     */
    protected $errorMessage;

    /**
     * @var Collection|Job[]
     * @ORM\OneToMany(targetEntity="Job", mappedBy="build", cascade={"persist"}, orphanRemoval=true)
     */
    protected $jobs;

    public function __construct()
    {
        $this->jobs = new ArrayCollection();
    }

    public function addJob(Job $job)
    {
        $job->setBuild($this);
        $this->jobs[] = $job;
        return $this;
    }

    public function removeJob(Job $job)
    {
        $this->jobs->removeElement($job);
        return $this;
    }

    public function getId()
    {
        return $this->id;
    }

    public function setId($id)
    {
        $this->id = $id;
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

     public function getProject()
    {
        return $this->project;
    }

    public function setProject($project)
    {
        $this->project = $project;
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


    public function getCommit()
    {
        return $this->commit;
    }

    public function setCommit($commit)
    {
        $this->commit = $commit;
        return $this;
    }

    public function getMessage()
    {
        return $this->message;
    }

    public function setMessage($message)
    {
        $this->message = $message;
        return $this;
    }

    public function getAuthorName()
    {
        return $this->authorName;
    }

    public function setAuthorName($authorName)
    {
        $this->authorName = $authorName;
        return $this;
    }

     public function getCommittedDate()
    {
        return $this->committedDate;
    }

    public function setCommittedDate($committedDate)
    {
        $this->committedDate = $committedDate;
        return $this;
    }

    public function getAuthorEmail()
    {
        return $this->authorEmail;
    }

    public function setAuthorEmail($authorEmail)
    {
        $this->authorEmail = $authorEmail;
        return $this;
    }

    public function getBranch()
    {
        return $this->branch;
    }

    public function getCommitRange()
    {
        return $this->commitRange;
    }

    public function setCommitRange($commitRange)
    {
        $this->commitRange = $commitRange;
        return $this;
    }

    public function setBranch($branch)
    {
        $this->branch = $branch;
        return $this;
    }

    public function getJobs()
    {
        return $this->jobs;
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

    public function getErrorMessage()
    {
        return $this->errorMessage;
    }

    public function setErrorMessage($errorMessage)
    {
        $this->errorMessage = $errorMessage;
        return $this;
    }

    public function getTag()
    {
        return $this->tag;
    }

    public function setTag($tag)
    {
        $this->tag = $tag;
        return $this;
    }


}