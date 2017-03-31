<?php

namespace AppBundle\System\Enum;

use Doctrine\DBAL\Platforms\MySqlPlatform;
use Doctrine\DBAL\Platforms\PostgreSqlPlatform;
use Doctrine\DBAL\Platforms\SqlitePlatform;
use Doctrine\DBAL\Types\Type;
use Doctrine\DBAL\Platforms\AbstractPlatform;

class Enum extends Type
{
    protected static $values;

    public function getName()
    {
        return (new \ReflectionClass(get_called_class()))->getShortName();
    }

    public static function getValues()
    {
        return static::$values;
    }

    public static function hasValue($value)
    {
        return array_key_exists($value, static::$values);
    }

    /**
     * {@inheritdoc}
     */
    public function requiresSQLCommentHint(AbstractPlatform $platform)
    {
        return true;
    }

    public function getSqlDeclaration(array $fieldDeclaration, AbstractPlatform $platform)
    {
        $values = implode(
            ", ",
            array_map(function ($val) {
                return "'" . $val . "'";
            }, static::getValues()));

        if ($platform instanceof MysqlPlatform) {
            return sprintf('ENUM(%s)', $values);
        } elseif ($platform instanceof SqlitePlatform) {
            return sprintf('TEXT CHECK(%s IN (%s))', $fieldDeclaration['name'], $values);
        } elseif ($platform instanceof PostgreSqlPlatform) {
            return sprintf('VARCHAR(255) CHECK(%s IN (%s))', $fieldDeclaration['name'], $values);
        } else {
            throw new \Exception(sprintf("Sorry, platform %s currently not supported enums", $platform->getName()));
        }

    }

    public function convertToPHPValue($value, AbstractPlatform $platform)
    {
        return $value;
    }

    public function convertToDatabaseValue($value, AbstractPlatform $platform)
    {
        return $value;
    }


}
