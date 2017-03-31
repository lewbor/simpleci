<?php

namespace AppBundle\Model\Type;

use AppBundle\System\Enum\Enum;

class CacheType extends Enum
{
    const NONE = 'none';
    const SSH = 'ssh';
    const S3 = 's3';
    const GOOGLE_STORAGE = 'gs';

    protected static $values = [
        self::NONE,
        self::SSH,
        self::S3,
        self::GOOGLE_STORAGE
    ];
}