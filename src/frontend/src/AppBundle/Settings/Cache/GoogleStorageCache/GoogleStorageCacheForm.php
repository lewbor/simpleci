<?php

namespace AppBundle\Settings\Cache\GoogleStorageCache;


use AppBundle\Model\Entity\Settings\Cache\GoogleStorageCache;
use AppBundle\Model\Entity\Settings\GoogleCloudAccount;
use Symfony\Bridge\Doctrine\Form\Type\EntityType;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\TextType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;

class GoogleStorageCacheForm extends AbstractType
{

    public function buildForm(FormBuilderInterface $builder, array $options)
    {
        $builder
            ->add('name', TextType::class)
            ->add('gcAccount', EntityType::class, ['class' => GoogleCloudAccount::class, 'choice_label' => 'name'])
            ->add('bucket', TextType::class);

    }

    public function configureOptions(OptionsResolver $resolver)
    {
        $resolver->setDefaults(['data_class' => GoogleStorageCache::class]);
    }

}