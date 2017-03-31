package simpleci.worker.bash;

public class CommandOption {
    public final boolean echo;
    public final boolean checkAssert;
    public final boolean timing;

    public CommandOption(boolean checkAssert, boolean echo, boolean timing) {
        this.echo = echo;
        this.checkAssert = checkAssert;
        this.timing = timing;
    }
}
