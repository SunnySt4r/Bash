package com.example;

import com.example.command.Command;
import com.example.command.CommandFactory;
import com.example.utils.WrongCommandException;
import com.example.command.AssignmentCommand;
import com.example.Token.QuoteType;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    public static List<Command> parse(String input) throws WrongCommandException {
        List<Command> commands = new ArrayList<>();
        String[] pipelineParts = input.split("(?<!\\\\)\\|");

        for (String part : pipelineParts) {
            part = part.trim().replaceAll("\\\\\\|", "|");
            Matcher assignMatcher = Pattern.compile("^([a-zA-Z_][a-zA-Z0-9_]*)=(.*)$").matcher(part);
            if (assignMatcher.matches()) {
                String varName = assignMatcher.group(1);
                String value = assignMatcher.group(2).trim();
                if ((value.startsWith("'") && value.endsWith("'"))) {
                value = value.substring(1, value.length() - 1);
                } else {
                    if ((value.startsWith("\"") && value.endsWith("\""))) {
                        value = value.substring(1, value.length() - 1);
                    }
                    value = replaceVariables(value);
                }
                // System.out.printf("DEBUG: %s = '%s'%n", varName, value);
                commands.add(new AssignmentCommand(varName, value));
                continue;
            }
            
            List<Token> tokens = splitWithQuotes(part);

            // System.out.println("DEBUG tokens: ");
            // for (Token t : tokens) {
            //     System.out.printf("  [%s] '%s'%n", t.getQuoteType(), t.getValue());
            // }

            if (tokens.isEmpty()) continue;

            Token firstToken = tokens.get(0);
            String commandValue = firstToken.getValue();
            List<Token> argTokens = tokens.subList(1, tokens.size());
            String[] args = processArguments(argTokens);

            // System.out.printf("DEBUG command: %s, args: %s%n", commandValue, Arrays.toString(args));

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
        return tokens.stream().map(token -> {
                switch (token.getQuoteType()) {
                    case SINGLE:
                        return token.getValue(); 
                    case DOUBLE:
                    case NONE:
                        return replaceVariables(token.getValue()); 
                    default:
                        return token.getValue();
                }
            }).toArray(String[]::new);
    }

    private static String replaceVariables(String input) {
        Pattern pattern = Pattern.compile("\\$(\\w+|\\{[^}]+\\})");
        Matcher matcher = pattern.matcher(input);
        StringBuffer sb = new StringBuffer();
        SessionVariables vars = SessionVariables.getInstance();
        
        while (matcher.find()) {
            String varName = matcher.group(1).replaceAll("[{}]", "");
            String value = vars.get(varName);
            
            if (value != null) {
                // value = value.replace("\\", "\\\\").replace("\"", "\\\"");
                // if (value.contains(" ")) {
                //     value = "\"" + value + "\"";
                // }
            }
            
            matcher.appendReplacement(sb, value != null ? 
                Matcher.quoteReplacement(value) : "");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}