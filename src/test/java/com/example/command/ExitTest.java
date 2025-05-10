package com.example.command;

import com.example.utils.ExitExeption;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExitTest {

    @Test
    void execute_ThrowsExitException() {
        Exit exitCommand = new Exit();
        assertThrows(ExitExeption.class, exitCommand::execute);
    }

    @Test
    void putArgs_DoesNothing() {
        Exit exitCommand = new Exit();
        exitCommand.putArgs("some input");
    }
}
