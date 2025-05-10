package com.example;

import com.example.command.Command;
import com.example.utils.ExecutionResult;
import com.example.utils.ExitException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExecutePoolTest {

    @Test
    void execute_SingleCommand_ReturnsResultOfCommand() throws ExitException {
        Command mockCommand = mock(Command.class);
        ExecutionResult expectedResult = new ExecutionResult(true, "command output");
        when(mockCommand.execute()).thenReturn(expectedResult);

        ExecutionResult actualResult = ExecutePool.execute(Collections.singletonList(mockCommand));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void execute_TwoCommands_SecondCommandReceivesOutputOfFirst() throws ExitException {
        Command mockCommand1 = mock(Command.class);
        ExecutionResult result1 = new ExecutionResult(true, "output from command 1");
        when(mockCommand1.execute()).thenReturn(result1);

        Command mockCommand2 = mock(Command.class);
        ExecutionResult expectedResult = new ExecutionResult(true, "command 2 output");
        when(mockCommand2.execute()).thenReturn(expectedResult);

        List<Command> commands = Arrays.asList(mockCommand1, mockCommand2);
        ExecutionResult actualResult = ExecutePool.execute(commands);

        assertEquals(expectedResult, actualResult);
        Mockito.verify(mockCommand2).putArgs("output from command 1");
    }

    @Test
    void execute_CommandThrowsExitException_PropagatesException() throws ExitException {
        Command mockCommand = mock(Command.class);
        when(mockCommand.execute()).thenThrow(new ExitException("Exit signal"));

        List<Command> commands = Collections.singletonList(mockCommand);
        assertThrows(ExitException.class, () -> ExecutePool.execute(commands));
    }

    @Test
    void execute_FirstCommandFails_SecondCommandStillExecuted() throws ExitException {
        Command mockCommand1 = mock(Command.class);
        ExecutionResult result1 = new ExecutionResult(false, "error in command 1");
        when(mockCommand1.execute()).thenReturn(result1);

        Command mockCommand2 = mock(Command.class);
        ExecutionResult expectedResult = new ExecutionResult(true, "command 2 output");
        when(mockCommand2.execute()).thenReturn(expectedResult);

        List<Command> commands = Arrays.asList(mockCommand1, mockCommand2);
        ExecutionResult actualResult = ExecutePool.execute(commands);

        assertEquals(expectedResult, actualResult);
        Mockito.verify(mockCommand2).putArgs("");
    }

    @Test
    void execute_EmptyCommandList_ReturnsNull() throws ExitException {
        List<Command> commands = Collections.emptyList();
        ExecutionResult actualResult = ExecutePool.execute(commands);
        assertNull(actualResult);
    }
}
