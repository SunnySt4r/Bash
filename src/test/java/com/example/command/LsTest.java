package com.example.command;

import com.example.utils.ExecutionResult;
import org.junit.jupiter.api.Disabled;
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
        String args[] = { "src/test/testsForFolderLookup" };
        Ls lsCommand = new Ls(args);
        ExecutionResult result = lsCommand.execute();
        assertTrue(result.isSuccess());
        assertFalse(result.getOutput().isEmpty());
        assertEquals("a_first.file.txt\n" +
                "folder\n" +
                "visible_file.txt", result.getOutput());
        assertEquals("", result.getError());
    }

    @Test
    void putArgs_FailsWhenIncorrectDataPassed() {
        Ls lsCommand = new Ls();
        lsCommand.putArgs("bruh");
        ExecutionResult result = lsCommand.execute();
        assertFalse(result.isSuccess());
        assertEquals("ls: cannot access '" + "bruh" + "': No such file or directory", result.getError());
    }

    @Test
    void putArgs_GetIfDirectory() {
        Ls lsCommand = new Ls();
        lsCommand.putArgs("src/test/testsForFolderLookup");
        ExecutionResult result = lsCommand.execute();
        assertTrue(result.isSuccess());
        assertFalse(result.getOutput().isEmpty());
        assertEquals("a_first.file.txt\n" +
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

    @Test
    void execute_MultipleDirectories() {
        String args[] = {"src/test/testsForFolderLookup", "src/test/testsForFolderLookup/folder"};
        Ls lsCommand = new Ls(args);
        ExecutionResult result = lsCommand.execute();
        assertTrue(result.isSuccess());
        assertFalse(result.getOutput().isEmpty());
        String ans = "src/test/testsForFolderLookup:\n" +
                "a_first.file.txt\n" +
                "folder\n" +
                "visible_file.txt\n" +
                "\n" +
                "src/test/testsForFolderLookup/folder:\n" +
                "a_first_file_inside_folder.txt\n" +
                "file_inside_folder.txt";
        assertEquals(ans, result.getOutput());
        assertEquals("", result.getError());
    }

    @Test
    void putArgs_MultipleDirectories() {
        String args = "src/test/testsForFolderLookup src/test/testsForFolderLookup/folder";
        Ls lsCommand = new Ls();
        lsCommand.putArgs(args);
        ExecutionResult result = lsCommand.execute();
        assertTrue(result.isSuccess());
        assertFalse(result.getOutput().isEmpty());
        String ans = "src/test/testsForFolderLookup:\n" +
                "a_first.file.txt\n" +
                "folder\n" +
                "visible_file.txt\n" +
                "\n" +
                "src/test/testsForFolderLookup/folder:\n" +
                "a_first_file_inside_folder.txt\n" +
                "file_inside_folder.txt";
        assertEquals(ans, result.getOutput());
        assertEquals("", result.getError());
    }

    @Test
    void putArgs_MultiplePutArgsCleansAll() {
        String args[] = {"src/test/testsForFolderLookup", "src/test/testsForFolderLookup/folder"};
        Ls lsCommand = new Ls();
        lsCommand.putArgs(args[0]);
        lsCommand.putArgs(args[1]);
        ExecutionResult result = lsCommand.execute();
        assertTrue(result.isSuccess());
        assertFalse(result.getOutput().isEmpty());
        String ans =
                "a_first_file_inside_folder.txt\n" +
                "file_inside_folder.txt";
        assertEquals(ans, result.getOutput());
        assertEquals("", result.getError());
    }

    @Test
    void execute_MultipleFiles() {
        String args[] = {"src/test/testsForFolderLookup/a_first.file.txt", "src/test/testsForFolderLookup/visible_file.txt"};
        Ls lsCommand = new Ls(args);
        ExecutionResult result = lsCommand.execute();
        assertTrue(result.isSuccess());
        assertFalse(result.getOutput().isEmpty());
        String ans =
                "src/test/testsForFolderLookup/a_first.file.txt\n" +
                "src/test/testsForFolderLookup/visible_file.txt";
        assertEquals(ans, result.getOutput());
        assertEquals("", result.getError());
    }

    @Test
    void execute_FilesAndDirectory() {
        String args[] = {"src/test/testsForFolderLookup/a_first.file.txt", "src/test/testsForFolderLookup/folder"};
        Ls lsCommand = new Ls(args);
        ExecutionResult result = lsCommand.execute();
        assertTrue(result.isSuccess());
        assertFalse(result.getOutput().isEmpty());
        String ans =
                "src/test/testsForFolderLookup/a_first.file.txt\n" +
                "src/test/testsForFolderLookup/folder:\n" +
                "a_first_file_inside_folder.txt\n" +
                "file_inside_folder.txt";
        assertEquals(ans, result.getOutput());
        assertEquals("", result.getError());
    }

    @Test
    void execute_ErrorAndFiles() {
        String args[] = {"bruh", "src/test/testsForFolderLookup/a_first.file.txt"};
        Ls lsCommand = new Ls(args);
        ExecutionResult result = lsCommand.execute();
        assertFalse(result.isSuccess());
        assertTrue(result.getOutput().isEmpty());
        String ans =
                "ls: cannot access '" + "bruh" + "': No such file or directory\n" +
                "src/test/testsForFolderLookup/a_first.file.txt";
        assertEquals(ans, result.getError());
    }

}
