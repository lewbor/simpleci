<?php


namespace AppBundle\System\Service;


use Symfony\Component\HttpFoundation\Session\Flash\FlashBag;
use Symfony\Component\HttpFoundation\Session\SessionInterface;
use Symfony\Component\Translation\TranslatorInterface;

/**
 * Class to work with session flash messages
 */
class FlashHelper {
    const ERROR = 'danger';
    const SUCCESS = 'success';

    private $translator;
    private $session;
    private $translationDomain;

    public function __construct(
        TranslatorInterface $translator,
        SessionInterface $session,
        $translationDomain = 'messages')
    {
        $this->translator = $translator;
        $this->session = $session;
        $this->translationDomain = $translationDomain;
    }

    public function setFlash($type, $eventName, $params = array())
    {
        /** @var FlashBag $flashBag */
        $flashBag = $this->session->getBag('flashes');
        $flashBag->add($type, $this->translateFlashMessage($eventName, $params));
    }


    private function translateFlashMessage($message, $params = array())
    {
        return $this->translator->trans($message, $params, $this->translationDomain);
    }

} 