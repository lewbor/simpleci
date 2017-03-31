<?php

namespace AppBundle\Settings\Cache\S3Cache;


use AppBundle\Model\Entity\Settings\Cache\S3Cache;
use AppBundle\System\Controller\Crud\CrudController;
use AppBundle\System\Controller\Crud\CrudControllerDescription;

class S3CacheController  extends CrudController
{

    protected function getDescription()
    {
        $description = new CrudControllerDescription();
        $description->entityClass = S3Cache::class;
        $description->editFormType = S3CacheForm::class;

        $description->actions = [
            'list',
            'edit',
            'create'];
        $description->generateRoutes('settings');
        $description->generateTemplates('settings/cache');
        return $description;
    }

    protected function createNew()
    {
        return new S3Cache();
    }
}