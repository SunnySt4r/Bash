package com.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.example.command.Command;
import com.example.command.CommandFactory;

public class Parser {
    public static List<Command> parse(String input) {
        List<Command> commands = new ArrayList<>();
        String[] lines = input.split(" *\\| *");
        for (String line : lines) {
            line = line.trim();
            String[] parts = line.split(" +");
            String command = parts[0];
            String[] arguments = Arrays.copyOfRange(parts, 1, parts.length);
            commands.add(CommandFactory.create(command, arguments));
        }
        return commands;
    }
}
