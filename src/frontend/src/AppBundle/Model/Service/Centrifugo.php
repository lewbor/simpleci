<?php

namespace AppBundle\Model\Service;


use Symfony\Component\Security\Core\Authentication\Token\Storage\TokenStorageInterface;

class Centrifugo
{
    private $tokenStorage;
    private $centrifugoSecret;

    public function __construct(TokenStorageInterface $tokenStorage, $centrifugoSecret)
    {
        $this->tokenStorage = $tokenStorage;
        $this->centrifugoSecret = $centrifugoSecret;
    }

    public function clientRequisites()
    {
        $user = $this->tokenStorage->getToken()->getUser();
        $timestamp = (string)time();
        $token = $this->generateTokenSign($this->centrifugoSecret, $user->getId(), $timestamp);

        return [
            'user_id' => (string)$user->getId(),
            'timestamp' => $timestamp,
            'token' => $token];
    }

    public function channel($client, $channel, $info = '')
    {
        return [
            'sign' => $this->generateChannelSign($this->centrifugoSecret, $client, $channel, $info),
            'info' => $info];
    }

    private function generateTokenSign($secret, $user, $timestamp)
    {
        $ctx = hash_init('sha256', HASH_HMAC, $secret);
        hash_update($ctx, (string)$user);
        hash_update($ctx, (string)$timestamp);
        return hash_final($ctx);
    }

    private function generateChannelSign($secret, $user, $channel, $info = '')
    {
        $ctx = hash_init('sha256', HASH_HMAC, $secret);
        hash_update($ctx, (string)$user);
        hash_update($ctx, (string)$channel);
        hash_update($ctx, (string)$info);
        return hash_final($ctx);
    }
}