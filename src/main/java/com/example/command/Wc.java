package com.example.command;

import com.example.utils.ExecutionResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Wc extends Command {

    private final String[] arguments;
    private String pipeInput = null;
    private long totalLines = 0;
    private long totalWords = 0;
    private long totalBytes = 0;

    public Wc(String[] arguments) {
        this.arguments = arguments;
    }

    @Override
    public ExecutionResult execute() {
        if (arguments == null && pipeInput == null) {
            return new ExecutionResult(false, "wc: missing arguments");
        }

        totalLines = 0;
        totalWords = 0;
        totalBytes = 0;
        StringBuilder output = new StringBuilder();
        boolean success = true;

        if (pipeInput != null && arguments == null) {
            processInput(pipeInput, null, output);
        } else {
            for (String filename : arguments) {
                Path path = Paths.get(filename);
                try {
                    if (Files.exists(path)) {
                        String content = Files.readString(path);
                        processInput(content, filename, output);
                    } else {
                        output.append("wc: ").append(filename).append(": No such file or directory\n");
                        success = false;
                    }
                } catch (IOException e) {
                    output.append("wc: ").append(filename).append(": ").append(e.getMessage()).append("\n");
                    success = false;
                }
            }

            if (arguments.length > 1) {
                output.append(String.format("%7d %8d %8d total\n", totalLines, totalWords, totalBytes));
            }
        }

        String result = output.toString().stripTrailing();
        return new ExecutionResult(success, result);
    }

    private void processInput(String input, String source, StringBuilder output) {
        long lines = 0;
        long words = 0;
        long bytes = 0;

        if (input != null) {
            bytes = input.getBytes().length;
            lines = input.split("\n").length;
            if (!input.endsWith("\n") && source != null) {
                if (lines > 0) {
                    // Если нет завершающей новой строки, но есть хотя бы одна строка,
                    // оригинальный wc обычно не учитывает последнюю "незавершенную" строку
                    lines--;
                }
            }

            String[] wordList = input.split("\\s+");
            for (String word : wordList) {
                if (!word.isEmpty()) {
                    words++;
                }
            }

            totalLines += lines;
            totalWords += words;
            totalBytes += bytes;
        }

        if (source != null) {
            output.append(String.format("%7d %8d %8d %s\n", lines, words, bytes, source));
        } else {
            output.append(String.format("%7d %8d %8d\n", lines, words, bytes));
        }

    }

    @Override
    public void putArgs(String args) {
        this.pipeInput = args;
    }
}
