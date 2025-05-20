package com.example.command;

import com.example.utils.ExitException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExitTest {

    @Test
    void execute_ThrowsExitException() {
        Exit exitCommand = new Exit();
        assertThrows(ExitException.class, exitCommand::execute);
    }

    @Test
    void putArgs_DoesNothing() {
        Exit exitCommand = new Exit();
        exitCommand.putArgs("some input");
    }
}
