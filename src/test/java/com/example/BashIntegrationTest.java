package com.example;

import com.example.command.Command;
import com.example.utils.ExecutionResult;
import com.example.utils.ExitException;
import com.example.utils.WrongCommandException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BashIntegrationTest {

    @Test
    void integrationTest_PipedEchoGrep() throws WrongCommandException, ExitException {
        String input = "echo hello world | grep world";
        List<Command> commands = Parser.parse(input);
        ExecutionResult result = ExecutePool.execute(commands);
        assertTrue(result.isSuccess());
        assertEquals("hello world", result.getOutput().trim());
        assertEquals("", result.getError());
    }
}
