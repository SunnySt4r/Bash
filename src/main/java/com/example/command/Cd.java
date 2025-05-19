package com.example.command;

import com.example.SessionVariables;
import com.example.utils.ExecutionResult;

import java.io.File;
import java.nio.file.Paths;

public class Cd extends Command {
    private String directory = "~";

    public Cd() {
    }

    public Cd(String[] args) {
        if (args != null && args.length > 0) {
            if (args.length > 1) {
                directory = null;
            } else {
                directory = args[0];
            }
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
        if (directory == null) {
            return new ExecutionResult(false, "cd: too many arguments");
        }
        SessionVariables sessionVars = SessionVariables.getInstance();
        String targetDir = directory;

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
            return new ExecutionResult(false, "cd: " + directory + ": No such file or directory");
        }

        if (!dir.isDirectory()) {
            return new ExecutionResult(false, "cd: " + directory + ": Not a directory");
        }

        String oldPwd = sessionVars.get("PWD");
        sessionVars.set("OLDPWD", oldPwd != null ? oldPwd : System.getProperty("user.dir"));
        sessionVars.set("PWD", dir.getAbsolutePath());

        return new ExecutionResult(true, "");
    }
}
