<?php

namespace AppBundle\Core;


use AppBundle\Model\Service\Centrifugo;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;

class CentrifugoController
{
    private $centrifugo;

    public function __construct(Centrifugo $centrifugo) {
        $this->centrifugo = $centrifugo;
    }

    public function authAction(Request $request)
    {
        //todo validate acess rights to channel
        $data = json_decode($request->getContent(), true);
        $client = $data['client'];
        $channels = $data['channels'];

        $response = [];
        foreach ($channels as $channel) {
            $response[$channel] = $this->centrifugo->channel($client, $channel);
        }
        return new JsonResponse($response);
    }



}