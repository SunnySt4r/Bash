package com.example;

import java.util.HashMap;
import java.util.Map;

public class SessionVariables {
    private final Map<String, String> variables = new HashMap<>();
    private static final SessionVariables INSTANCE = new SessionVariables();

    private SessionVariables() {}

    public static SessionVariables getInstance() {
        return INSTANCE;
    }

    public void set(String name, String value) {
        variables.put(name, value);
    }

    public String get(String name) {
        return variables.getOrDefault(name, System.getenv(name)); // эта штука поищет в переменных окружения у операционки, работает даже на винде 
    }
}