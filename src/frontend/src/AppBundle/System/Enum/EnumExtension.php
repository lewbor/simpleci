<?php

namespace AppBundle\System\Enum;

class EnumExtension extends \Twig_Extension
{
    private $translator;

    public function __construct(EnumTranslator $translator)
    {
        $this->translator = $translator;
    }

    public function getFilters()
    {
        return array(
            new \Twig_SimpleFilter('translate_enum', array($this, 'translate')),
        );
    }

    public function translate($enumValue, $enumType, $translationDomain = null)
    {
        return $this->translator->translate($enumType, $enumValue, $translationDomain);
    }

    public function getName()
    {
        return 'enum_extension';
    }
}
