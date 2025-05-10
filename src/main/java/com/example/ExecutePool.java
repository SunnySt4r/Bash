package com.example;

import com.example.command.Command;
import com.example.utils.ExecutionResult;
import com.example.utils.ExitException;

import java.util.List;

public class ExecutePool {
    
    public static ExecutionResult execute(List<Command> commands) throws ExitException {
        ExecutionResult prevResult = null;
        for (Command command : commands) {
            if (prevResult != null) {
                command.putArgs(prevResult.getOutput());
            }
            prevResult = command.execute();
        }

        return prevResult;
    }
}
