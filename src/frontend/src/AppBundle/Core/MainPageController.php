<?php

namespace AppBundle\Core;

use AppBundle\Model\Entity\Job;
use AppBundle\System\Controller\ControllerHelper;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Templating\EngineInterface;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpKernel\HttpKernelInterface;
use Symfony\Component\HttpKernel\Tests\Controller;
use Symfony\Component\Routing\RouterInterface;

class MainPageController
{
    private $em;
    private $router;
    private $templating;
    private $httpKernel;

    public function __construct(
        EngineInterface $templating,
        EntityManagerInterface $em,
        RouterInterface $router,
        HttpKernelInterface $httpKernel)
    {
        $this->templating = $templating;
        $this->em = $em;
        $this->router = $router;
        $this->httpKernel = $httpKernel;
    }

    public function indexAction(Request $request)
    {
        $repository = $this->em->getRepository(Job::class);
        $lastJob = $repository->latestJob();

        if ($lastJob !== null) {
            $project = $lastJob->getBuild()->getProject();

            return ControllerHelper::forward($request, $this->httpKernel, 'controller.project:viewAction', ['id' => $project->getId()]);
        }

        return $this->templating->renderResponse('main_page/index.html.twig');

    }


}
