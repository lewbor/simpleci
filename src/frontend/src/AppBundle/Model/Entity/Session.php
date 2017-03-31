<?php

namespace AppBundle\Model\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * Class to represent php session store table
 * @ORM\Entity()
 * @ORM\Table(name="sessions")
 */
class Session
{
    /**
     * @var string
     * @ORM\Id
     * @ORM\Column(name="sess_id", type="string")
     */
    protected $id;

    /**
     * @var string
     * @ORM\Column(name="sess_data", type="blob", nullable=false)
     */
    protected $data;

    /**
     * @var string
     * @ORM\Column(name="sess_time", type="integer", nullable=false)
     */
    protected $time;

    /**
     * @var string
     * @ORM\Column(name="sess_lifetime", type="integer", nullable=false)
     */
    protected $lifetime;

}