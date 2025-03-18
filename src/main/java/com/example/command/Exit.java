package com.example.command;

import com.example.utils.ExecutionResult;
import com.example.utils.ExitExeption;

public class Exit extends Command {
    public Exit() {
    }

    @Override
    public ExecutionResult execute() throws ExitExeption {
        throw new ExitExeption("Exit");
    }

    @Override
    public void putArgs(String args) {
    }
    
}
