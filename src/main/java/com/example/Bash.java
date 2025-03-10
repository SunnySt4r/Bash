package com.example;

import java.time.Instant;
import java.util.Date;
import java.util.Scanner;

import com.example.command.Echo;


public class Bash {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String name = "";
        while (true) {
            System.out.printf("|%s|%s> ", name, Date.from(Instant.now()));
            String command = sc.nextLine();
            if (command.equals("exit")) {
                break;
            }
            if (command.startsWith("setname ")) {
                name = command.substring(8);
                continue;
            }
            if (command.startsWith("echo ")) {
                Echo echo = new Echo(command.substring(5));
                echo.execute();
                continue;
            }
        }
        sc.close();
    }
}
