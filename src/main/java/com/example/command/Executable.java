package com.example.command;

import com.example.utils.ExecutionResult;
import com.example.utils.ExitExeption;

public interface Executable {
    public ExecutionResult execute() throws ExitExeption;
}
