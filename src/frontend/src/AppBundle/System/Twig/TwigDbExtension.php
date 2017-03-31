<?php

namespace AppBundle\System\Twig;

use AppBundle\ScienceActivity\Service\AuthorFormatter;
use AppBundle\ScienceActivity\Service\PublicationService;
use Doctrine\ORM\EntityManager;
use Pa\RadBundle\Enums\Enum;
use AppBundle\Model\Entity\Publication\Publication;
use AppBundle\Model\Type\PublicationType;
use AppBundle\System\Utils\ReflectionUtils;
use Symfony\Component\Routing\RouterInterface;
use Symfony\Component\Translation\TranslatorInterface;

class TwigDbExtension extends \Twig_Extension
{
    private $em;

    public function __construct(EntityManager $em)
    {
        $this->em = $em;
    }

    public function getFilters()
    {
        return array(
            new \Twig_SimpleFilter('persisted', array($this, 'isEntityPersisted')),
        );
    }

    public function isEntityPersisted($entity)
    {
        return \Doctrine\ORM\UnitOfWork::STATE_MANAGED === $this->em->getUnitOfWork()->getEntityState($entity);
    }

    public function getName()
    {
        return 'system_extension';
    }

}
