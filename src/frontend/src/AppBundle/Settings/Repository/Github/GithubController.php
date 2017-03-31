<?php

namespace AppBundle\Settings\Repository\Github;


use AppBundle\Model\Entity\Settings\Repository\GithubRepository;
use AppBundle\System\Controller\Crud\CrudController;
use AppBundle\System\Controller\Crud\CrudControllerDescription;

class GithubController extends CrudController
{

    protected function getDescription()
    {
        $description = new CrudControllerDescription();
        $description->entityClass = GithubRepository::class;
        $description->editFormType = GithubForm::class;
        $description->actions = [
            'list',
            'edit',
            'create'];

        $description->generateRoutes('settings');
        $description->generateTemplates('settings/repository');
        return $description;
    }

    protected function createNew()
    {
        return (new GithubRepository())
            ->setUrl("https://github.com");
    }


}