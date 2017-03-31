<?php


namespace AppBundle\Frontend\Project;

use AppBundle\Model\Entity\Build;
use AppBundle\Model\Entity\Project;
use AppBundle\Model\Entity\Settings\Repository\GithubRepository;
use AppBundle\Model\Entity\Settings\Repository\GitlabRepository;
use AppBundle\System\Controller\ControllerHelper;
use AppBundle\System\Service\FlashHelper;
use Doctrine\ORM\EntityManagerInterface;
use Knp\Component\Pager\PaginatorInterface;
use Symfony\Bundle\FrameworkBundle\Templating\EngineInterface;
use Symfony\Component\Form\FormFactoryInterface;
use Symfony\Component\HttpFoundation\RedirectResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Routing\RouterInterface;

class ProjectController
{
    protected $formFactory;
    protected $router;
    protected $templating;
    protected $em;
    protected $flashHelper;
    protected $paginator;
    protected $hostName;

    public function __construct(
        EngineInterface $templating,
        FormFactoryInterface $formFactory,
        RouterInterface $router,
        EntityManagerInterface $em,
        FlashHelper $flashHelper,
        PaginatorInterface $paginator,
        $hostName)
    {
        $this->router = $router;
        $this->templating = $templating;
        $this->formFactory = $formFactory;
        $this->em = $em;
        $this->flashHelper = $flashHelper;
        $this->paginator = $paginator;
        $this->hostName = $hostName;
    }

    public function viewAction(Request $request, $id)
    {
        $project = $this->em->getRepository(Project::class)->find($id);
        ControllerHelper::checkEntityExists($project);

        $buildRepository = $this->em->getRepository(Build::class);
        $buildsQuery = $buildRepository->projectBuildsQuery($project);
        $pagination = $this->paginator->paginate(
            $buildsQuery,
            $request->query->getInt('page', 1), 10);

        return $this->templating->renderResponse('frontend/project/view.html.twig', [
            'entityList' => $pagination,
            'project' => $project,
            'pushUrl' => sprintf('http://%s%s', $this->hostName, $this->router->generate('project_hook', ['id' => $project->getId()]))
        ]);
    }



}