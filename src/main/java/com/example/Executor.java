package com.example;

import com.example.command.Executable;

public class Executor {
    public boolean exec(Executable command) {
        return command.execute();
    }
}
