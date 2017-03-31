<?php

namespace AppBundle\Model\Type;


use AppBundle\System\Enum\Enum;

class OperationStatus extends Enum
{
    const PENDING = 'pending';
    const RUNNING = 'running';
    const STOPPED = 'stopped';
    const FINISHED_SUCCESS = 'finished_success';
    const FAILED = 'failed';

    protected static $values = [
        self::PENDING,
        self::RUNNING,
        self::STOPPED,
        self::FINISHED_SUCCESS,
        self::FAILED
    ];
}