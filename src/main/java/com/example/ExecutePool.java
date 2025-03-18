package com.example;

import java.util.List;

import com.example.command.Command;
import com.example.utils.ExecutionResult;
import com.example.utils.ExitExeption;

public class ExecutePool {
    
    public static ExecutionResult execute(List<Command> commands) throws ExitExeption {
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
