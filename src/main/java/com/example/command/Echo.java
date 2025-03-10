package com.example.command;

public class Echo implements Executable {
    private String message;

    public Echo(String message) {
        this.message = message;
    }

    public boolean execute() {
        System.out.println(message);
        return true;
    }
    
}
