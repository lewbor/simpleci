<?php

namespace AppBundle\Model\Entity\Settings\Provider;

use AppBundle\Model\Entity\Settings\GoogleCloudAccount;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert;
use Symfony\Bridge\Doctrine\Validator\Constraints as DoctrineConstraint;

/**
 * @ORM\Entity()
 */
class GoogleComputeProvider extends Provider
{
    /**
     * @var GoogleCloudAccount
     * @ORM\ManyToOne(targetEntity="AppBundle\Model\Entity\Settings\GoogleCloudAccount")
     * @ORM\JoinColumn(name="gc_account_id", referencedColumnName="id", nullable=false)
     * @Assert\NotNull()
     **/
    protected $gcAccount;

    /**
     * @var string
     * @ORM\Column(name="project", type="string")
     * @Assert\NotBlank()
     */
    public $project;

    /**
     * @var string
     * @ORM\Column(name="zone", type="string")
     * @Assert\NotBlank()
     */
    public $zone;

    /**
     * @var string
     * @ORM\Column(name="machine_type", type="string")
     * @Assert\NotBlank()
     */
    public $machineType;

    /**
     * @var string
     * @ORM\Column(name="snapshot_name", type="string")
     * @Assert\NotBlank()
     */
    public $snapshotName;

    /**
     * @var string
     * @ORM\Column(name="disk_type", type="string")
     * @Assert\NotBlank()
     */
    public $diskType;

    /**
     * @var integer
     * @ORM\Column(name="disk_size", type="integer")
     * @Assert\NotBlank()
     * @Assert\Range(min=10)
     */
    public $diskSize;

    public function getGcAccount()
    {
        return $this->gcAccount;
    }

    public function setGcAccount(GoogleCloudAccount $gcAccount)
    {
        $this->gcAccount = $gcAccount;
        return $this;
    }

    public function getProject()
    {
        return $this->project;
    }

    public function setProject($project)
    {
        $this->project = $project;
    }

    public function getZone()
    {
        return $this->zone;
    }

    public function setZone($zone)
    {
        $this->zone = $zone;
        return $this;
    }

    public function getMachineType()
    {
        return $this->machineType;
    }

    public function setMachineType($machineType)
    {
        $this->machineType = $machineType;
        return $this;
    }

    public function getSnapshotName()
    {
        return $this->snapshotName;
    }

    public function setSnapshotName($snapshotName)
    {
        $this->snapshotName = $snapshotName;
        return $this;
    }

    public function getDiskType()
    {
        return $this->diskType;
    }

    public function setDiskType($diskType)
    {
        $this->diskType = $diskType;
        return $this;
    }

    public function getDiskSize()
    {
        return $this->diskSize;
    }

    public function setDiskSize($diskSize)
    {
        $this->diskSize = $diskSize;
        return $this;
    }


}