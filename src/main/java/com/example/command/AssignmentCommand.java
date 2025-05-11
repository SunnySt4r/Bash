package com.example.command;

import com.example.SessionVariables;
import com.example.utils.ExecutionResult;

public class AssignmentCommand extends Command {
    private final String variableName;
    private final String value;

    public AssignmentCommand(String varName, String value) {
        this.variableName = varName;
        this.value = value;
    }

    @Override
    public ExecutionResult execute() {
        SessionVariables.getInstance().set(variableName, value);
        return new ExecutionResult(true, "");
    }

    @Override
    public void putArgs(String args) {

    }
}