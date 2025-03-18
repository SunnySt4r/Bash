package com.example.utils;

public class ExecutionResult {
    private String output;
    private String error;
    private boolean success;
    
    public ExecutionResult(boolean success, String text) {
        this.success = success;
        if (success) {
            this.output = text;
            this.error = "";
        } else {
            this.output = "";
            this.error = text;
        }

    }

    public String getOutput() {
        return output;
    }

    public boolean isSuccess() {
        return success;
    }

    public void addError(String error) {
        this.error += "\n" + error;
    }

    public void addOutput(String output) {
        this.output += "\n" + output;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    @Override
    public String toString() {
        return "ExecutionResult [output=" + output + ", error=" + error + ", success=" + success + "]";
    }

}
