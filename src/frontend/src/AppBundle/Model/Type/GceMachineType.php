<?php
namespace AppBundle\Model\Type;


use AppBundle\System\Enum\Enum;

class GceMachineType extends Enum
{
    const N1_STANDARD_1 = 'n1-standard-1';
    const N1_STANDARD_2 = 'n1-standard-2';

    protected static $values = [
        self::N1_STANDARD_1,
        self::N1_STANDARD_2
    ];
}