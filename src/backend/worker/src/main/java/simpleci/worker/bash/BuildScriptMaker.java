package simpleci.worker.bash;

import com.google.common.base.Joiner;
import simpleci.shared.job.config.EnvironmentVar;
import simpleci.worker.job.JobContext;

import java.io.InputStream;
import java.util.Scanner;

public class BuildScriptMaker {
    public final static CommandOption BEFORE_SCRIPT_CMD_OPTIONS = new CommandOption(true, true, true);
    public final static CommandOption SCRIPT_CMD_OPTIONS = new CommandOption(false, true, true);
    public final static CommandOption AFTER_SCRIPT_CMD_OPTIONS = new CommandOption(false, true, true);

    private final static int CACHE_DIFF_OUTPUT_LINES = 20;
    public static final String SSH_DIR_PERMISSION = "0700";
    public static final String SSH_PRIVATE_KEY_PERMISSION = "0600";
    public static final int GIT_CLONE_DEPTH = 50;


    public String generateBuildScript(JobContext job) {

        CommandGenerator bash = new CommandGenerator();
        bash.appendScript(getFileContent("/header.sh"))
            .append("source /etc/profile");

        this.initEnvVars(bash, job)
            .printEnvVars(bash, job)
            .setUpSsh(bash, job)
            .cloneRepository(bash, job)
            .unpackCache(bash, job)
            .beforeScriptSection(bash, job)
            .scriptSection(bash, job)
            .afterScriptSection(bash, job)
            .packCache(bash, job)
            .finishSection(bash);

        return bash.makeScript();
    }


    private BuildScriptMaker scriptSection(CommandGenerator bash, JobContext job) {
        for (String cmd : job.jobConfig.script) {
            bash.cmd(cmd, SCRIPT_CMD_OPTIONS)
                .append("simpleci_result $?");
        }
        return this;
    }

    private BuildScriptMaker setUpSsh(CommandGenerator bash, JobContext job) {
        if (job.jobSettings.sshKey.hasKey) {
            bash.emptyLine()
                .mkdir(Locations.SSH_DIR, true)
                .chmod(Locations.SSH_DIR, SSH_DIR_PERMISSION)
                .append(String.format("echo 'StrictHostKeyChecking no' >> %s", Locations.SSH_CONFIG_FILE))
                .dumpFile(job.jobSettings.sshKey.publicKey, Locations.SSH_PUBLIC_KEY, null)
                .dumpFile(job.jobSettings.sshKey.privateKey, Locations.SSH_PRIVATE_KEY, null)
                .chmod(Locations.SSH_PRIVATE_KEY, SSH_PRIVATE_KEY_PERMISSION);
        }
        return this;
    }

    private BuildScriptMaker cloneRepository(CommandGenerator bash, JobContext job) {
        bash.emptyLine()
            .mkdir(Locations.BUILD_DIR, true)
            .cmd(String.format("git clone -q --depth=%d --branch %s %s %s",
                    GIT_CLONE_DEPTH, job.jobSettings.repositorySettings.branch, job.jobSettings.repositorySettings.repositoryUrl, Locations.BUILD_DIR))
            .cd(Locations.BUILD_DIR)
            .cmd(String.format("git checkout -qf %s", job.jobSettings.repositorySettings.commit))
            .emptyLine();
        return this;
    }

    private BuildScriptMaker initEnvVars(CommandGenerator bash, JobContext job) {
        bash.emptyLine();
        for (EnvironmentVar envVar : job.jobEnvVars.allEnvironment) {
            bash.export(envVar);
        }
        bash.emptyLine();
        return this;
    }

    private BuildScriptMaker printEnvVars(CommandGenerator bash, JobContext job) {
        bash.emptyLine();
        bash.doEcho("Environment vars:");
        for (EnvironmentVar envVar : job.jobEnvVars.allEnvironment) {
            bash.doEcho(envVar.toBashString());
        }
        bash.emptyLine();
        return this;
    }

    private BuildScriptMaker beforeScriptSection(CommandGenerator bash, JobContext job) {
        for (String cmd : job.jobConfig.beforeScript) {
            bash.cmd(cmd, BEFORE_SCRIPT_CMD_OPTIONS);
        }
        return this;
    }


