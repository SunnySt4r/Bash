package com.example;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import com.example.command.Command;
import com.example.utils.ExecutionResult;
import com.example.utils.ExitExeption;
import com.example.utils.WrongCommandExeption;


public class Bash {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String name = "bash";
        System.out.printf("|> %s %s%n", name, Date.from(Instant.now()));
        while (true) {
            System.out.print("|> ");
            String command = sc.nextLine();
            List<Command> commands;
            try {
                commands = Parser.parse(command);
            } catch (WrongCommandExeption e) {
                System.out.println(e.getMessage());
                continue;
            }
            ExecutionResult res;
            try {
                res = ExecutePool.execute(commands);
            } catch (ExitExeption e) {
                break;
            }
            if (!res.isSuccess()) {
                System.out.println(res.getError());
            } else {
                System.out.println(res.getOutput() + (!res.getError().isEmpty() ? "\n" + res.getError() : ""));
            }
        }
        sc.close();
    }
}
