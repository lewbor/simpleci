<?php

namespace AppBundle\System\Controller\Crud;


use AppBundle\System\Utils;

class CrudControllerDescription
{
    public $entityClass;
    public $editFormType;
    public $actions = [];

    public $routes = [];
    public $templates = [];

    public function generateRoutes($prefix)
    {
        $section = Utils::className($this->entityClass);
        foreach ($this->actions as $action) {
            $this->routes [$action] = sprintf('%s.%s.%s', $prefix, $section, $action);
        }

    }

    public function generateTemplates($directory)
    {
        $section = Utils::className($this->entityClass);
        foreach ($this->actions as $action) {
            $this->templates[$action] = sprintf('%s/%s/%s.html.twig', $directory, $section, $action);
        }
    }
}