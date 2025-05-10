package com.example.command;

import com.example.utils.ExecutionResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Grep extends Command {
    private final String[] arguments;
    private boolean ignoreCase = false;
    private boolean countOnly = false;
    private boolean fileNamesOnly = false;
    private boolean wholeWord = false;
    private int afterContext = 0;
    private String pattern;
    private String pipeInput = null;
    private final List<String> files = new ArrayList<>();
    private String exceptionMessage = "";

    public Grep(String[] arguments) {
        this.arguments = arguments;
        parseArguments();
    }

    public boolean isIgnoreCase() {
        return ignoreCase;
    }

    public boolean isCountOnly() {
        return countOnly;
    }

    public boolean isFileNamesOnly() {
        return fileNamesOnly;
    }

    public boolean isWholeWord() {
        return wholeWord;
    }

    public int getAfterContext() {
        return afterContext;
    }

    public String getPattern() {
        return pattern;
    }

    public List<String> getFiles() {
        return new ArrayList<>(files);
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    private void parseArguments() {
        if (arguments == null || arguments.length == 0) {
            return;
        }

        int i = 0;
        while (i < arguments.length && arguments[i].startsWith("-")) {
            String arg = arguments[i];
            for (int j = 1; j < arg.length(); j++) {
                char option = arg.charAt(j);
                switch (option) {
                    case 'i':
                        ignoreCase = true;
                        break;
                    case 'c':
                        countOnly = true;
                        break;
                    case 'l':
                        fileNamesOnly = true;
                        break;
                    case 'w':
                        wholeWord = true;
                        break;
                    case 'A':
                        if (j + 1 < arg.length()) {
                            try {
                                afterContext = Integer.parseInt(arg.substring(j + 1));
                                j = arg.length();
                            } catch (NumberFormatException e) {
                                exceptionMessage = e.getMessage();
                                return;
                            }
                        } else if (i + 1 < arguments.length) {
                            try {
                                afterContext = Integer.parseInt(arguments[i + 1]);
                                i++;
                            } catch (NumberFormatException e) {
                                exceptionMessage = e.getMessage();
                                return;
                            }
                        }
                        break;
                }
            }
            i++;
        }

        if (i < arguments.length) {
            pattern = arguments[i++];
            if (pattern.startsWith("\"") && pattern.endsWith("\"") && pattern.length() > 1) {
                pattern = pattern.substring(1, pattern.length() - 1);
            } else if (pattern.startsWith("'") && pattern.endsWith("'") && pattern.length() > 1) {
                pattern = pattern.substring(1, pattern.length() - 1);
            }
        }

        while (i < arguments.length) {
            files.add(arguments[i++]);
        }
    }

    @Override
    public ExecutionResult execute() {
        if (!exceptionMessage.isEmpty()) {
            return new ExecutionResult(false, exceptionMessage);
        }

        if (pattern == null && pipeInput == null) {
            return new ExecutionResult(false, "grep: missing pattern");
        }

        if (pipeInput != null && pattern == null && arguments != null && arguments.length > 0) {
            pattern = arguments[0];
            files.addAll(Arrays.asList(arguments).subList(1, arguments.length));
        }

        if (pipeInput != null && files.isEmpty()) {
            return grepString(pipeInput, pattern, null);
        }

        if (files.isEmpty()) {
            return new ExecutionResult(false, "grep: no input files");
        }

        StringBuilder output = new StringBuilder();
        boolean success = true;

        List<String> expandedFiles = new ArrayList<>();
        for (String filePath : files) {
            if (filePath.contains("*") || filePath.contains("?") || filePath.contains("[")) {
                String dirPart = ".";
                String filePattern = filePath;

                int lastSlashIndex = filePath.lastIndexOf('/');
                if (lastSlashIndex >= 0) {
                    dirPart = filePath.substring(0, lastSlashIndex);
                    filePattern = filePath.substring(lastSlashIndex + 1);
                }

                File dir = new File(dirPart);
                if (!dir.isDirectory()) {
                    output.append("grep: cannot access '")
                            .append(filePath)
                            .append("': No such file or directory\n");
                    success = false;
                    continue;
                }

                String regex = filePattern
                        .replace(".", "\\.")
                        .replace("*", ".*")
                        .replace("?", ".");
                Pattern fileNamePattern = Pattern.compile(regex);

                File[] matchingFiles = dir.listFiles((f, name) -> fileNamePattern.matcher(name).matches());

                if (matchingFiles == null || matchingFiles.length == 0) {
                    output.append("grep: ").append(filePath)
                            .append(": No such file or directory\n");
                    success = false;
                } else {
                    for (File f : matchingFiles) {
                        if (dirPart.equals(".")) {
                            expandedFiles.add(f.getName());
                        } else {
                            expandedFiles.add(dirPart + "/" + f.getName());
                        }
                    }
                }
            } else {
                expandedFiles.add(filePath);
            }
        }

        boolean multipleFiles = expandedFiles.size() > 1;

        for (String filePath : expandedFiles) {
            File file = new File(filePath);

            if (!file.exists()) {
                output.append("grep: ").append(filePath).append(": No such file or directory\n");
                success = false;
                continue;
            }

            try {
                List<String> lines = Files.readAllLines(Paths.get(filePath));
                ExecutionResult result = grepList(lines, pattern, multipleFiles ? filePath : null);

                if (result.isSuccess() && !result.getOutput().isEmpty()) {
                    if (!output.isEmpty() && output.charAt(output.length() - 1) != '\n') {
                        output.append('\n');
                    }
                    output.append(result.getOutput());
                } else if (!result.isSuccess()) {
                    if (!output.isEmpty() && output.charAt(output.length() - 1) != '\n') {
                        output.append('\n');
                    }
                    output.append(result.getError());
                    success = false;
                }
            } catch (IOException e) {
                output.append("grep: ").append(filePath).append(": ").append(e.getMessage()).append("\n");
                success = false;
            }
        }

        if (pipeInput != null && !files.isEmpty()) {
            ExecutionResult result = grepString(pipeInput, pattern, multipleFiles ? "(standard input)" : null);
            if (result.isSuccess() && !result.getOutput().isEmpty()) {
                if (!output.isEmpty() && output.charAt(output.length() - 1) != '\n') {
                    output.append('\n');
                }
                output.append(result.getOutput());
            }
        }

        return new ExecutionResult(success, !output.isEmpty() ? output.toString().trim() : "");
    }

    private ExecutionResult grepString(String input, String searchPattern, String filename) {
        String[] lines = input.split("\n");
        List<String> linesList = new ArrayList<>(List.of(lines));
        return grepList(linesList, searchPattern, filename);
    }

    private ExecutionResult grepList(List<String> lines, String searchPattern, String filename) {
        Pattern regexPattern = createPattern(searchPattern);
        List<Integer> matchedLines = new ArrayList<>();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            Matcher matcher = regexPattern.matcher(line);
            if (matcher.find()) matchedLines.add(i);
        }

        if (matchedLines.isEmpty()) {
            return new ExecutionResult(true, "");
        }

        StringBuilder result = new StringBuilder();

        if (countOnly) {
            if (filename != null) {
                result.append(filename).append(":");
            }
            result.append(matchedLines.size());
            return new ExecutionResult(true, result.toString());
        }

        if (fileNamesOnly && filename != null) {
            return new ExecutionResult(true, filename);
        }

        boolean showFilename = filename != null;

        for (int i = 0; i < matchedLines.size(); i++) {
            int lineNum = matchedLines.get(i);
            if (showFilename) result.append(filename).append(":");
            result.append(lines.get(lineNum)).append("\n");

            if (afterContext > 0) {
                int end = Math.min(lineNum + afterContext, lines.size() - 1);

                for (int j = lineNum + 1; j <= end; j++) {
                    if (matchedLines.contains(j)) continue;
                    if (showFilename) result.append(filename).append("-");
                    result.append(lines.get(j)).append("\n");
                }

                if (i < matchedLines.size() - 1 && matchedLines.get(i + 1) > lineNum + afterContext) {
                    result.append("--\n");
                }
            }
        }

        return new ExecutionResult(true, result.toString().trim());
    }

    private Pattern createPattern(String searchPattern) {
        if (wholeWord) {
            searchPattern = "\\b" + searchPattern + "\\b";
        }

        int flags = ignoreCase ? Pattern.CASE_INSENSITIVE : 0;
        return Pattern.compile(searchPattern, flags);
    }

    @Override
    public void putArgs(String args) {
        this.pipeInput = args;
    }
}
