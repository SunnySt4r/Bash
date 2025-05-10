package com.example.command;

import com.example.utils.ExecutionResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GrepTest {

    @TempDir
    Path tempDir;

    private Path existingFile1;
    private Path existingFile2;

    @BeforeEach
    void setUp() throws IOException {
        existingFile1 = tempDir.resolve("test.txt");
        String fileContent1 = """
                This line contains the word test.
                Another line without the test.
                TESTING in uppercase.
                line with test at the end test
                context before
                match with context
                context after 1
                context after 2
                another match
                """;
        Files.writeString(existingFile1, fileContent1);

        existingFile2 = tempDir.resolve("test2.txt");
        String fileContent2 = """
                This is a line with the word test.
                Another line.
                This line also has test in it.
                And one more line.
                TESTING uppercase.""";
        Files.writeString(existingFile2, fileContent2);
    }

    @Test
    void parseArguments_IgnoreCaseFlag() {
        Grep grep = new Grep(new String[]{"-i", "test", "file.txt", "file2.txt"});
        assertTrue(grep.isIgnoreCase());
        assertEquals("test", grep.getPattern());
        assertEquals(List.of("file.txt", "file2.txt"), grep.getFiles());
    }

    @Test
    void parseArguments_CountOnlyFlag() {
        Grep grep = new Grep(new String[]{"-c", "test", "file.txt"});
        assertTrue(grep.isCountOnly());
        assertEquals("test", grep.getPattern());
        assertEquals(List.of("file.txt"), grep.getFiles());
    }

    @Test
    void parseArguments_FileNamesOnlyFlag() {
        Grep grep = new Grep(new String[]{"-l", "test", "file.txt"});
        assertTrue(grep.isFileNamesOnly());
        assertEquals("test", grep.getPattern());
        assertEquals(List.of("file.txt"), grep.getFiles());
    }

    @Test
    void parseArguments_WholeWordFlag() {
        Grep grep = new Grep(new String[]{"-w", "test", "file.txt"});
        assertTrue(grep.isWholeWord());
        assertEquals("test", grep.getPattern());
        assertEquals(List.of("file.txt"), grep.getFiles());
    }

    @Test
    void parseArguments_AfterContextFlagWithValueAttached() {
        Grep grep = new Grep(new String[]{"-A2", "test", "file.txt"});
        assertEquals(2, grep.getAfterContext());
        assertEquals("test", grep.getPattern());
        assertEquals(List.of("file.txt"), grep.getFiles());
    }

    @Test
    void parseArguments_AfterContextFlagWithValueSeparate() {
        Grep grep = new Grep(new String[]{"-A", "3", "test", "file.txt"});
        assertEquals(3, grep.getAfterContext());
        assertEquals("test", grep.getPattern());
        assertEquals(List.of("file.txt"), grep.getFiles());
    }

    @Test
    void parseArguments_InvalidAfterContextValueAttached() {
        Grep grep = new Grep(new String[]{"-Ax", "test", "file.txt"});
        assertEquals("For input string: \"x\"", grep.getExceptionMessage());
        assertNull(grep.getPattern());
        assertTrue(grep.getFiles().isEmpty());
    }

    @Test
    void parseArguments_InvalidAfterContextValueSeparate() {
        Grep grep = new Grep(new String[]{"-A", "y", "test", "file.txt"});
        assertEquals("For input string: \"y\"", grep.getExceptionMessage());
        assertNull(grep.getPattern());
        assertTrue(grep.getFiles().isEmpty());
    }

    @Test
    void parseArguments_MultipleOptions() {
        Grep grep = new Grep(new String[]{"-i", "-c", "-icA3", "test", "file.txt"});
        assertTrue(grep.isIgnoreCase());
        assertTrue(grep.isCountOnly());
        assertEquals(3, grep.getAfterContext());
        assertEquals("test", grep.getPattern());
        assertEquals(List.of("file.txt"), grep.getFiles());
    }

    @Test
    void parseArguments_InvalidOption() {
        Grep grep = new Grep(new String[]{"-p", "test", "file.txt"});
        assertEquals("grep: invalid option -- 'p'", grep.getExceptionMessage());
        assertNull(grep.getPattern());
        assertTrue(grep.getFiles().isEmpty());
    }

    @Test
    void parseArguments_MissingPattern() {
        Grep grep = new Grep(new String[]{"-i", "-c", "file.txt"});
        assertEquals("file.txt", grep.getPattern());
        assertTrue(grep.getFiles().isEmpty());
    }

    @Test
    void parseArguments_PatternWithQuotes() {
        Grep grep1 = new Grep(new String[]{"\"test word\"", "file.txt"});
        assertEquals("test word", grep1.getPattern());
        Grep grep2 = new Grep(new String[]{"'test word'", "file.txt"});
        assertEquals("test word", grep2.getPattern());
    }

    @Test
    void execute_ExistingFileNoMatch() {
        Grep grep = new Grep(new String[]{"nomatch", existingFile1.toString()});
        ExecutionResult result = grep.execute();
        assertTrue(result.isSuccess());
        assertEquals("", result.getOutput());
        assertEquals("", result.getError());
    }

    @Test
    void execute_MissingFiles() {
        Grep grep = new Grep(new String[]{"pattern"});
        ExecutionResult result = grep.execute();
        assertFalse(result.isSuccess());
        assertEquals("grep: no input files", result.getError());
    }

    @Test
    void execute_ExistingFileSingleMatch() {
        Grep grep = new Grep(new String[]{"word", existingFile1.toString()});
        ExecutionResult result = grep.execute();
        assertTrue(result.isSuccess());
        assertEquals("This line contains the word test.", result.getOutput().trim());
        assertEquals("", result.getError());
    }

    @Test
    void execute_TwoExistingFilesMatches() {
        Grep grep = new Grep(new String[]{"word", existingFile1.toString(), existingFile2.toString()});
        ExecutionResult result = grep.execute();
        assertTrue(result.isSuccess());
        assertTrue(result.getOutput().contains(existingFile1.toString() + ":This line contains the word test."));
        assertTrue(result.getOutput().contains(existingFile2.toString() + ":This is a line with the word test."));
        assertEquals("", result.getError());
    }

    @Test
    void execute_ExistingFileIgnoreCaseMatch() {
        Grep grep = new Grep(new String[]{"-i", "testing", existingFile1.toString()});
        ExecutionResult result = grep.execute();
        assertTrue(result.isSuccess());
        assertEquals("TESTING in uppercase.", result.getOutput().trim());
        assertEquals("", result.getError());
    }

    @Test
    void execute_ExistingFileCountOnly() {
        Grep grep = new Grep(new String[]{"-c", "test", existingFile1.toString(), existingFile2.toString()});
        ExecutionResult result = grep.execute();
        assertTrue(result.isSuccess());
        assertTrue(result.getOutput().contains(existingFile1.toString() + ":3"));
        assertTrue(result.getOutput().contains(existingFile2.toString() + ":2"));
        assertEquals("", result.getError());
    }

    @Test
    void execute_ExistingFileFileNamesOnlyMatch() {
        Grep grep = new Grep(new String[]{"-l", "test", existingFile1.toString(), existingFile2.toString()});
        ExecutionResult result = grep.execute();
        assertTrue(result.isSuccess());
        assertEquals(existingFile1.toString() + '\n' + existingFile2.toString(), result.getOutput().trim());
        assertEquals("", result.getError());
    }

    @Test
    void execute_ExistingFileFileNamesOnlyMatchOneMatching() {
        Grep grep = new Grep(new String[]{"-l", "match", existingFile1.toString(), existingFile2.toString()});
        ExecutionResult result = grep.execute();
        assertTrue(result.isSuccess());
        assertEquals(existingFile1.toString(), result.getOutput().trim());
        assertEquals("", result.getError());
    }

    @Test
    void execute_ExistingFileFileNamesOnlyNoMatch() {
        Grep grep = new Grep(new String[]{"-l", "nomatch", existingFile1.toString()});
        ExecutionResult result = grep.execute();
        assertTrue(result.isSuccess());
        assertEquals("", result.getOutput());
        assertEquals("", result.getError());
    }

    @Test
    void execute_ExistingFileWholeWordMatch() {
        Grep grep = new Grep(new String[]{"-w", "test", existingFile1.toString()});
        ExecutionResult result = grep.execute();
        assertTrue(result.isSuccess());
        assertEquals("""
                This line contains the word test.
                Another line without the test.
                line with test at the end test""", result.getOutput().trim());
        assertEquals("", result.getError());
    }

    @Test
    void execute_ExistingFileAfterContext() {
        Grep grep = new Grep(new String[]{"-A", "2", "match with", existingFile1.toString()});
        ExecutionResult result = grep.execute();
        assertTrue(result.isSuccess());
        assertEquals("""
                match with context
                context after 1
                context after 2""", result.getOutput().trim());
        assertEquals("", result.getError());
    }

    @Test
    void execute_ExistingFileAfterContextEndOfFile() {
        Grep grep = new Grep(new String[]{"-A", "2", "another", existingFile1.toString()});
        ExecutionResult result = grep.execute();
        assertTrue(result.isSuccess());
        assertEquals("another match", result.getOutput().trim());
        assertEquals("", result.getError());
    }

    @Test
    void execute_ExistingFileAfterContextTwoMatches() {
        Grep grep = new Grep(new String[]{"-A", "2", "match", existingFile1.toString()});
        ExecutionResult result = grep.execute();
        assertTrue(result.isSuccess());
        assertEquals("""
                match with context
                context after 1
                context after 2
                --
                another match""", result.getOutput().trim());
        assertEquals("", result.getError());
    }

    @Test
    void execute_InvalidAfterContextValueAttached() {
        Grep grep = new Grep(new String[]{"-Ax", "test", "file.txt"});
        ExecutionResult result = grep.execute();
        assertFalse(result.isSuccess());
        assertEquals("For input string: \"x\"", result.getError());
    }

    @Test
    void execute_NonExistingFile() {
        Grep grep = new Grep(new String[]{"test", "nonexistent.txt"});
        ExecutionResult result = grep.execute();
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("grep: nonexistent.txt: No such file or directory"));
        assertEquals("", result.getOutput());
    }

    @Test
    void execute_MultipleFilesOneExistsOneNot() {
        Path nonExistingFile = tempDir.resolve("nonexistent.txt");
        Grep grep = new Grep(new String[]{"word", existingFile1.toString(), nonExistingFile.toString()});
        ExecutionResult result = grep.execute();
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains(existingFile1 + ":This line contains the word test."));
        assertTrue(result.getError().contains("grep: " + nonExistingFile + ": No such file or directory"));
    }

    // Тестирование execute() с pipeInput
    @Test
    void execute_PipeInputNoMatch() {
        Grep grep = new Grep(new String[]{"nomatch"});
        grep.putArgs("input line 1\ninput line 2");
        ExecutionResult result = grep.execute();
        assertTrue(result.isSuccess());
        assertEquals("", result.getOutput());
        assertEquals("", result.getError());
    }

    @Test
    void execute_PipeInputSingleMatch() {
        Grep grep = new Grep(new String[]{"first"});
        grep.putArgs("first line\nsecond line");
        ExecutionResult result = grep.execute();
        assertTrue(result.isSuccess());
        assertEquals("first line", result.getOutput().trim());
        assertEquals("", result.getError());
    }

    @Test
    void execute_PipeInputDoubleMatch() {
        Grep grep = new Grep(new String[]{"line"});
        grep.putArgs("first line\nsecond line");
        ExecutionResult result = grep.execute();
        assertTrue(result.isSuccess());
        assertEquals("first line\nsecond line", result.getOutput().trim());
        assertEquals("", result.getError());
    }

    @Test
    void execute_PipeInputIgnoreCaseMatch() {
        Grep grep = new Grep(new String[]{"-i", "FIRST"});
        grep.putArgs("first line");
        ExecutionResult result = grep.execute();
        assertTrue(result.isSuccess());
        assertEquals("first line", result.getOutput().trim());
        assertEquals("", result.getError());
    }

    @Test
    void execute_PipeInputCountOnly() {
        Grep grep = new Grep(new String[]{"-c", "line"});
        grep.putArgs("first line\nsecond line\nthird line");
        ExecutionResult result = grep.execute();
        assertTrue(result.isSuccess());
        assertEquals("3", result.getOutput().trim());
        assertEquals("", result.getError());
    }

    @Test
    void execute_PipeInputAfterContext() {
        Grep grep = new Grep(new String[]{"-A", "1", "match"});
        grep.putArgs("line before\nline with match\nline after 1\nline after 2");
        ExecutionResult result = grep.execute();
        assertTrue(result.isSuccess());
        assertEquals("line with match\nline after 1", result.getOutput().trim());
        assertEquals("", result.getError());
    }

    @Test
    void execute_PipeInputWithSpecifiedFiles() {
        Grep grep = new Grep(new String[]{"test", existingFile1.toString(), existingFile2.toString()});
        grep.putArgs("test line 1\ntest line 2");
        ExecutionResult result = grep.execute();
        assertTrue(result.isSuccess());
        assertTrue(result.getOutput().contains("(standard input):test line 1\n(standard input):test line 2"));
        assertTrue(result.getOutput().contains(existingFile1 + ":This line contains the word test."));
        assertTrue(result.getOutput().contains(existingFile2 + ":This is a line with the word test."));
        assertEquals("", result.getError());
    }

    // Тестирование wildcard'ов в именах файлов
    @Test
    void execute_WildcardExistingFiles() {
        Grep grep = new Grep(new String[]{"-l", "test", tempDir.toString() + "/*.txt"});
        ExecutionResult result = grep.execute();
        assertTrue(result.isSuccess());
        assertTrue(result.getOutput().contains(tempDir + "/test.txt"));
        assertTrue(result.getOutput().contains(tempDir + "/test2.txt"));
        assertEquals("", result.getError());
    }

    @Test
    void execute_WildcardNoMatchingFiles() {
        Grep grep = new Grep(new String[]{"test", tempDir.toString() + "/nomatch*.txt"});
        ExecutionResult result = grep.execute();
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("grep: " + tempDir.toString() + "/nomatch*.txt: No such file or directory"));
        assertEquals("", result.getOutput());
    }

    @Test
    void execute_WildcardNoSuchDir() {
        Grep grep = new Grep(new String[]{"test", "dir.txt/*.txt"});
        ExecutionResult result = grep.execute();
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("grep: cannot access 'dir.txt/*.txt': No such file or directory"));
        assertEquals("", result.getOutput());
    }

    @Test
    void execute_MissingPatternNoPipeInputNoFiles() {
        Grep grep = new Grep(new String[]{});
        ExecutionResult result = grep.execute();
        assertFalse(result.isSuccess());
        assertEquals("grep: missing pattern", result.getError());
        assertEquals("", result.getOutput());
    }

    @Test
    void putArgs_SetsPipeInput() {
        Grep grep = new Grep(new String[]{"test", "file.txt"});
        grep.putArgs("piped data");
    }
}
