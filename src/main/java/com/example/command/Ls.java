package com.example.command;

import com.example.SessionVariables;
import com.example.utils.ExecutionResult;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;

public class Ls extends Command {
    private String directory = ".";

    public Ls() {
    }

    public Ls(String[] args) {
        if (args != null && args.length > 0) {
            directory = args[0];
        }
    }

    @Override
    public void putArgs(String args) {
        if (args != null && !args.trim().isEmpty()) {
            directory = args.trim();
        }
    }

    @Override
    public ExecutionResult execute() {
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
            return new ExecutionResult(false, "ls: cannot access '" + directory + "': No such file or directory");
        }

        if (!dir.isDirectory()) {
            return new ExecutionResult(true, directory);
        }

        File[] files = dir.listFiles();
        if (files == null) {
            return new ExecutionResult(false, "ls: cannot open directory '" + directory + "'");
        }

        // Сортировка файлов
        Arrays.sort(files);

        StringBuilder output = new StringBuilder();
        for (File file : files) {
            if (!file.getName().startsWith(".")) { // Пропускаем скрытые файлы
                output.append(file.getName()).append("\n");
            }
        }

        String result = output.toString();
        if (!result.isEmpty()) {
            result = result.substring(0, result.length() - 1); // Удаляем последний перенос строки
        }

        return new ExecutionResult(true, result);
    }
}
