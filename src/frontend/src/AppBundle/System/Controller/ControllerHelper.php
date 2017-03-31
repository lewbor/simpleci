<?php

namespace AppBundle\System\Controller;


use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpKernel\Exception\NotFoundHttpException;
use Symfony\Component\HttpKernel\HttpKernelInterface;

class ControllerHelper
{

    public static function checkEntityExists($object)
    {
        if (null === $object) {
            throw new NotFoundHttpException();
        }
        return $object;
    }


    public static function forward(Request $request, HttpKernelInterface $httpKernel, $controller, array $path = [], array $query = [])
    {
        $path['_controller'] = $controller;
        $subRequest = $request->duplicate($query, null, $path);

        return $httpKernel->handle($subRequest, HttpKernelInterface::SUB_REQUEST);
    }


}