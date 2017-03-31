<?php

namespace AppBundle\Model\Entity\Settings\Cache;

use Doctrine\ORM\Mapping as ORM;
use Symfony\Bridge\Doctrine\Validator\Constraints as DoctrineConstraint;
use Symfony\Component\Validator\Constraints as Assert;

/**
 * @ORM\Entity()
 */
class S3Cache extends Cache
{
    /**
     * @var string
     * @ORM\Column(name="endpoint", type="string")
     * @Assert\NotBlank()
     */
    protected $endPoint;

    /**
     * @var string
     * @ORM\Column(name="bucket", type="string")
     * @Assert\NotBlank()
     */
    protected $bucket;

    /**
     * @var string
     * @ORM\Column(name="access_key", type="string")
     * @Assert\NotBlank()
     */
    protected $accessKey;

    /**
     * @var string
     * @ORM\Column(name="secret_key", type="string")
     * @Assert\NotBlank()
     */
    protected $secretKey;

    public function getEndPoint()
    {
        return $this->endPoint;
    }

    public function setEndPoint($endPoint)
    {
        $this->endPoint = $endPoint;
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

    public function getAccessKey()
    {
        return $this->accessKey;
    }

    public function setAccessKey($accessKey)
    {
        $this->accessKey = $accessKey;
        return $this;
    }

    public function getSecretKey()
    {
        return $this->secretKey;
    }

    public function setSecretKey($secretKey)
    {
        $this->secretKey = $secretKey;
        return $this;
    }



}