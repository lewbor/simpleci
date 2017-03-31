<?php

namespace AppBundle\System\Controller\Crud;


use AppBundle\System\Service\FlashHelper;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Templating\EngineInterface;
use Symfony\Component\Form\FormFactoryInterface;
use Symfony\Component\HttpFoundation\RedirectResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpKernel\Exception\NotFoundHttpException;
use Symfony\Component\Routing\RouterInterface;

abstract class CrudController
{
    protected $em;
    protected $router;
    protected $templating;
    protected $formFactory;
    protected $flashHelper;

    public function __construct(
        EntityManagerInterface $em,
        EngineInterface $templating,
        RouterInterface $router,
        FormFactoryInterface $formFactory,
        FlashHelper $flashHelper)
    {
        $this->em = $em;
        $this->templating = $templating;
        $this->router = $router;
        $this->formFactory = $formFactory;
        $this->flashHelper = $flashHelper;
    }

    /**
     * @return CrudControllerDescription
     */
    protected abstract function getDescription();

    protected abstract function createNew();

    public function listAction()
    {
        $description = $this->getDescription();
        $sshKeys = $this->em->getRepository($description->entityClass)->findAll();
        return $this->templating->renderResponse($description->templates['list'], [
            'entity_list' => $sshKeys,
            'controllerDescription' => $description
        ]);
    }

    public function createAction(Request $request)
    {
        $description = $this->getDescription();
        $entity = $this->createNew();
        return $this->handleEdit($description, $entity, $request);
    }

    public function editAction(Request $request, $id)
    {
        $description = $this->getDescription();
        $entity = $this->em->getRepository($description->entityClass)->find($id);
        if ($entity === null) {
            throw new NotFoundHttpException();
        }
        return $this->handleEdit($description, $entity, $request);
    }

    public function viewAction(Request $request, $id)
    {
        $description = $this->getDescription();
        $entity = $this->em->getRepository($description->entityClass)->find($id);
        if ($entity === null) {
            throw new NotFoundHttpException();
        }

        return $this->templating->renderResponse($description->templates['view'], [
            'entity' => $entity,
            'controllerDescription' => $description
        ]);
    }

    private function handleEdit(CrudControllerDescription $description, $entity, Request $request)
    {

        $form = $this->formFactory->create($description->editFormType, $entity, $this->editFormOptions());
        $form->handleRequest($request);

        if ($form->isSubmitted()) {
            if ($form->isValid()) {
                $this->em->persist($entity);
                $this->em->flush();

                return new RedirectResponse($this->router->generate($description->routes['list']));
            }
        }

        return $this->templating->renderResponse($description->templates['edit'], [
            'entity' => $entity,
            'form' => $form->createView(),
            'controllerDescription' => $description
        ]);
    }

    protected function editFormOptions()
    {
        return [];
    }

}