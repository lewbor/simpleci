<?php

namespace AppBundle\System\Enum;

use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\ChoiceType;
use Symfony\Component\OptionsResolver\Options;
use Symfony\Component\OptionsResolver\OptionsResolver;

class EnumType extends AbstractType
{
    private $translator;

    public function __construct(EnumTranslator $translator)
    {
        $this->translator = $translator;
    }

    public function configureOptions(OptionsResolver $resolver)
    {
        $choiceBuilder = function (Options $options) {
            /** @var Enum $enum */
            $enum = $options['type'];
            $values = $enum::getValues();
            $choices = [];
            for ($i = 0; $i < count($values); $i++) {
                $choices[$values[$i]] = $this->translator->translate($enum, $values[$i], $options['translation_domain']);
            }
            return array_flip($choices);
        };


        $resolver
            ->setDefaults([
                'translation_domain' => 'enums',
                'choices' => $choiceBuilder,
                'choices_as_values' => true])
            ->setRequired(['type']);

    }

    public function getParent()
    {
        return ChoiceType::class;
    }
}
