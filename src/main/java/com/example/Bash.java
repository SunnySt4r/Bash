package com.example;

import com.example.command.Command;
import com.example.utils.ExecutionResult;
import com.example.utils.ExitException;
import com.example.utils.WrongCommandException;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Bash {

    public static void main(String[] args) {
        SessionVariables.getInstance().set("PWD", System.getProperty("user.dir"));

        Scanner sc = new Scanner(System.in);
        String name = "bash";
        System.out.printf("|> %s %s%n", name, Date.from(Instant.now()));
        while (true) {
            System.out.print("|> ");
            String command = sc.nextLine();
            List<Command> commands;
            try {
                commands = Parser.parse(command);
            } catch (WrongCommandException e) {
                System.out.println(e.getMessage());
                continue;
            }
            ExecutionResult res;
            try {
                res = ExecutePool.execute(commands);
            } catch (ExitException e) {
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
