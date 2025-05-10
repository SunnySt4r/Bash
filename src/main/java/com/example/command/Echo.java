package com.example.command;

import com.example.utils.ExecutionResult;

public class Echo extends Command {
    private final String message;

    public Echo(String[] arguments) {
        this(String.join(" ", arguments));
    }

    public Echo(String message) {
        this.message = message;
    }

    public ExecutionResult execute() {
        return new ExecutionResult(true, message);
    }

    @Override
    public void putArgs(String args) {
    }
}
