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

public class CatTest {

    @TempDir
    Path tempDir;

    private Path existingFile1Path;
    private String file1Content;
    private Path existingFile2Path;
    private String file2Content;
    private String nonExistingFile;

    @BeforeEach
    public void setUp() throws IOException {
        existingFile1Path = tempDir.resolve("file1.txt");
        file1Content = "Content of file 1.\nSecond line.";
        Files.writeString(existingFile1Path, file1Content);

        existingFile2Path = tempDir.resolve("file2.txt");
        file2Content = "Content of file 2.\nSecond line.";
        Files.writeString(existingFile2Path, file2Content);

        nonExistingFile = "missing.txt";
    }

    @Test
    void execute_ExistingFile_ReturnsFileContent() {
        Cat catCommand = new Cat(new String[]{existingFile1Path.toString()});
        ExecutionResult result = catCommand.execute();

        assertTrue(result.isSuccess());
        assertTrue(result.getOutput().contains(file1Content));
        assertEquals("", result.getError());
    }

    @Test
    void execute_NonExistingFile_ReturnsErrorMessage() {
        Cat catCommand = new Cat(new String[]{nonExistingFile});
        ExecutionResult result = catCommand.execute();

        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains(nonExistingFile + ": No such file"));
        assertEquals("", result.getOutput());
    }

    @Test
    void execute_MultipleFiles_ReturnsContentOfAllExistingFilesAndErrorsForNonExisting() {
        Cat catCommand = new Cat(new String[]{existingFile1Path.toString(), nonExistingFile, existingFile2Path.toString()});
        ExecutionResult result = catCommand.execute();

        assertTrue(result.isSuccess()); // Если хотя бы один файл прочитан успешно
        assertTrue(result.getOutput().contains(file1Content));
        assertTrue(result.getOutput().contains(file2Content));
        assertTrue(result.getError().contains(nonExistingFile + ": No such file"));
    }

    @Test
    void execute_NoArguments_ReturnsMissingArgumentsError() {
        Cat catCommand = new Cat(null);
        ExecutionResult result = catCommand.execute();

        assertFalse(result.isSuccess());
        assertEquals("cat: missing arguments", result.getError());
        assertEquals("", result.getOutput());
    }

    @Test
    void putArgs_NoInitialArguments_SetsStaticResultAndReturnsItOnExecute() {
        Cat catCommand = new Cat(null);
        String pipedInput = "content from pipe";
        catCommand.putArgs(pipedInput);
        ExecutionResult result = catCommand.execute();

        assertTrue(result.isSuccess());
        assertEquals(pipedInput, result.getOutput());
        assertEquals("", result.getError());
    }

    @Test
    void putArgs_WithInitialArguments_DoesNotAffectFileReading() throws IOException {
        Path filePath = tempDir.resolve("initial.txt");
        String fileContent = "Initial file content.";
        Files.writeString(filePath, fileContent);

        Cat catCommand = new Cat(new String[]{filePath.toString()});
        String pipedInput = "this should not be read";
        catCommand.putArgs(pipedInput);
        ExecutionResult result = catCommand.execute();

        assertTrue(result.isSuccess());
        assertTrue(result.getOutput().contains(fileContent));
        assertEquals("", result.getError());
    }

    @Test
    void execute_AbsolutePath_ReturnsFileContent() throws IOException {
        Path absoluteFilePath = tempDir.resolve("absolute.txt").toAbsolutePath();
        String absoluteFileContent = "Absolute path content.";
        Files.writeString(absoluteFilePath, absoluteFileContent);

        Cat catCommand = new Cat(new String[]{absoluteFilePath.toString()});
        ExecutionResult result = catCommand.execute();

        assertTrue(result.isSuccess());
        assertTrue(result.getOutput().contains(absoluteFileContent));
        assertEquals("", result.getError());
    }

    @Test
    void execute_DirectoryPath_ReturnsErrorMessage() throws IOException {
        Path directoryPath = tempDir.resolve("directory");
        Files.createDirectory(directoryPath);

        Cat catCommand = new Cat(new String[]{directoryPath.toString()});
        ExecutionResult result = catCommand.execute();

        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains(directoryPath.toString() + ": It's a directory"));
        assertEquals("", result.getOutput());
    }

}
