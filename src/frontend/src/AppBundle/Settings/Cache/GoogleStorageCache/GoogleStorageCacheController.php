<?php

namespace AppBundle\Settings\Cache\GoogleStorageCache;


use AppBundle\Model\Entity\Settings\Cache\GoogleStorageCache;
use AppBundle\System\Controller\Crud\CrudController;
use AppBundle\System\Controller\Crud\CrudControllerDescription;

class GoogleStorageCacheController extends CrudController
{

    protected function getDescription()
    {
        $description = new CrudControllerDescription();
        $description->entityClass = GoogleStorageCache::class;
        $description->editFormType = GoogleStorageCacheForm::class;

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
        return new GoogleStorageCache();
    }
}