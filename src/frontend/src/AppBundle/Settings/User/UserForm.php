<?php

namespace AppBundle\Settings\User;


use AppBundle\Model\Entity\User;
use FOS\UserBundle\Doctrine\UserManager;
use FOS\UserBundle\Model\UserManagerInterface;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\CheckboxType;
use Symfony\Component\Form\Extension\Core\Type\TextType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\Form\FormEvent;
use Symfony\Component\Form\FormEvents;
use Symfony\Component\OptionsResolver\OptionsResolver;

class UserForm extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options)
    {
        /** @var UserManagerInterface $userManager */
        $userManager = $options['user_manager'];
        /** @var User $user **/
        $user = $options['data'];

        $builder
            ->add('username', TextType::class)
            ->add('email', TextType::class)
            ->add('plainPassword', TextType::class, ['required' => $user == null || $user->getId() == null ])
            ->add('enabled', CheckboxType::class, ['required' => false]);

        $builder->addEventListener(FormEvents::SUBMIT, function (FormEvent $event) use($userManager) {
            $user = $event->getForm()->getData();
            $userManager->updateCanonicalFields($user);
            $userManager->updatePassword($user);
        });
    }

    public function configureOptions(OptionsResolver $resolver)
    {
        $resolver
            ->setRequired('user_manager')
            ->setAllowedTypes('user_manager', UserManagerInterface::class)
            ->setDefaults(['data_class' => User::class]);
    }
}