package com.example.command;

import com.example.utils.ExecutionResult;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PwdTest {

    @Test
    void execute_ReturnsAbsolutePathOfWorkingDirectory() {
        Pwd pwdCommand = new Pwd();
        ExecutionResult result = pwdCommand.execute();
        assertTrue(result.isSuccess());
        assertEquals(Paths.get("").toAbsolutePath().toString(), result.getOutput());
        assertEquals("", result.getError());
    }

    @Test
    void putArgs_PipeInput_ReturnsAbsolutePathOfWorkingDirectory() {
        Pwd pwdCommand = new Pwd();
        pwdCommand.putArgs("some input");
        ExecutionResult result = pwdCommand.execute();
        assertTrue(result.isSuccess());
        assertEquals(Paths.get("").toAbsolutePath().toString(), result.getOutput());
        assertEquals("", result.getError());
    }
}
