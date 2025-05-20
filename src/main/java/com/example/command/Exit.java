package com.example.command;

import com.example.utils.ExecutionResult;
import com.example.utils.ExitException;

public class Exit extends Command {
    public Exit() {
    }

    @Override
    public ExecutionResult execute() throws ExitException {
        throw new ExitException("Exit");
    }

    @Override
    public void putArgs(String args) {
    }
    
}
