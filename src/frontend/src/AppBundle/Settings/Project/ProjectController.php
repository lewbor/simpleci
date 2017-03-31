<?php

namespace AppBundle\Settings\Project;


use AppBundle\Model\Entity\Project;
use AppBundle\Model\Entity\Settings\Repository\GithubRepository;
use AppBundle\Model\Entity\Settings\Repository\GitlabRepository;
use AppBundle\System\Controller\ControllerHelper;
use AppBundle\System\Controller\Crud\CrudController;
use AppBundle\System\Controller\Crud\CrudControllerDescription;
use AppBundle\System\Service\FlashHelper;
use Symfony\Component\HttpFoundation\RedirectResponse;
use Symfony\Component\HttpFoundation\Request;

class ProjectController extends CrudController
{

    protected function getDescription()
    {
        $description = new CrudControllerDescription();
        $description->entityClass = Project::class;
        $description->editFormType = ProjectForm::class;
        $description->actions = [
            'list',
            'edit',
        ];

        $description->generateRoutes('settings');
        $description->generateTemplates('settings');
        return $description;
    }

    protected function createNew()
    {
        return new Project();
    }

    public function createAction(Request $request)
    {
        $sources = [];
        $gitlabRepos = $this->em->getRepository(GitlabRepository::class)
            ->findAll();
        if (count($gitlabRepos) > 0) {
            $sources['gitlab'] = $gitlabRepos;
        }

        $githubRepos = $this->em->getRepository(GithubRepository::class)
            ->findAll();
        if (count($githubRepos) > 0) {
            $sources['github'] = $githubRepos;
        }


        return $this->templating->renderResponse('settings/project/create.html.twig', [
            'sources' => $sources]);

    }

    public function removeAction($id)
    {
        $project = $this->em->getRepository(Project::class)->find($id);
        ControllerHelper::checkEntityExists($project);

        $this->em->remove($project);
        $this->em->flush();
        $this->flashHelper->setFlash(FlashHelper::SUCCESS, 'Project successfully removed');

        return new RedirectResponse($this->router->generate('settings.project.list'));
    }
}