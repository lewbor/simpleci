<?php

namespace AppBundle\System\Enum;

use AppBundle\System\Utils;
use Symfony\Component\Translation\TranslatorInterface;

class EnumTranslator
{
    private $translator;

    public function __construct(TranslatorInterface $translator)
    {
        $this->translator = $translator;
    }

    public function translate($enum, $choice)
    {
        $enumName = Utils::className($enum);
        return $this->translator->trans(sprintf('enum.%s.%s', $enumName, $choice), [], 'messages');
    }

}
