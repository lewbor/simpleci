<?php

namespace AppBundle\Settings\SshKey;


use AppBundle\Model\Entity\Settings\SshKey;
use AppBundle\Model\Service\SshKeysGenerator;
use AppBundle\System\Controller\Crud\CrudController;
use AppBundle\System\Controller\Crud\CrudControllerDescription;

class SshKeyController extends CrudController
{
    /** @var  SshKeysGenerator */
    private $keysGenerator;
    /** @var  string */
    private $hostName;

    public function setKeysGenerator(SshKeysGenerator $keysGenerator)
    {
        $this->keysGenerator = $keysGenerator;
    }

    public function setHostName($hostName)
    {
        $this->hostName = $hostName;
    }

    protected function getDescription()
    {
        $description = new CrudControllerDescription();
        $description->entityClass = SshKey::class;
        $description->editFormType = SshKeyForm::class;
        $description->actions = [
            'list',
            'view',
            'edit',
            'create'];

        $description->generateRoutes('settings');
        $description->generateTemplates('settings');
        return $description;
    }

    protected function createNew()
    {
        $sshKey = $this->keysGenerator->generateKeyPair('simpleci', $this->hostName);

        return (new SshKey())
            ->setCreatedAt(new \DateTime())
            ->setPublicKey($sshKey['publicKey'])
            ->setPrivateKey($sshKey['privateKey']);
    }
}