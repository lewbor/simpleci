<?php
namespace AppBundle\Frontend\Security;

use AppBundle\Frontend\Security\ChangePasswordForm;
use AppBundle\Frontend\Security\UserForm;
use AppBundle\System\Service\FlashHelper;
use FOS\UserBundle\Model\UserManagerInterface;
use Symfony\Bundle\FrameworkBundle\Templating\EngineInterface;
use Symfony\Component\Form\FormFactoryInterface;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Security\Core\Authentication\Token\Storage\TokenStorageInterface;


class UserController
{
    private $templating;
    private $formFactory;
    private $tokenStorage;
    private $userManager;
    private $flashHelper;

    public function __construct(
        EngineInterface $templating,
        FormFactoryInterface $formFactory,
        TokenStorageInterface $tokenStorage,
        UserManagerInterface $userManager,
        FlashHelper $flashHelper)
    {
        $this->templating = $templating;
        $this->formFactory = $formFactory;
        $this->tokenStorage = $tokenStorage;
        $this->userManager = $userManager;
        $this->flashHelper = $flashHelper;
    }

    public function profileSettingsAction(Request $request)
    {
        $user = $this->tokenStorage->getToken()->getUser();

        $userForm = $this->formFactory->create(UserForm::class, $user);
        $userForm->handleRequest($request);
        if ($userForm->isSubmitted()) {
            if ($userForm->isValid()) {
                $this->userManager->updateUser($user);
                $this->flashHelper->setFlash(FlashHelper::SUCCESS, 'Profile settings saved');
            }
        }

        $passwordForm = $this->formFactory->create(ChangePasswordForm::class, $user);
        $passwordForm->handleRequest($request);
        if ($passwordForm->isSubmitted()) {
            if ($passwordForm->isValid()) {
                $this->userManager->updateUser($user);
                $this->flashHelper->setFlash(FlashHelper::SUCCESS, 'Password updated');
            }
        }

        return $this->templating->renderResponse('user/profile_settings.html.twig', [
            'user_form'     => $userForm->createView(),
            'password_form' => $passwordForm->createView()
        ]);
    }


}