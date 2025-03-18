package com.example;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import com.example.command.Command;
import com.example.utils.ExecutionResult;
import com.example.utils.ExitExeption;


public class Bash {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String name = "";
        while (true) {
            System.out.printf("|> ", name, Date.from(Instant.now()));
            String command = sc.nextLine();
            List<Command> commands = Parser.parse(command);
            ExecutionResult res = null;
            try {
                res = ExecutePool.execute(commands);
            } catch (ExitExeption e) {
                break;
            }
            if (!res.isSuccess()) {
                System.out.println(res.getError());
            }else {
                System.out.println(res.getOutput() + (res.getError().length() != 0? "\n" + res.getError(): ""));
            }
        }
        sc.close();
    }
}
