package com.example.command;

import com.example.utils.ExecutionResult;

import java.io.IOException;

public class Ls extends Command {

    @Override
    public void putArgs(String args) {
    }

    @Override
    public ExecutionResult execute() {
        String[] command = {"ls"};
        try {
            Process process = Runtime.getRuntime().exec(command);
            return new ExecutionResult(true, new String(process.getInputStream().readAllBytes()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
