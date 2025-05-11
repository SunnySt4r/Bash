package com.example.command;

import com.example.SessionVariables;
import com.example.utils.ExecutionResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AssignmentCommandTest {

    @Test
    void execute_AssignsValueToVariable() {
        String varName = "MY_VARIABLE";
        String value = "some value";
        AssignmentCommand command = new AssignmentCommand(varName, value);
        ExecutionResult result = command.execute();

        assertTrue(result.isSuccess());
        assertEquals("", result.getOutput());
        assertEquals(value, SessionVariables.getInstance().get(varName));
    }

    @Test
    void execute_AssignsEmptyValue() {
        String varName = "EMPTY_VAR";
        String value = "";
        AssignmentCommand command = new AssignmentCommand(varName, value);
        ExecutionResult result = command.execute();

        assertTrue(result.isSuccess());
        assertEquals("", result.getOutput());
        assertEquals(value, SessionVariables.getInstance().get(varName));
    }

    @Test
    void execute_OverridesExistingVariable() {
        String varName = "TEST_VAR";
        SessionVariables.getInstance().set(varName, "initial value");
        String newValue = "new value";
        AssignmentCommand command = new AssignmentCommand(varName, newValue);
        ExecutionResult result = command.execute();

        assertTrue(result.isSuccess());
        assertEquals("", result.getOutput());
        assertEquals(newValue, SessionVariables.getInstance().get(varName));
    }

    @Test
    void putArgs_DoesNothing() {
        String varName = "VAR";
        String value = "val";
        AssignmentCommand command = new AssignmentCommand(varName, value);
        command.putArgs("some args");
        ExecutionResult result = command.execute();

        assertTrue(result.isSuccess());
        assertEquals(value, SessionVariables.getInstance().get(varName));
    }
}
