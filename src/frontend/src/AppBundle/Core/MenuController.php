<?php

namespace AppBundle\Core;

use AppBundle\Model\Entity\Project;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Templating\EngineInterface;
use Symfony\Component\Routing\RouterInterface;

class MenuController
{
    protected $router;
    protected $templating;
    protected $em;

    public function __construct(
        EngineInterface $templating,
        RouterInterface $router,
        EntityManagerInterface $em)
    {
        $this->router = $router;
        $this->templating = $templating;
        $this->em = $em;
    }

    public function userMenuAction()
    {
        $projects = $this->em->getRepository(Project::class)->findAll();

        return $this->templating->renderResponse('menu/user_menu.html.twig', [
            'projects' => array_map(function (Project $project) {
                return [
                    'id'           => $project->getId(),
                    'name'         => $project->getName(),
                    'last_build'   => $project->getBuilds()->last(),
                    'total_builds' => $project->getBuilds()->count()
                ];
            }, $projects)
        ]);
    }

}