<?php

namespace AppBundle\Settings\Repository\Gitlab;


use AppBundle\Model\Entity\Settings\Repository\GitlabRepository;
use AppBundle\System\Controller\Crud\CrudController;
use AppBundle\System\Controller\Crud\CrudControllerDescription;

class GitlabController extends CrudController
{

    protected function getDescription()
    {
        $description = new CrudControllerDescription();
        $description->entityClass = GitlabRepository::class;
        $description->editFormType = GitlabForm::class;
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
        return (new GitlabRepository());
    }
}