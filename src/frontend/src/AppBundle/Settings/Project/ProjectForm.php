<?php

namespace AppBundle\Settings\Project;


use AppBundle\Model\Entity\Project;
use AppBundle\Model\Entity\Settings\Cache\Cache;
use AppBundle\Model\Entity\Settings\Provider\Provider;
use AppBundle\Model\Entity\Settings\Repository\Repository;
use AppBundle\Model\Entity\Settings\SshKey;
use Symfony\Bridge\Doctrine\Form\Type\EntityType;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\TextareaType;
use Symfony\Component\Form\Extension\Core\Type\TextType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;

class ProjectForm extends AbstractType
{

    public function buildForm(FormBuilderInterface $builder, array $options)
    {
        $builder
            ->add('name', TextType::class)
            ->add('description', TextareaType::class, ['required' => false])
            ->add('repositoryUrl', TextType::class, ['required' => true])
            ->add('serverIdentity', TextType::class, ['required' => false, 'label' => 'Project'])
            ->add('repository', EntityType::class, ['required' => false, 'class' => Repository::class, 'choice_label' => 'url'])
            ->add('sshKey', EntityType::class, ['class' => SshKey::class, 'choice_label' => 'name', 'required' => false])
            ->add('cache', EntityType::class, ['class' => Cache::class, 'choice_label' => 'name', 'required' => false])
            ->add('provider', EntityType::class, ['class' => Provider::class, 'choice_label' => 'name', 'required' => false]);
    }

    public function configureOptions(OptionsResolver $resolver)
    {
        $resolver->setDefaults(['data_class' => Project::class]);
    }

}