<?php

namespace AppBundle\Frontend\Job;

use AppBundle\Model\Entity\Job;
use AppBundle\Model\Service\Centrifugo;
use AppBundle\System\Controller\ControllerHelper;
use AppBundle\System\Service\FlashHelper;
use Doctrine\ORM\EntityManagerInterface;
use OldSound\RabbitMqBundle\RabbitMq\Producer;
use Symfony\Bundle\FrameworkBundle\Templating\EngineInterface;
use Symfony\Component\Form\FormFactoryInterface;
use Symfony\Component\HttpFoundation\RedirectResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpKernel\HttpKernelInterface;
use Symfony\Component\Routing\RouterInterface;

class JobController
{
    protected $formFactory;
    protected $router;
    protected $flashHelper;
    protected $templating;
    protected $em;
    protected $httpKernel;
    private $centrifugo;
    private $producer;

    public function __construct(
        EngineInterface $templating,
        FormFactoryInterface $formFactory,
        RouterInterface $router,
        EntityManagerInterface $em,
        FlashHelper $flashHelper,
        Centrifugo $centrifugo,
        Producer $producer,
        HttpKernelInterface $httpKernel)
    {
        $this->formFactory = $formFactory;
        $this->router = $router;
        $this->flashHelper = $flashHelper;
        $this->templating = $templating;
        $this->em = $em;
        $this->centrifugo = $centrifugo;
        $this->httpKernel = $httpKernel;
        $this->producer = $producer;
    }


    public function viewAction($id)
    {
        $job = $this->em->getRepository(Job::class)->find($id);
        ControllerHelper::checkEntityExists($job);

        return $this->templating->renderResponse('frontend/job/view.html.twig', [
            'entity' => $job]);
    }


    public function logAction($id)
    {
        /** @var \AppBundle\Model\Entity\Job $job */
        $job = $this->em->getRepository(Job::class)->find($id);
        ControllerHelper::checkEntityExists($job);

        return new Response($job->getLog());
    }

    public function stopAction(Request $request, $id)
    {
        /** @var \AppBundle\Model\Entity\Job $job */
        $job = $this->em->getRepository(Job::class)->find($id);
        ControllerHelper::checkEntityExists($job);

        $message = ['type' => 'job_stop_request', 'job_id' => $job->getId()];
        $this->producer->publish(json_encode($message));

        return new RedirectResponse($request->headers->get('referer'));
    }


}