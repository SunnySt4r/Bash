import java.util.Scanner;

public class Bash {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String name = "";
        while (true) {
            System.out.printf("|%s> ", name);
            String command = sc.nextLine();
            if (command.equals("exit")) {
                break;
            }
            if (command.startsWith("setname ")) {
                name = command.substring(8);
                continue;
            }
        }
        sc.close();
    }
}
