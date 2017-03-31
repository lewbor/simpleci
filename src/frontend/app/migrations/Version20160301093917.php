<?php

namespace Application\Migrations;

use Doctrine\DBAL\Migrations\AbstractMigration;
use Doctrine\DBAL\Schema\Schema;

/**
 * Initial migration
 */
class Version20160301093917 extends AbstractMigration
{
    /**
     * @param Schema $schema
     */
    public function up(Schema $schema)
    {
        $this->abortIf($this->connection->getDatabasePlatform()->getName() != 'mysql', 'Migration can only be executed safely on \'mysql\'.');

        $this->addSql('CREATE TABLE simpleci_user (id INT AUTO_INCREMENT NOT NULL, username VARCHAR(255) NOT NULL, username_canonical VARCHAR(255) NOT NULL, email VARCHAR(255) NOT NULL, email_canonical VARCHAR(255) NOT NULL, enabled TINYINT(1) NOT NULL, salt VARCHAR(255) NOT NULL, password VARCHAR(255) NOT NULL, last_login DATETIME DEFAULT NULL, locked TINYINT(1) NOT NULL, expired TINYINT(1) NOT NULL, expires_at DATETIME DEFAULT NULL, confirmation_token VARCHAR(255) DEFAULT NULL, password_requested_at DATETIME DEFAULT NULL, roles LONGTEXT NOT NULL COMMENT \'(DC2Type:array)\', credentials_expired TINYINT(1) NOT NULL, credentials_expire_at DATETIME DEFAULT NULL, UNIQUE INDEX UNIQ_71D5DB3C92FC23A8 (username_canonical), UNIQUE INDEX UNIQ_71D5DB3CA0D96FBF (email_canonical), PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci ENGINE = InnoDB');
        $this->addSql('CREATE TABLE build (id INT AUTO_INCREMENT NOT NULL, project_id INT DEFAULT NULL, number INT NOT NULL, created_at DATETIME NOT NULL, started_at DATETIME DEFAULT NULL, ended_at DATETIME DEFAULT NULL, status ENUM(\'pending\', \'running\', \'stopped\', \'finished_success\', \'failed\') NOT NULL COMMENT \'(DC2Type:OperationStatus)\', commit VARCHAR(255) NOT NULL, commit_range VARCHAR(255) NOT NULL, branch VARCHAR(255) NOT NULL, message VARCHAR(255) NOT NULL, author_name VARCHAR(255) NOT NULL, author_email VARCHAR(255) NOT NULL, committed_date DATETIME NOT NULL, config LONGTEXT NOT NULL, INDEX IDX_BDA0F2DB166D1F9C (project_id), PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci ENGINE = InnoDB');
        $this->addSql('CREATE TABLE sessions (sess_id VARCHAR(255) NOT NULL, sess_data LONGBLOB NOT NULL, sess_time INT NOT NULL, sess_lifetime INT NOT NULL, PRIMARY KEY(sess_id)) DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci ENGINE = InnoDB');
        $this->addSql('CREATE TABLE project (id INT AUTO_INCREMENT NOT NULL, name VARCHAR(255) NOT NULL, description LONGTEXT DEFAULT NULL, server_identity VARCHAR(255) NOT NULL, public_key LONGTEXT NOT NULL, private_key LONGTEXT NOT NULL, repository_url VARCHAR(255) NOT NULL, repository_type ENUM(\'gitlab\', \'github\') NOT NULL COMMENT \'(DC2Type:RepositoryType)\', PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci ENGINE = InnoDB');
        $this->addSql('CREATE TABLE job (id INT AUTO_INCREMENT NOT NULL, build_id INT DEFAULT NULL, number INT NOT NULL, started_at DATETIME DEFAULT NULL, ended_at DATETIME DEFAULT NULL, status ENUM(\'pending\', \'running\', \'stopped\', \'finished_success\', \'failed\') NOT NULL COMMENT \'(DC2Type:OperationStatus)\', stage VARCHAR(255) NOT NULL, parameters LONGTEXT NOT NULL, log LONGTEXT NOT NULL, INDEX IDX_FBD8E0F817C13F8B (build_id), PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci ENGINE = InnoDB');
        $this->addSql('ALTER TABLE build ADD CONSTRAINT FK_BDA0F2DB166D1F9C FOREIGN KEY (project_id) REFERENCES project (id)');
        $this->addSql('ALTER TABLE job ADD CONSTRAINT FK_FBD8E0F817C13F8B FOREIGN KEY (build_id) REFERENCES build (id)');
    }

    /**
     * @param Schema $schema
     */
    public function down(Schema $schema)
    {
        $this->abortIf($this->connection->getDatabasePlatform()->getName() != 'mysql', 'Migration can only be executed safely on \'mysql\'.');

        $this->addSql('ALTER TABLE job DROP FOREIGN KEY FK_FBD8E0F817C13F8B');
        $this->addSql('ALTER TABLE build DROP FOREIGN KEY FK_BDA0F2DB166D1F9C');
        $this->addSql('DROP TABLE simpleci_user');
        $this->addSql('DROP TABLE build');
        $this->addSql('DROP TABLE sessions');
        $this->addSql('DROP TABLE project');
        $this->addSql('DROP TABLE job');
    }
}
