package com.example.command;

import com.example.utils.WrongCommandExeption;

public class CommandFactory {
    public static Command create(String command, String[] arguments) throws WrongCommandExeption{
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
                throw new WrongCommandExeption("Unknown command: " + command);
        }
    }
}
