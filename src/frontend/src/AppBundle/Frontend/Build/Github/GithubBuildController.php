<?php

namespace AppBundle\Frontend\Build\Github;


use AppBundle\Model\Entity\Project;
use AppBundle\Model\Service\BuildCreator;
use AppBundle\System\Api\GithubApi;
use AppBundle\System\Controller\ControllerHelper;
use AppBundle\System\Service\FlashHelper;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Templating\EngineInterface;
use Symfony\Component\Form\FormFactoryInterface;
use Symfony\Component\HttpFoundation\RedirectResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Routing\RouterInterface;

class GithubBuildController
{
    protected $formFactory;
    protected $router;
    protected $templating;
    protected $em;
    protected $buildCreator;
    protected $flashHelper;

    public function __construct(
        EngineInterface $templating,
        FormFactoryInterface $formFactory,
        RouterInterface $router,
        EntityManagerInterface $em,
        FlashHelper $flashHelper,
        BuildCreator $buildCreator)
    {
        $this->router = $router;
        $this->templating = $templating;
        $this->formFactory = $formFactory;
        $this->em = $em;
        $this->flashHelper = $flashHelper;
        $this->buildCreator = $buildCreator;
    }


    public function createAction(Request $request, $id)
    {
        /** @var Project $project */
        $project = $this->em->getRepository(Project::class)->find($id);
        ControllerHelper::checkEntityExists($project);

        $api = GithubApi::createApi($project->getRepository());
        $form = $this->formFactory->create(GithubBuildForm::class, null, ['api' => $api, 'project' => $project]);
        $form->handleRequest($request);
        $build = null;

        if ($form->isSubmitted() && $form->isValid()) {
            try {
                $buildData = $form->getData();
                $normalizedBuildData = $this->normalizeBuildData($api, $project->getServerIdentity(), $buildData);
                $build = $this->buildCreator->createBuild($project,$normalizedBuildData);
                $this->flashHelper->setFlash(FlashHelper::SUCCESS, 'Build successfully created');

                return new RedirectResponse($this->router->generate('build_view', ['id' => $build->getId()]));
            } catch(\Exception $e) {
                $this->flashHelper->setFlash(FlashHelper::ERROR, sprintf('Error creating build: %s', $e->getMessage()));
            }
        }

        return $this->templating->renderResponse('frontend/build/create_github.html.twig', [
            'form'    => $form->createView(),
            'entity'  => $build,
            'project' => $project]);

    }

    private function normalizeBuildData(GithubApi $api, $serverIdentity, $formData)
    {
        try {
            $commitInfo = $api->commitInfo($serverIdentity, $formData['commit']);
        } catch (\Exception $e) {
            throw new \Exception("Error get commit info from repository", 0, $e);
        }

        try {
            $buildConfig = $api->fetchConfig($serverIdentity, $formData['commit']);
        } catch (\Exception $e) {
            throw new \Exception("Config file .simpleci.yml not found", 0, $e);
        }

        return [
            'commit' => $formData['commit'],
            'commit_range' => sprintf('%s...%s', count($commitInfo['parents']) > 0 ? $commitInfo['parents'][0] : $formData['commit'],  $formData['commit']),
            'branch' => $formData['branch'],
            'author_name' => $commitInfo['author'],
            'author_email' => $commitInfo['author_email'],
            'commit_message' => $commitInfo['message'],
            'commit_date' => new \DateTime($commitInfo['commited_date']),
            'build_config' => $buildConfig
        ];
    }

}