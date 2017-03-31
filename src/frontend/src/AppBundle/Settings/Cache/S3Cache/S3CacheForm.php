<?php

namespace AppBundle\Settings\Cache\S3Cache;


use AppBundle\Model\Entity\Settings\Cache\S3Cache;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\TextType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;

class S3CacheForm extends AbstractType
{

    public function buildForm(FormBuilderInterface $builder, array $options)
    {
        $builder
            ->add('name', TextType::class)
            ->add('endpoint', TextType::class)
            ->add('bucket', TextType::class)
            ->add('accessKey', TextType::class)
            ->add('secretKey', TextType::class);

    }

    public function configureOptions(OptionsResolver $resolver)
    {
        $resolver->setDefaults(['data_class' => S3Cache::class]);
    }

}