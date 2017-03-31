<?php


namespace AppBundle\Model\Service;


use Symfony\Component\Process\Process;

class SshKeysGenerator {

    /**
     * @param string $user
     * @param string $host
     * @return array ['publicKey' => '...', 'privateKey' => '...']
     */
    public function generateKeyPair($user = 'simpleci', $host = 'simpleci')
    {
        $tempPath = sys_get_temp_dir() . '/';
        $keyFile = $tempPath . md5(microtime(true));
        if (!is_dir($tempPath)) {
            mkdir($tempPath);
        }
        $shellCommand = sprintf('ssh-keygen -q -t rsa -b 2048 -f %s -N "" -C "%s@%s"', $keyFile, $user, $host);
        $process = new Process($shellCommand);
        $process->run();
        if (!$process->isSuccessful()) {
            throw new \RuntimeException($process->getErrorOutput());
        }
        $pub = file_get_contents($keyFile . '.pub');
        $prv = file_get_contents($keyFile);
        return ['publicKey' => $pub, 'privateKey' => $prv];
    }
}