package com.example.command;

public class CommandFactory {
    public static Command create(String command, String[] arguments) {
        if (arguments.length == 0) {
            arguments = null;
        }
        return switch (command) {
            case "echo" -> new Echo(arguments);
            case "exit" -> new Exit();
            case "cat" -> new Cat(arguments);
            case "pwd" -> new Pwd();
            case "ls" -> new Ls(arguments);
            case "cd" -> new Cd(arguments);
            case "wc" -> new Wc(arguments);
            case "grep" -> new Grep(arguments);
            default -> new ExternalCommand(command, arguments);
        };
    }
}
