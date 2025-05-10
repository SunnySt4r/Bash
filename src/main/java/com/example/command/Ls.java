package com.example.command;

import com.example.utils.ExecutionResult;
import com.example.utils.ExitExeption;

import java.io.IOException;

public class Ls extends Command {

    @Override
    public void putArgs(String args) {}

    @Override
    public ExecutionResult execute() throws ExitExeption {
        String[] command = {"ls"};
        try {
            Process process = Runtime.getRuntime().exec(command);
            return new ExecutionResult(true, new String(process.getInputStream().readAllBytes()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
