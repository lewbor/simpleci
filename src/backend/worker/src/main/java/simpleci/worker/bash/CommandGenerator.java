package simpleci.worker.bash;

import com.google.common.base.Joiner;
import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;
import org.apache.commons.codec.binary.Base64;
import simpleci.shared.job.config.EnvironmentVar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandGenerator {

    public static final CommandOption DEFAULT_CMD_OPTIONS = new CommandOption(true, true, true);

    private List<String> script = new ArrayList<String>();

    public String makeScript() {
        return Joiner.on("\n").join(script);
    }

    public List<String> getLines() {
        return script;
    }

    public CommandGenerator cmd(String cmdToExecute, CommandOption option) {
        String cmd = String.format("simpleci_cmd %s %s", escapeShellArgument(cmdToExecute), generateOptions(option));
        append(cmd);
        return this;
    }
    public CommandGenerator cmd(String cmdToExecute) {
        return cmd(cmdToExecute, DEFAULT_CMD_OPTIONS);
    }


    public CommandGenerator emptyLine() {
        append("");
        return this;
    }

    public CommandGenerator doEcho(String message) {
        String[] messages = message.split("\n");

        for (String displayMessage : messages) {
            append(String.format("echo %s", escapeShellArgument(displayMessage)));
        }
        return this;
    }

    public CommandGenerator export(EnvironmentVar var) {
        append(String.format("export %s", var.toBashString()));
        return this;
    }

    public CommandGenerator mkdir(String name, boolean createParents) {
        String arguments = createParents ? "-p " : "";
        append(String.format("mkdir %s%s", arguments, name));
        return this;
    }

    public CommandGenerator cd(String dir) {
        append("cd " + dir);
        return this;
    }

    public CommandGenerator chmod(String dir, String mode) {
        append(String.format("chmod %s %s", mode, dir));
        return this;
    }

    public CommandGenerator doIf(String condition, List<String> ifLines) {
        return doIf(condition, ifLines, new ArrayList<String>());
    }

    public CommandGenerator doIf(String condition, List<String> ifLines, List<String> elseLines) {
        List<String> lines = new ArrayList<String>();
        lines.add("if [[ " + condition + " ]]; then");
        lines.addAll(ifLines);

        if(!elseLines.isEmpty()) {
            lines.add("else");
            lines.addAll(elseLines);
        }

        lines.add("fi");
        appendMultiply(lines);

        return this;
    }

    public CommandGenerator dumpFile(String content, String path, String description) {
        if (description != null) {
            doEcho(description);
        }
        String cmd = String.format("echo %s | base64 --decode > %s", new String(Base64.encodeBase64(content.getBytes())), path);
        append(cmd);
        return this;
    }

    public CommandGenerator appendScript(String script) {
        String[] scriptLines = script.split("\n");
        appendMultiply(Arrays.asList(scriptLines));
        return this;
    }

    private String generateOptions(CommandOption cmdOptions) {
        List<String> options = new ArrayList<String>();
        if (cmdOptions.checkAssert) {
            options.add("--assert");
        }
        if (cmdOptions.echo) {
            options.add("--echo");
        }
        if (cmdOptions.timing) {
            options.add("--timing");
        }
        return Joiner.on(" ").join(options);

    }

    public CommandGenerator append(String line) {
        script.add(line);
        return this;
    }

    private CommandGenerator appendMultiply(Iterable<String> lines) {
        for (String line : lines) {
            append(line);
        }
        return this;
    }

    public static String escapeShellArgument(String argument) {
        final Escapers.Builder builder = Escapers.builder();
        builder.addEscape('\'', "'\"'\"'");
        Escaper escaper = builder.build();
        return String.format("'%s'", escaper.escape(argument));
    }
}
