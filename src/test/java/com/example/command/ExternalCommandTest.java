package com.example.command;

import com.example.utils.ExecutionResult;
import com.example.utils.WrongCommandException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExternalCommandTest {

    @Test
    void execute_successfulExternalCommand() {
        String command = "echo";
        String[] args = {"hello", "world"};
        ExternalCommand externalCommand = new ExternalCommand(command, args);
        ExecutionResult result = externalCommand.execute();
        assertTrue(result.isSuccess());
        assertEquals("hello world", result.getOutput());
        assertEquals("", result.getError());
    }

    @Test
    void execute_successfulExternalCommandWithoutArgs() {
        String command = "git";
        String[] args = {};
        ExternalCommand externalCommand = new ExternalCommand(command, args);
        ExecutionResult result = externalCommand.execute();
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("usage: git [-v | --version]"));
        assertEquals("", result.getOutput());
    }

    @Test
    void execute_externalCommandWithErrorOutput_invalidArgument() {
        String command = "java";
        String[] args = {"-invalid_argument"};
        ExternalCommand externalCommand = new ExternalCommand(command, args);
        ExecutionResult result = externalCommand.execute();
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("Unrecognized option: -invalid_argument"));
        assertEquals("", result.getOutput());
    }

    @Test
    void execute_nonExistentExternalCommand_ThrowsWrongCommandException() {
        String command = "nonexistent_command";
        String[] args = {};
        ExternalCommand externalCommand = new ExternalCommand(command, args);
        assertThrows(WrongCommandException.class, externalCommand::execute);
    }
}
