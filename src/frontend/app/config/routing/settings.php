<?php

use AppBundle\System\Utils;
use Symfony\Component\Routing\RouteCollection;
use Symfony\Component\Routing\Route;

$settingsClasses = [
    \AppBundle\Model\Entity\Settings\SshKey::class,
    \AppBundle\Model\Entity\Settings\GoogleCloudAccount::class,
    \AppBundle\Model\Entity\Settings\Provider\GoogleComputeProvider::class,
    \AppBundle\Model\Entity\Settings\Cache\GoogleStorageCache::class,
    \AppBundle\Model\Entity\Settings\Cache\S3Cache::class,
    \AppBundle\Model\Entity\Settings\Repository\GitlabRepository::class,
    \AppBundle\Model\Entity\Settings\Repository\GithubRepository::class,
    \Gitlab\Model\Project::class,
    \AppBundle\Model\Entity\User::class,
];

$collection = new RouteCollection();

foreach ($settingsClasses as $settingsClass) {
    $item = Utils::className($settingsClass);

    $collection->add(sprintf('settings.%s.list', $item), new Route(sprintf('/settings/%s', $item), array(
        '_controller' => sprintf('controller.settings.%s:listAction', $item)
    )));
    $collection->add(sprintf('settings.%s.create', $item), new Route(sprintf('/settings/%s/create', $item), array(
        '_controller' => sprintf('controller.settings.%s:createAction', $item)
    )));
    $collection->add(sprintf('settings.%s.edit', $item), new Route(sprintf('/settings/%s/{id}/edit', $item), array(
        '_controller' => sprintf('controller.settings.%s:editAction', $item)
    )));
    $collection->add(sprintf('settings.%s.view', $item), new Route(sprintf('/settings/%s/{id}', $item), array(
        '_controller' => sprintf('controller.settings.%s:viewAction', $item)
    )));

}


return $collection;
