package com.example.command;

import com.example.SessionVariables;
import com.example.utils.ExecutionResult;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class Ls extends Command {
    private List<String> directories;

    public Ls() {
        directories = new ArrayList<>();
        directories.add(".");
    }

    public Ls(String[] args) {
        directories = new ArrayList<>();
        if (args != null && args.length > 0) {
            directories.addAll(Arrays.asList(args));
        } else {
            directories.add(".");
        }
    }

    @Override
    public void putArgs(String args) {
        directories = new ArrayList<>();
        if (args != null && !args.trim().isEmpty()) {
            String[] parts = args.trim().split("\\s+");
            directories.addAll(Arrays.asList(parts));
        } else {
            directories.add(".");
        }
    }

    @Override
    public ExecutionResult execute() {
        if (directories.isEmpty()) {
            directories.add(".");
        }

        StringBuilder finalOutput = new StringBuilder();
        boolean showDirNames = directories.size() > 1;
        boolean success = true;

        for (int i = 0; i < directories.size(); i++) {
            String directory = directories.get(i);
            String targetDir = directory;
            SessionVariables sessionVars = SessionVariables.getInstance();

            if (targetDir.equals("~") || targetDir.startsWith("~/")) {
                String homeDir = System.getProperty("user.home");
                if (targetDir.equals("~")) {
                    targetDir = homeDir;
                } else {
                    targetDir = homeDir + targetDir.substring(1);
                }
            }

            if (!Paths.get(targetDir).isAbsolute()) {
                String currentDir = sessionVars.get("PWD");
                if (currentDir == null) {
                    currentDir = System.getProperty("user.dir");
                    sessionVars.set("PWD", currentDir);
                }
                targetDir = Paths.get(currentDir, targetDir).normalize().toString();
            }

            File dir = new File(targetDir);

            if (!dir.exists()) {
                finalOutput.append("ls: cannot access '").append(directory).append("': No such file or directory\n");
                success = false;
                continue;
            }

            if (!dir.isDirectory()) {
                finalOutput.append(directory).append("\n");
                continue;
            }

            File[] files = dir.listFiles();
            if (files == null) {
                finalOutput.append("ls: cannot open directory '").append(directory).append("'\n");
                success = false;
                continue;
            }

            // Show directory name when multiple directories
            if (showDirNames) {
                if (i > 0) {
                    finalOutput.append("\n");
                }
                finalOutput.append(directory).append(":\n");
            }

            // Sort files
            Arrays.sort(files);

            StringBuilder output = new StringBuilder();
            for (File file : files) {
                if (!file.getName().startsWith(".")) { // Skip hidden files
                    output.append(file.getName()).append("\n");
                }
            }

            String result = output.toString();
            if (!result.isEmpty()) {
                result = result.substring(0, result.length() - 1); // Remove last newline
            }

            finalOutput.append(result);

            // Add newline between directories if not the last one
            if (i < directories.size() - 1 && !result.isEmpty()) {
                finalOutput.append("\n");
            }
        }

        String result = finalOutput.toString();
        return new ExecutionResult(success, result);
    }
}
