<?php

namespace AppBundle\System;


use Doctrine\ORM\EntityManagerInterface;

class Utils
{
    public static function disableSqlLogger(EntityManagerInterface $em)
    {
        $em->getConnection()->getConfiguration()->setSQLLogger(null);
    }

    public static function className($classFullName) {
        $reflection = new \ReflectionClass($classFullName);
        return self::underscore($reflection->getShortName());
    }

    public static function underscore($id)
    {
        return strtolower(preg_replace(array('/([A-Z]+)([A-Z][a-z])/', '/([a-z\d])([A-Z])/'), array('\\1_\\2', '\\1_\\2'), strtr($id, '_', '.')));
    }
}