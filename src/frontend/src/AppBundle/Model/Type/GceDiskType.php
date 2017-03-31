<?php

namespace AppBundle\Model\Type;


use AppBundle\System\Enum\Enum;

class GceDiskType extends Enum
{

    const PD_STANDART = 'pd-standard';
    const PD_SSD = 'pd-ssd';

    protected static $values = [
        self::PD_STANDART,
        self::PD_SSD
    ];
}