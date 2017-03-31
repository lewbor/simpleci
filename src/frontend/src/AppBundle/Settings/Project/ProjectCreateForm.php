<?php

namespace AppBundle\Settings\Project;


use AppBundle\Model\Entity\Settings\Cache\Cache;
use AppBundle\Model\Entity\Settings\Provider\Provider;
use AppBundle\Model\Entity\Settings\Repository\Repository;
use AppBundle\Model\Entity\Settings\SshKey;
use Gitlab\Model\Project;
use Symfony\Bridge\Doctrine\Form\Type\EntityType;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;

class ProjectCreateForm extends AbstractType
{

    public function buildForm(FormBuilderInterface $builder, array $options)
    {
        $builder
            ->add('sshKey', EntityType::class, ['class' => SshKey::class, 'choice_label' => 'name', 'required' => false])
            ->add('cache', EntityType::class, ['class' => Cache::class, 'choice_label' => 'name', 'required' => false])
            ->add('provider', EntityType::class, ['class' => Provider::class, 'choice_label' => 'name', 'required' => false]);
    }

}