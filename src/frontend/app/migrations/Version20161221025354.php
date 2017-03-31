<?php

namespace Application\Migrations;

use Doctrine\DBAL\Migrations\AbstractMigration;
use Doctrine\DBAL\Schema\Schema;

class Version20161221025354 extends AbstractMigration
{
    /**
     * @param Schema $schema
     */
    public function up(Schema $schema)
    {
        // this up() migration is auto-generated, please modify it to your needs
        $this->abortIf($this->connection->getDatabasePlatform()->getName() != 'mysql', 'Migration can only be executed safely on \'mysql\'.');

        $this->addSql('CREATE TABLE ssh_key (id INT AUTO_INCREMENT NOT NULL, name VARCHAR(255) NOT NULL, created_at DATETIME NOT NULL, public_key LONGTEXT NOT NULL, private_key LONGTEXT NOT NULL, UNIQUE INDEX UNIQ_82A73B645E237E06 (name), PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci ENGINE = InnoDB');
        $this->addSql('CREATE TABLE provider (id INT AUTO_INCREMENT NOT NULL, gc_account_id INT NOT NULL, name VARCHAR(255) NOT NULL, discr VARCHAR(255) NOT NULL, project VARCHAR(255) DEFAULT NULL, zone VARCHAR(255) DEFAULT NULL, machine_type VARCHAR(255) DEFAULT NULL, snapshot_name VARCHAR(255) DEFAULT NULL, disk_type VARCHAR(255) DEFAULT NULL, disk_size INT DEFAULT NULL, UNIQUE INDEX UNIQ_92C4739C5E237E06 (name), INDEX IDX_92C4739CB09F3A52 (gc_account_id), PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci ENGINE = InnoDB');
        $this->addSql('CREATE TABLE repository (id INT AUTO_INCREMENT NOT NULL, url VARCHAR(255) NOT NULL, discr VARCHAR(255) NOT NULL, token VARCHAR(255) DEFAULT NULL, UNIQUE INDEX UNIQ_5CFE57CDF47645AE (url), PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci ENGINE = InnoDB');
        $this->addSql('CREATE TABLE google_cloud_account (id INT AUTO_INCREMENT NOT NULL, name VARCHAR(255) NOT NULL, service_account LONGTEXT NOT NULL, UNIQUE INDEX UNIQ_15FE5FE35E237E06 (name), PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci ENGINE = InnoDB');
        $this->addSql('CREATE TABLE cache (id INT AUTO_INCREMENT NOT NULL, gc_account_id INT DEFAULT NULL, name VARCHAR(255) NOT NULL, discr VARCHAR(255) NOT NULL, endpoint VARCHAR(255) DEFAULT NULL, bucket VARCHAR(255) DEFAULT NULL, access_key VARCHAR(255) DEFAULT NULL, secret_key VARCHAR(255) DEFAULT NULL, UNIQUE INDEX UNIQ_41476BE75E237E06 (name), INDEX IDX_41476BE7B09F3A52 (gc_account_id), PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci ENGINE = InnoDB');
        $this->addSql('ALTER TABLE provider ADD CONSTRAINT FK_92C4739CB09F3A52 FOREIGN KEY (gc_account_id) REFERENCES google_cloud_account (id)');
        $this->addSql('ALTER TABLE cache ADD CONSTRAINT FK_41476BE7B09F3A52 FOREIGN KEY (gc_account_id) REFERENCES google_cloud_account (id)');

        $this->addSql('ALTER TABLE build ADD error_message LONGTEXT DEFAULT NULL');
        $this->addSql('ALTER TABLE job ADD created_at DATETIME NOT NULL, CHANGE parameters config LONGTEXT NOT NULL');
        $this->addSql('ALTER TABLE project ADD repository_id INT DEFAULT NULL, ADD ssh_key_id INT DEFAULT NULL, ADD cache_id INT DEFAULT NULL, ADD provider_id INT DEFAULT NULL, DROP public_key, DROP private_key, DROP repository_type');
        $this->addSql('ALTER TABLE project ADD CONSTRAINT FK_2FB3D0EE50C9D4F7 FOREIGN KEY (repository_id) REFERENCES repository (id)');
        $this->addSql('ALTER TABLE project ADD CONSTRAINT FK_2FB3D0EE5D9F7F3D FOREIGN KEY (ssh_key_id) REFERENCES ssh_key (id)');
        $this->addSql('ALTER TABLE project ADD CONSTRAINT FK_2FB3D0EEA45D650B FOREIGN KEY (cache_id) REFERENCES cache (id)');
        $this->addSql('ALTER TABLE project ADD CONSTRAINT FK_2FB3D0EEA53A8AA FOREIGN KEY (provider_id) REFERENCES provider (id)');
        $this->addSql('CREATE INDEX IDX_2FB3D0EE50C9D4F7 ON project (repository_id)');
        $this->addSql('CREATE INDEX IDX_2FB3D0EE5D9F7F3D ON project (ssh_key_id)');
        $this->addSql('CREATE INDEX IDX_2FB3D0EEA45D650B ON project (cache_id)');
        $this->addSql('CREATE INDEX IDX_2FB3D0EEA53A8AA ON project (provider_id)');

        $this->addSql("UPDATE job set created_at = started_at WHERE started_at is not null");

        $this->addSql('DROP TABLE settings_parameter');
    }

    /**
     * @param Schema $schema
     */
    public function down(Schema $schema)
    {
        $this->throwIrreversibleMigrationException();
    }



}
