<?php


namespace AppBundle\Model\Entity\Settings\Cache;

use AppBundle\Model\Entity\Settings\GoogleCloudAccount;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert;
use Symfony\Bridge\Doctrine\Validator\Constraints as DoctrineConstraint;

/**
 * @ORM\Entity()
 */
class GoogleStorageCache extends Cache
{
    /**
     * @var GoogleCloudAccount
     * @ORM\ManyToOne(targetEntity="AppBundle\Model\Entity\Settings\GoogleCloudAccount")
     * @ORM\JoinColumn(name="gc_account_id", referencedColumnName="id")
     * @Assert\NotNull()
     **/
    protected $gcAccount;

    /**
     * @var string
     * @ORM\Column(name="bucket", type="string")
     * @Assert\NotBlank()
     */
    protected $bucket;

    public function getGcAccount()
    {
        return $this->gcAccount;
    }

    public function setGcAccount($gcAccount)
    {
        $this->gcAccount = $gcAccount;
        return $this;
    }

    public function getBucket()
    {
        return $this->bucket;
    }

    public function setBucket($bucket)
    {
        $this->bucket = $bucket;
        return $this;
    }


}