package com.example.command;

import com.example.utils.ExecutionResult;

import java.io.FileInputStream;
import java.io.IOException;

public class Cat extends Command {

    private final String[] arguments;
    private String staticResult = null;

    public Cat(String[] arguments) {
        this.arguments = arguments;
    }

    public ExecutionResult execute() {
        if (staticResult != null) {
            return new ExecutionResult(true, staticResult);
        }

        if (arguments == null) {
            return new ExecutionResult(false, "cat: missing arguments");
        }

        ExecutionResult result = new ExecutionResult(false, "");
        for (String arg : arguments) {
            try (FileInputStream stream = new FileInputStream(arg)) {
                result.setSuccess(true);
                result.addOutput(new String(stream.readAllBytes()));
            } catch (IOException e) {
                result.addError(arg + ": No such file or directory");
            }
        }
        return result;
    }

    @Override
    public void putArgs(String args) {
        if (arguments == null) {
            staticResult = args;
        }
    }
}
