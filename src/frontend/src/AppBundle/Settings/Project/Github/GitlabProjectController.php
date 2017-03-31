<?php

namespace AppBundle\Settings\Project\Github;


use AppBundle\Model\Entity\Project;
use AppBundle\Model\Entity\Settings\Repository\GithubRepository;
use AppBundle\System\Api\GithubApi;
use AppBundle\System\Service\FlashHelper;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Templating\EngineInterface;
use Symfony\Component\Form\FormFactoryInterface;
use Symfony\Component\HttpFoundation\RedirectResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpKernel\Exception\NotFoundHttpException;
use Symfony\Component\Routing\RouterInterface;

class GitlabProjectController
{
    const CREATE_TEMPLATE = 'settings/project/create/create_github.html.twig';

    protected $container;
    protected $formFactory;
    protected $router;
    protected $templating;
    protected $em;
    protected $githubService;
    protected $flashHelper;

    public function __construct(
        EngineInterface $templating,
        FormFactoryInterface $formFactory,
        RouterInterface $router,
        EntityManagerInterface $em,
        FlashHelper $flashHelper)
    {
        $this->router = $router;
        $this->templating = $templating;
        $this->formFactory = $formFactory;
        $this->em = $em;
        $this->flashHelper = $flashHelper;
    }


    public function createAction(Request $request, $id)
    {
        $repository = $this->em->getRepository(GithubRepository::class)
            ->find($id);
        if ($repository === null) {
            throw new NotFoundHttpException();
        }
        $api = GithubApi::createApi($repository);

        $form = $this->formFactory->create(GithubProjectForm::class, ['repository' => $repository], ['api' => $api]);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid() ) {
            try {
                $project = $this->createProject($api, $repository, $form->getData());
                $this->em->persist($project);
                $this->em->flush();
                $this->flashHelper->setFlash(FlashHelper::SUCCESS, 'Project successfully created');

                return new RedirectResponse($this->router->generate('settings.project.list'));
            } catch(\Exception $e ) {
                $this->flashHelper->setFlash(FlashHelper::ERROR, sprintf('Error create project: %s', $e->getMessage()));
            }
        }

        return $this->templating->renderResponse(self::CREATE_TEMPLATE, [
            'form' => $form->createView()]);
    }

    private function createProject(GithubApi $api, GithubRepository $repository, array $formData)
    {
        $project = new Project();
        $project
            ->setName($formData['name'])
            ->setDescription($formData['description'])
            ->setRepositoryUrl($formData['repository_url'])
            ->setServerIdentity($formData['project_identity'])
            ->setRepository($repository)
            ->setSshKey($formData['sshKey'])
            ->setCache($formData['cache']);

        if ($formData['add_deploy_key']) {
            $api->addDeployKeyToRepository($project->getServerIdentity(),
                'simpleci_deploy', $project->getSshKey()->getPublicKey());
        }

        return $project;
    }


}