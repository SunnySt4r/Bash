package com.example.command;

public class CommandFactory {
    public static Command create(String command, String[] arguments) {
        if (arguments.length == 0) {
            arguments = null;
        }
        switch (command) {
            case "echo":
                return new Echo(arguments);
            case "exit":
                return new Exit();
            case "cat":
                return new Cat(arguments);
            default:
                throw new RuntimeException("Unknown command: " + command);
        }
    }
}
