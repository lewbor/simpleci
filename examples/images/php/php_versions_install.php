<?php

class PhpInstaller
{
    private static $VARIANTS;
    private static $EXTENSIONS;
    private static $PHP_7_EXTENSIONS;
    private static $OPTIONS;
    private static $VERSIONS;

    public function __construct()
    {
        self::$VARIANTS = [
            'filter',
            'dom',
            'bcmath',
            'ctype',
            'mhash',
            'mcrypt',
            'openssl',
            'fileinfo',
            'pdo',
            'posix',
            'ipc',
            'pcntl',
            'bz2',
            'zip',
            'cli',
            'json',
            'mbstring',
            'mbregex',
            'calendar',
            'sockets',
            'readline',
            'xml_all',
            'fpm',
            'sqlite',
            'mysql',
            'pgsql',
            'pdo',
            'icu',
            'intl',
            'iconv',
            'phar',
            'soap',
            'exif',
            'xmlrpc',
            'curl',
            'gd'];

        self::$EXTENSIONS = [
            'memcache' => [],
            'memcached' => ['--disable-memcached-sasl'],
            'mongo' => [],
            'redis' => [],
            'xdebug' => [],
            'ldap' => ['--with-libdir=lib/x86_64-linux-gnu'],
        ];

        self::$PHP_7_EXTENSIONS = [
            'github:php-memcached-dev/php-memcached php7' => ['--disable-memcached-sasl'],
            'redis' => [],
            'xdebug' => [],
            'ldap' => ['--with-libdir=lib/x86_64-linux-gnu'],
        ];

        self::$OPTIONS = ['--with-libdir=lib/x86_64-linux-gnu', '--with-gd=shared', '--enable-gd-natf', '--with-jpeg-dir=/usr', '--with-png-dir=/usr'];

        self::$VERSIONS = [
            '5.6' => [
                'variants' => self::$VARIANTS,
                'extensions' => self::$EXTENSIONS,
                'options' => self::$OPTIONS],
            '7.0' => [
                'variants' => self::$VARIANTS,
                'extensions' => self::$PHP_7_EXTENSIONS,
                'options' => self::$OPTIONS]];
    }

    public function generateScript()
    {
        $this->cmd('set +e');
        $this->cmd('source ${SIMPLECI_HOME}/.phpbrew/bashrc');

        foreach (self::$VERSIONS as $version => $versionConf) {
            $this->installPhp($version, $versionConf['variants'], $versionConf['options']);
            $this->cmd(sprintf('phpbrew switch %s', $version));
            $this->cmd('mkdir -p $(phpbrew path config-scan)');
            $this->installExtensions($versionConf['extensions']);
            $this->postSetup($versionConf['variants']);
            $this->clean($version);
            $this->cmd();
        }
    }

    private function installPhp($version, $variants, $options)
    {
        $variantArg = implode(' ', array_map(function ($ext) {
            return '+' . $ext;
        }, $variants));

        $optionsArg = '';
        if (!empty($options)) {
            $optionsArg = ' -- ' . implode(' ', $options);
        }

        $installCmd = sprintf('phpbrew install -j $(nproc) %s as %s %s %s',
            $version, $version, $variantArg, $optionsArg);
        $this->cmd($installCmd);
    }

    private function installExtensions($extensions)
    {
        foreach ($extensions as $name => $options) {
            $optionsArg = '';
            if (!empty($options)) {
                $optionsArg = ' -- ' . implode(' ', $options);
            }

            $cmd = sprintf('phpbrew --no-progress ext install %s %s', $name, $optionsArg);
            $this->cmd($cmd);
        }
    }

    private function postSetup($variants)
    {
        $this->cmd('echo "date.timezone=UTC" >> $(phpbrew path config-scan)/simpleci.ini');
        if (in_array('gd', $variants)) {
            $this->cmd('echo "extension=gd.so" >> $(phpbrew path config-scan)/gd.ini');
        }
    }

    private function cmd($cmd = '')
    {
        echo $cmd, "\n";
        return $this;
    }

    private function clean($version)
    {
        $this->cmd(sprintf('phpbrew clean %s', $version));
    }


}

(new PhpInstaller())->generateScript();











