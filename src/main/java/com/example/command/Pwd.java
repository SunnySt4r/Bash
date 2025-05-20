package com.example.command;

import com.example.SessionVariables;
import com.example.utils.ExecutionResult;

public class Pwd extends Command {
    @Override
    public ExecutionResult execute() {
        String currentDirectory = SessionVariables.getInstance().get("PWD");
        if (currentDirectory == null) {
            currentDirectory = System.getProperty("user.dir");
            SessionVariables.getInstance().set("PWD", currentDirectory);
        }
        return new ExecutionResult(true, currentDirectory);
    }

    @Override
    public void putArgs(String args) {
    }
}
