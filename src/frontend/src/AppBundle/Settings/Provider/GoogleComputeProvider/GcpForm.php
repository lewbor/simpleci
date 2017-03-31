<?php

namespace AppBundle\Settings\Provider\GoogleComputeProvider;


use AppBundle\Model\Entity\Settings\GoogleCloudAccount;
use AppBundle\Model\Entity\Settings\Provider\GoogleComputeProvider;
use AppBundle\Model\Type\GceDiskType;
use AppBundle\Model\Type\GceMachineType;
use AppBundle\System\Enum\EnumType;
use Symfony\Bridge\Doctrine\Form\Type\EntityType;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\IntegerType;
use Symfony\Component\Form\Extension\Core\Type\TextType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;

class GcpForm extends AbstractType
{

    public function buildForm(FormBuilderInterface $builder, array $options)
    {
        $builder
            ->add('gcAccount', EntityType::class, ['class' => GoogleCloudAccount::class, 'choice_label' => 'name'])
            ->add('name', TextType::class)
            ->add('project', TextType::class)
            ->add('zone', TextType::class)
            ->add('machineType', EnumType::class, ['type' => GceMachineType::class])
            ->add('snapshotName', TextType::class)
            ->add('diskType', EnumType::class, ['type' => GceDiskType::class])
            ->add('diskSize', IntegerType::class);
    }

    public function configureOptions(OptionsResolver $resolver)
    {
        $resolver->setDefaults(['data_class' => GoogleComputeProvider::class]);
    }

}