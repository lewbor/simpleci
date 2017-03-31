<?php

namespace AppBundle\Frontend\Build;

use AppBundle\Model\Entity\Build;
use AppBundle\Model\Entity\Project;
use AppBundle\Model\Entity\Settings\Repository\GithubRepository;
use AppBundle\Model\Entity\Settings\Repository\GitlabRepository;
use AppBundle\Model\Service\Centrifugo;
use AppBundle\Model\Type\RepositoryType;
use AppBundle\System\Controller\ControllerHelper;
use AppBundle\System\Service\FlashHelper;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Templating\EngineInterface;
use Symfony\Component\Form\FormFactoryInterface;
use Symfony\Component\HttpFoundation\RedirectResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpKernel\HttpKernelInterface;
use Symfony\Component\Routing\RouterInterface;

class BuildController
{
    protected $formFactory;
    protected $router;
    protected $flashHelper;
    protected $templating;
    protected $em;
    protected $httpKernel;
    private $centrifugo;

    public function __construct(
        EngineInterface $templating,
        FormFactoryInterface $formFactory,
        RouterInterface $router,
        EntityManagerInterface $em,
        FlashHelper $flashHelper,
        Centrifugo $centrifugo,
        HttpKernelInterface $httpKernel)
    {
        $this->formFactory = $formFactory;
        $this->router = $router;
        $this->flashHelper = $flashHelper;
        $this->templating = $templating;
        $this->em = $em;
        $this->centrifugo = $centrifugo;
        $this->httpKernel = $httpKernel;
    }


    public function viewAction($id)
    {
        $build = $this->em->getRepository(Build::class)->find($id);
        ControllerHelper::checkEntityExists($build);

        return $this->templating->renderResponse('frontend/build/view.html.twig', [
            'entity' => $build,
            'project' => $build->getProject()
        ]);
    }

    public function createAction(Request $request, $id)
    {
        /** @var Project $project */
        $project = $this->em->getRepository(Project::class)->find($id);
        ControllerHelper::checkEntityExists($project);

        $repository = $project->getRepository();
        $repositoryType = get_class($repository);
        switch ($repositoryType) {
            case GithubRepository::class:
                return ControllerHelper::forward($request, $this->httpKernel, 'controller.build.github:createAction', ['id' => $project->getId()]);
            case GitlabRepository::class:
                return ControllerHelper::forward($request, $this->httpKernel, 'controller.build.gitlab:createAction', ['id' => $project->getId()]);
            default:
                throw new \LogicException(sprintf("Unknown project type: %s", $project->getRepositoryType()));

        }
    }

    public function deleteAction(Request $request, $id) {
        $build = $this->em->getRepository(Build::class)->find($id);
        $project = $build->getProject();
        ControllerHelper::checkEntityExists($build);

        $this->em->remove($build);
        $this->em->flush();
        $this->flashHelper->setFlash(FlashHelper::SUCCESS, 'Build successfully deleted');

        return new RedirectResponse($this->router->generate('project_view', ['id' => $project->getId()]));
    }

}