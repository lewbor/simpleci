<?php

namespace AppBundle\Model\Type;

use AppBundle\System\Enum\Enum;

class RepositoryType extends Enum
{
    const GITLAB = 'gitlab';
    const GITHUB = 'github';

    protected static $values = [
        self::GITLAB,
        self::GITHUB
    ];
}