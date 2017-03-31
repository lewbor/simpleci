<?php

namespace AppBundle\Settings\User;


use AppBundle\Model\Entity\User;
use AppBundle\System\Controller\Crud\CrudController;
use AppBundle\System\Controller\Crud\CrudControllerDescription;
use FOS\UserBundle\Model\UserManagerInterface;

class UserController extends CrudController
{
    private $userManager;
    public function setUserManager(UserManagerInterface $userManager) {
        $this->userManager = $userManager;
    }

    protected function getDescription()
    {
        $description = new CrudControllerDescription();
        $description->entityClass = User::class;
        $description->editFormType = UserForm::class;
        $description->actions = [
            'list',
            'edit',
            'create'];

        $description->generateRoutes('settings');
        $description->generateTemplates('settings');
        return $description;
    }

    protected function createNew()
    {
        return new User();
    }

    protected function editFormOptions()
    {
        return ['user_manager' => $this->userManager];
    }
}