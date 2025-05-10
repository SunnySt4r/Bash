package com.example.command;

import com.example.utils.ExecutionResult;
import com.example.utils.ExitException;

public interface Executable {
    ExecutionResult execute() throws ExitException;
}
