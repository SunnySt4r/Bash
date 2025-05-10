package com.example.command;

import com.example.utils.ExecutionResult;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Pwd extends Command {
    @Override
    public ExecutionResult execute() {
        Path currentDirectory = Paths.get("").toAbsolutePath();
        return new ExecutionResult(true, currentDirectory.toString());
    }

    @Override
    public void putArgs(String args) {
    }
}
