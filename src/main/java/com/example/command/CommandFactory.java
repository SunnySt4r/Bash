package com.example.command;

import com.example.utils.WrongCommandException;

public class CommandFactory {
    public static Command create(String command, String[] arguments) throws WrongCommandException {
        if (arguments.length == 0) {
            arguments = null;
        }
        return switch (command) {
            case "echo" -> new Echo(arguments);
            case "exit" -> new Exit();
            case "cat" -> new Cat(arguments);
            case "pwd" -> new Pwd();
            case "ls" -> new Ls();
            case "wc" -> new Wc(arguments);
            case "grep" -> new Grep(arguments);
            default -> throw new WrongCommandException("Unknown command: " + command);
        };
    }
}
