<?php

namespace AppBundle\Settings\Account\GoogleCloudAccount;


use AppBundle\Model\Entity\Settings\GoogleCloudAccount;
use AppBundle\System\Controller\Crud\CrudController;
use AppBundle\System\Controller\Crud\CrudControllerDescription;

class GcAccountController extends CrudController
{

    protected function getDescription()
    {
        $description = new CrudControllerDescription();
        $description->entityClass = GoogleCloudAccount::class;
        $description->editFormType = GcAccountForm::class;
        $description->actions = [
            'list',
            'view',
            'edit',
            'create'];
        $description->generateRoutes('settings');
        $description->generateTemplates('settings/account');
        return $description;
    }

    protected function createNew()
    {
        return new GoogleCloudAccount();
    }
}