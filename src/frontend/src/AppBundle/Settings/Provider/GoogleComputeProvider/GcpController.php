<?php

namespace AppBundle\Settings\Provider\GoogleComputeProvider;


use AppBundle\Model\Entity\Settings\Provider\GoogleComputeProvider;
use AppBundle\Model\Type\GceDiskType;
use AppBundle\System\Controller\Crud\CrudController;
use AppBundle\System\Controller\Crud\CrudControllerDescription;

class GcpController extends CrudController
{

    protected function getDescription()
    {
        $description = new CrudControllerDescription();
        $description->entityClass = GoogleComputeProvider::class;
        $description->editFormType = GcpForm::class;
        $description->actions = [
            'list',
            'edit',
            'create'];

        $description->generateRoutes('settings');
        $description->generateTemplates('settings/provider');
        return $description;
    }

    protected function createNew()
    {
        return (new GoogleComputeProvider())
            ->setDiskType(GceDiskType::PD_SSD)
            ->setDiskSize(10);
    }
}