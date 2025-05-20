package com.example.command;

import com.example.SessionVariables;
import com.example.utils.ExecutionResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;

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
        SessionVariables sessionVars = SessionVariables.getInstance();
        for (String arg : arguments) {
            if (!Paths.get(arg).isAbsolute()) {
                String currentDir = sessionVars.get("PWD");
                if (currentDir == null) {
                    currentDir = System.getProperty("user.dir");
                    sessionVars.set("PWD", currentDir);
                }
                arg = Paths.get(currentDir, arg).normalize().toString();
            }

            File file = new File(arg);
            if (!file.exists()) {
                result.addError(arg + ": No such file");
                continue;
            }
            if (!file.isFile()) {
                result.addError(arg + ": It's a directory");
                continue;
            }
            
            try (FileInputStream stream = new FileInputStream(arg)) {
                result.setSuccess(true);
                result.addOutput(new String(stream.readAllBytes()));
            } catch (IOException e) {
                result.addError(arg + ": No such file");
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
