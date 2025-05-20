package com.example.command;

import com.example.utils.ExecutionResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WcTest {

    @TempDir
    Path tempDir;
    private Path existingFile1;
    private Path existingFile2;

    @BeforeEach
    void setUp() throws IOException {
        existingFile1 = tempDir.resolve("file1.txt");
        Files.writeString(existingFile1, "This is line one.\nThis is line two.");
        existingFile2 = tempDir.resolve("file2.txt");
        Files.writeString(existingFile2, "One word\nTwo words here.\n");
    }

    @Test
    void execute_noArguments() {
        Wc wc = new Wc(null);
        ExecutionResult result = wc.execute();
        assertFalse(result.isSuccess());
        assertEquals("wc: missing arguments", result.getError());
        assertEquals("", result.getOutput());
    }

    @Test
    void execute_singleExistingFileWithoutNewLineInTheEnd() {
        Wc wc = new Wc(new String[]{existingFile1.toString()});
        ExecutionResult result = wc.execute();
        assertTrue(result.isSuccess());
        assertEquals(String.format("      1        8       35 %s", existingFile1), result.getOutput());
        assertEquals("", result.getError());
    }

    @Test
    void execute_singleExistingFileWithNewLineInTheEnd() {
        Wc wc = new Wc(new String[]{existingFile2.toString()});
        ExecutionResult result = wc.execute();
        assertTrue(result.isSuccess());
        assertEquals(String.format("      2        5       25 %s", existingFile2), result.getOutput());
        assertEquals("", result.getError());
    }

    @Test
    void execute_singleNonExistingFile() {
        Wc wc = new Wc(new String[]{"nonexistent.txt"});
        ExecutionResult result = wc.execute();
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("wc: nonexistent.txt: No such file or directory"));
        assertEquals("", result.getOutput());
    }

    @Test
    void execute_multipleExistingFiles() {
        Wc wc = new Wc(new String[]{existingFile1.toString(), existingFile2.toString()});
        ExecutionResult result = wc.execute();
        assertTrue(result.isSuccess());
        String expectedOutput = String.format("""
                      1        8       35 %s
                      2        5       25 %s
                      3       13       60 total\
                """, existingFile1, existingFile2);
        assertEquals(expectedOutput, result.getOutput());
        assertEquals("", result.getError());
    }

    @Test
    void execute_multipleFilesOneNonExisting() {
        Wc wc = new Wc(new String[]{existingFile1.toString(), "nonexistent.txt"});
        ExecutionResult result = wc.execute();
        assertFalse(result.isSuccess());
        String expectedOutput = String.format("""
                      1        8       35 %s
                wc: nonexistent.txt: No such file or directory
                      1        8       35 total\
                """, existingFile1);
        assertEquals(expectedOutput, result.getError());
    }

    @Test
    void execute_fromPipeInput() {
        Wc wc = new Wc(null);
        wc.putArgs("This is piped input.\nAnother line here.");
        ExecutionResult result = wc.execute();
        assertTrue(result.isSuccess());
        assertEquals("      2        7       39", result.getOutput());
        assertEquals("", result.getError());
    }

    @Test
    void execute_fromPipeInputWithFiles() {
        Wc wc = new Wc(new String[]{existingFile1.toString()});
        wc.putArgs("Should be ignored.");
        ExecutionResult result = wc.execute();
        assertTrue(result.isSuccess());
        assertEquals(String.format("      1        8       35 %s", existingFile1), result.getOutput());
        assertEquals("", result.getError());
    }
}
