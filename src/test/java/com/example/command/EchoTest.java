package com.example.command;

import com.example.utils.ExecutionResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EchoTest {
    @Test
    public void execute_NoArguments_ReturnsEmptyString() {
        Echo echoCommand = new Echo(new String[]{});
        ExecutionResult result = echoCommand.execute();
        assertTrue(result.isSuccess());
        assertEquals("", result.getOutput());
        assertEquals("", result.getError());
    }

    @Test
    public void execute_SingleArgument_ReturnsArgument() {
        Echo echoCommand = new Echo(new String[]{"hello"});
        ExecutionResult result = echoCommand.execute();
        assertTrue(result.isSuccess());
        assertEquals("hello", result.getOutput());
        assertEquals("", result.getError());
    }

    @Test
    void execute_MultipleArguments_ReturnsArgumentsJoinedBySpaces() {
        Echo echoCommand = new Echo(new String[]{"hello", "world", "!"});
        ExecutionResult result = echoCommand.execute();
        assertTrue(result.isSuccess());
        assertEquals("hello world !", result.getOutput());
        assertEquals("", result.getError());
    }

    @Test
    void putArgs_DoesNothing() {
        Echo echoCommand = new Echo(new String[]{"initial"});
        echoCommand.putArgs("some input from pipe");
        ExecutionResult result = echoCommand.execute();
        assertTrue(result.isSuccess());
        assertEquals("initial", result.getOutput()); // putArgs не должен изменять поведение echo
        assertEquals("", result.getError());
    }
}
