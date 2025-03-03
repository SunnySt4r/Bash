import command.Executable;

public class Executor {
    public boolean exec(Executable command) {
        return command.execute();
    }
}
