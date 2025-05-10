package com.example.command;

import com.example.utils.WrongCommandExeption;

public class CommandFactory {
    public static Command create(String command, String[] arguments) throws WrongCommandExeption{
        if (arguments.length == 0) {
            arguments = null;
        }
        return switch (command) {
            case "echo" -> new Echo(arguments);
            case "exit" -> new Exit();
            case "cat" -> new Cat(arguments);
            case "ls" -> new Ls();
            case "grep" -> new Grep(arguments);
            default -> throw new WrongCommandExeption("Unknown command: " + command);
        };
    }
}