    private BuildScriptMaker afterScriptSection(CommandGenerator bash, final JobContext job) {
        if (!job.jobConfig.afterSuccess.isEmpty()) {
            CommandGenerator afterScriptCommands = new CommandGenerator();
            for (String cmd : job.jobConfig.afterSuccess) {
                afterScriptCommands.cmd(cmd, AFTER_SCRIPT_CMD_OPTIONS);
            }
            bash.doIf("$SIMPLECI_TEST_RESULT = 0", afterScriptCommands.getLines());
        }

        if (!job.jobConfig.afterFailure.isEmpty()) {
            CommandGenerator afterFailureCommands = new CommandGenerator();
            for (String cmd : job.jobConfig.afterFailure) {
                afterFailureCommands.cmd(cmd, AFTER_SCRIPT_CMD_OPTIONS);
            }
            bash.doIf("$SIMPLECI_TEST_RESULT != 0", afterFailureCommands.getLines());
        }

        if (!job.jobConfig.afterScript.isEmpty()) {
            for (String cmd : job.jobConfig.afterScript) {
                bash.cmd(cmd, AFTER_SCRIPT_CMD_OPTIONS);
            }
        }
        return this;

    }

    private BuildScriptMaker unpackCache(CommandGenerator bash, JobContext job) {
        if (job.jobConfig.cache.pull) {
            if (!job.jobConfig.cache.directories.isEmpty()) {
                final String cacheDirs = Joiner.on(' ').join(job.jobConfig.cache.directories);

                for (String cacheDir : job.jobConfig.cache.directories) {
                    bash.mkdir(cacheDir, true);
                }

                bash.doIf(String.format("-f %s", Locations.CACHE_FILE), new CommandGenerator()
                        .doEcho("Extracting cache")
                        .append(String.format("tar -Pzxf %s %s", Locations.CACHE_FILE, cacheDirs))
                        .append(String.format("rm %s", Locations.CACHE_FILE))
                        .getLines());

                bash.append(hashCommand(cacheDirs, Locations.CACHE_MD5_BEFORE));
            }
        }
        return this;
    }

    private BuildScriptMaker packCache(CommandGenerator bash, final JobContext job) {
        if (job.jobConfig.cache.push) {
            if (!job.jobConfig.cache.directories.isEmpty()) {
                final String cacheDirs = Joiner.on(' ').join(job.jobConfig.cache.directories);

                bash.doEcho("Calculating cache diff")
                    .append(hashCommand(cacheDirs, Locations.CACHE_MD5_AFTER))
                    .append(String.format("diff -B %s %s | awk '/^[<>]/ {print $NF}' | sort > %s",
                            Locations.CACHE_MD5_BEFORE, Locations.CACHE_MD5_AFTER, Locations.CACHE_MD5_DIFF));

                bash.doIf(String.format("-s %s", Locations.CACHE_MD5_DIFF),
                        new CommandGenerator()
                                .doEcho("Cache changes detected:")
                                // print odd lines (md5diff contains pair of equal lines for single file)
                                .append(String.format("head -n %d %s | awk 'NR%%2==0'",
                                        CACHE_DIFF_OUTPUT_LINES, Locations.CACHE_MD5_DIFF))
                                .doEcho("Packing cache")
                                .append(String.format("tar -Pzcf %s %s", Locations.CACHE_FILE, cacheDirs))
                                .getLines(),
                        new CommandGenerator()
                                .doEcho("No cache changes detected")
                                .getLines());
            } else {
                bash.doEcho("No cache dirs configured");
            }
        } else {
            bash.doEcho("Cache will not push");
        }
        return this;
    }

    private String hashCommand(String cacheDirs, String hashFileLocation) {
        return String
                .format("hashdeep -c md5 -r %s | awk -F',' '{print $2,$3}' | sort > %s", cacheDirs, hashFileLocation);
    }

    private BuildScriptMaker finishSection(CommandGenerator bash) {
        bash.append("echo -e \"\\nDone. Your build exited with $SIMPLECI_TEST_RESULT.\"")
            .append("simpleci_terminate $SIMPLECI_TEST_RESULT");
        return this;
    }


    private String getFileContent(String fileName) {

        StringBuilder result = new StringBuilder("");

        InputStream stream = getClass().getResourceAsStream(fileName);
        try (Scanner scanner = new Scanner(stream)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                result.append(line).append("\n");
            }
        }

        return result.toString();

    }
}
