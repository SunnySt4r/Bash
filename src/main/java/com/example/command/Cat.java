package com.example.command;

import java.io.File;
import java.io.FileInputStream;

import com.example.utils.ExecutionResult;

public class Cat extends Command {

    private String[] arguments = null;
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
            try (FileInputStream stream = new FileInputStream(new File(arg))) {
                result.setSuccess(true);
                result.addOutput(new String(stream.readAllBytes()));
            } catch (Exception e) {
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
