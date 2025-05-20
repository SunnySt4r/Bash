package com.example.command;

import com.example.utils.ExecutionResult;
import com.example.utils.WrongCommandException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class ExternalCommand extends Command {

    private final String command;
    private final String[] arguments;

    public ExternalCommand(String command, String[] arguments) {
        this.command = command;
        this.arguments = arguments;
    }

    @Override
    public ExecutionResult execute() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            if (arguments != null) {
                processBuilder.command().addAll(List.of(arguments));
            }
            Process process = processBuilder.start();

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            StringBuilder errorOutput = new StringBuilder();
            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = errorReader.readLine()) != null) {
                    errorOutput.append(line).append("\n");
                }
            }

            int exitCode = process.waitFor();
            boolean success = exitCode == 0;

            ExecutionResult result = new ExecutionResult(success, output.toString().trim());
            if (!errorOutput.toString().trim().isEmpty()) {
                result.addError(errorOutput.toString().trim());
            }
            return result;

        } catch (IOException | InterruptedException e) {
            if (e.getMessage().contains("Cannot run program")) {
                throw new WrongCommandException(command + ": command not found");
            }
            return new ExecutionResult(false, e.getMessage());
        }
    }

    @Override
    public void putArgs(String args) {
    }
}
