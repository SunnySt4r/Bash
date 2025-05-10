package com.example.command;

import com.example.utils.ExecutionResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LsTest {

    @Test
    void execute_NoArguments_ListsFilesInCurrentDirectory() {
        Ls lsCommand = new Ls();
        ExecutionResult result = lsCommand.execute();
        assertTrue(result.isSuccess());
        assertFalse(result.getOutput().isEmpty());
        assertTrue(result.getOutput().contains("pom.xml"));
        assertTrue(result.getOutput().contains("src"));
        assertEquals("", result.getError());
    }

    @Test
    void putArgs_DoesNothing() {
        Ls lsCommand = new Ls();
        lsCommand.putArgs("some input from pipe");
        ExecutionResult result = lsCommand.execute();
        assertTrue(result.isSuccess());
        assertFalse(result.getOutput().isEmpty()); // putArgs не должен изменять поведение ls
        assertEquals("", result.getError());
    }
}
