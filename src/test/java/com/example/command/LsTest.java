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
    void execute_LsWithArguments() {
        String args[] = {"src/test/testsForFolderLookup"};
        Ls lsCommand = new Ls(args);
        ExecutionResult result = lsCommand.execute();
        assertTrue(result.isSuccess());
        assertFalse(result.getOutput().isEmpty());
        assertEquals("a_first.file.txt\n" +
                "empty_folder\n" +
                "folder\n" +
                "visible_file.txt", result.getOutput());
        assertEquals("", result.getError());
    }

    @Test
    void putArgs_FailsWhenIncorrectDataPassed() {
        Ls lsCommand = new Ls();
        lsCommand.putArgs("some input from pipe");
        ExecutionResult result = lsCommand.execute();
        assertFalse(result.isSuccess());
        assertEquals("ls: cannot access '" + "some input from pipe" + "': No such file or directory", result.getError());
    }

    @Test
    void putArgs_GetIfDirectory() {
        Ls lsCommand = new Ls();
        lsCommand.putArgs("src/test/testsForFolderLookup");
        ExecutionResult result = lsCommand.execute();
        assertTrue(result.isSuccess());
        assertFalse(result.getOutput().isEmpty());
        assertEquals("a_first.file.txt\n" +
                "empty_folder\n" +
                "folder\n" +
                "visible_file.txt", result.getOutput());
        assertEquals("", result.getError());
    }

    @Test
    void putArgs_GetIfNotDirectory() {
        Ls lsCommand = new Ls();
        lsCommand.putArgs("src/test/testsForFolderLookup/visible_file.txt");
        ExecutionResult result = lsCommand.execute();
        assertTrue(result.isSuccess());
        assertFalse(result.getOutput().isEmpty());
        assertEquals("src/test/testsForFolderLookup/visible_file.txt", result.getOutput());
        assertEquals("", result.getError());
    }

    @Test
    void putArgs_GetHome() {
        Ls lsCommand = new Ls();
        lsCommand.putArgs("~");
        ExecutionResult result = lsCommand.execute();
        assertTrue(result.isSuccess());
        assertFalse(result.getOutput().isEmpty());
        assertEquals("", result.getError());
    }


}
