package com.example;

import com.example.Token.QuoteType;
import com.example.command.AssignmentCommand;
import com.example.command.Command;
import com.example.command.CommandFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    public static List<Command> parse(String input) {
        List<Command> commands = new ArrayList<>();
        String[] pipelineParts = input.split("(?<!\\\\)\\|");

        for (String part : pipelineParts) {
            part = part.trim().replaceAll("\\\\\\|", "|");
            Matcher assignMatcher = Pattern.compile("^([a-zA-Z_][a-zA-Z0-9_]*)=(.*)$").matcher(part);
            if (assignMatcher.matches()) {
                String varName = assignMatcher.group(1);
                String value = assignMatcher.group(2).trim();
                if (value.startsWith("'") && value.endsWith("'")) {
                    value = value.substring(1, value.length() - 1);
                } else {
                    if (value.startsWith("\"") && value.endsWith("\"")) {
                        value = value.substring(1, value.length() - 1);
                    }
                    value = replaceVariables(value);
                }
                // System.out.printf("DEBUG: %s = '%s'%n", varName, value);
                commands.add(new AssignmentCommand(varName, value));
                continue;
            }
            
            List<Token> tokens = splitWithQuotes(part);

            if (tokens.isEmpty()) {
                continue;
            }

            Token firstToken = tokens.getFirst();
            String commandValue = firstToken.value();
            List<Token> argTokens = tokens.subList(1, tokens.size());
            String[] args = processArguments(argTokens);

            commands.add(CommandFactory.create(commandValue, args));
        }
        return commands;
    }

    private static List<Token> splitWithQuotes(String input) {
        List<Token> tokens = new ArrayList<>();
        Matcher m = Pattern.compile(
            "(')((?:\\\\'|.)*?)'|" +  
            "(\")((?:\\\\\"|.)*?)\"|" + 
            "(\\S+)"                    
        ).matcher(input);
        
        while (m.find()) {
            if (m.group(1) != null) { // Single quote
                String value = m.group(2).replace("\\'", "'");
                tokens.add(new Token(value, QuoteType.SINGLE));
            } else if (m.group(3) != null) { // Double quote
                String value = m.group(4).replace("\\\"", "\"");
                tokens.add(new Token(value, QuoteType.DOUBLE));
            } else { 
                tokens.add(new Token(m.group(5), QuoteType.NONE));
            }
        }
        return tokens;
    }

    private static String[] processArguments(List<Token> tokens) {
        return tokens.stream().map(token -> switch (token.quoteType()) {
            case DOUBLE, NONE -> replaceVariables(token.value());
            default -> token.value();
        }).toArray(String[]::new);
    }

    private static String replaceVariables(String input) {
        Pattern pattern = Pattern.compile("\\$(\\w+|\\{[^}]+})");
        Matcher matcher = pattern.matcher(input);
        StringBuilder sb = new StringBuilder();
        SessionVariables vars = SessionVariables.getInstance();
        
        while (matcher.find()) {
            String varName = matcher.group(1).replaceAll("[{}]", "");
            String value = vars.get(varName);
            
            matcher.appendReplacement(sb, value != null ? 
                Matcher.quoteReplacement(value) : "");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
