package com.example.command;

import com.example.SessionVariables;
import com.example.utils.ExecutionResult;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class CdTest {
    SessionVariables sessionVars = SessionVariables.getInstance();

    String getExpectedDirectory(String targetDir) {
        String root = "";
        if(targetDir.equals("~")) {
            root = System.getProperty("user.home");
            targetDir = "";
        } else if (targetDir.startsWith("~/")) {
            root = System.getProperty("user.home");
            targetDir = targetDir.substring(2, targetDir.length());
        } else {
            root = System.getProperty("user.dir");
        }
        String expectedDir = root.replace("\\", "/") + "/" + targetDir;
        File dir = new File(expectedDir);
        expectedDir = dir.getAbsolutePath();
        return expectedDir;
    }

    String getActualDirectory() {
        String actualDir = sessionVars.get("PWD");
        sessionVars.set("PWD", System.getProperty("user.dir"));
        return actualDir;
    }

    @Test
    void execute_CdNoArgs() {
        Cd cdCommand = new Cd();
        ExecutionResult result = cdCommand.execute();

        String expectedDir = getExpectedDirectory("~");
        String actualDir = getActualDirectory();

        assertTrue(result.isSuccess());
        assertEquals("", result.getError());
        assertEquals(expectedDir, actualDir);
    }

    @Test
    void execute_CdFromHome() {
        String args[]  = {"~/"};
        Cd cdCommand = new Cd(args);
        ExecutionResult result = cdCommand.execute();

        String expectedDir = getExpectedDirectory("~");
        String actualDir = getActualDirectory();

        assertTrue(result.isSuccess());
        assertEquals("", result.getError());
        assertEquals(expectedDir, actualDir);
    }

    @Test
    void execute_CdArgsEmptyList() {
        String args[]  = {};
        Cd cdCommand = new Cd(args);
        ExecutionResult result = cdCommand.execute();

        String expectedDir = getExpectedDirectory("~");
        String actualDir = getActualDirectory();

        assertTrue(result.isSuccess());
        assertEquals("", result.getError());
        assertEquals(expectedDir, actualDir);
    }

    @Test
    void execute_WithOneArg() {
        String args[]  = {"src/test/testsForFolderLookup"};
        Cd cdCommand = new Cd(args);
        ExecutionResult result = cdCommand.execute();

        String expectedDir = getExpectedDirectory(args[0]);
        String actualDir = getActualDirectory();

        assertTrue(result.isSuccess());
        assertEquals("", result.getError());
        assertEquals(expectedDir, actualDir);
    }

    @Test
    void execute_WithTwoArg() {
        String args[]  = {"src/test/testsForFolderLookup", "src/test/testsForFolderLookup/folder"};
        Cd cdCommand = new Cd(args);
        ExecutionResult result = cdCommand.execute();

        String expectedDir = getExpectedDirectory(args[0]);
        String actualDir = getActualDirectory();

        assertTrue(result.isSuccess());
        assertEquals("", result.getError());
        assertEquals(expectedDir, actualDir);
    }

    @Test
    void execute_notADirectory() {
        String args[]  = {"src/test/testsForFolderLookup/visible_file.txt"};
        Cd cdCommand = new Cd(args);
        ExecutionResult result = cdCommand.execute();

        String expectedDir = getExpectedDirectory("");
        String actualDir = getActualDirectory();

        assertFalse(result.isSuccess());
        assertEquals("cd: " + args[0] + ": Not a directory", result.getError());
        assertEquals(expectedDir, actualDir);
    }

    @Test
    void execute_PWDnull() {
        String args[]  = {"src/test/testsForFolderLookup"};
        Cd cdCommand = new Cd(args);
        ExecutionResult result = cdCommand.execute();

        String expectedDir = getExpectedDirectory(args[0]);
        String actualDir = getActualDirectory();

        assertTrue(result.isSuccess());
        assertEquals("", result.getError());
        assertEquals(expectedDir, actualDir);
    }

    @Test
    void execute_CannotFindDirectory() {
        String args[]  = {"bruh"};
        Cd cdCommand = new Cd(args);
        ExecutionResult result = cdCommand.execute();

        String expectedDir = getExpectedDirectory("");
        String actualDir = getActualDirectory();

        assertFalse(result.isSuccess());
        assertEquals("cd: " + "bruh" + ": No such file or directory", result.getError());
        assertEquals(expectedDir, actualDir);
    }

    @Test
    void PutArgs_simple() {
        String args[]  = {"src/test/testsForFolderLookup"};
        Cd cdCommand = new Cd();
        cdCommand.putArgs(args[0]);
        ExecutionResult result = cdCommand.execute();

        String expectedDir = getExpectedDirectory(args[0]);
        String actualDir = getActualDirectory();

        assertTrue(result.isSuccess());
        assertEquals("", result.getError());
        assertEquals(expectedDir, actualDir);
    }

    @Test
    void putArgs_OverwritesEverythingPut() {
        String args[]  = {"src/test/testsForFolderLookup/", "src/test/testsForFolderLookup/folder"};
        Cd cdCommand = new Cd(args);
        cdCommand.putArgs(args[1]);
        ExecutionResult result = cdCommand.execute();

        String expectedDir = getExpectedDirectory(args[1]);
        String actualDir = getActualDirectory();

        assertTrue(result.isSuccess());
        assertEquals("", result.getError());
        assertEquals(expectedDir, actualDir);
    }

}
