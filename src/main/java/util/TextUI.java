package util;

import java.util.Scanner;

public class TextUI {

    private static Scanner sc = new Scanner(System.in);

    public String getUserInput(String prompt){
        System.out.println(prompt); return sc.nextLine();
    }

    public void showMessage(String message){
        System.out.println(message);
    }

    public int promptForPhone(){
        System.out.println("Phone Number: ");
        return sc.nextInt();
        }

        public String promptForMail(){
        System.out.println("Mail: ");
        return sc.nextLine();
        }


    public String promptText(String msg) {
        System.out.println(msg);
        return sc.nextLine();
    }

    public boolean promptBinary(String msg) {
        System.out.println(msg + " (Y/N)");
        String input = sc.nextLine();

        if (input.equalsIgnoreCase("Y")) {
            return true;
        } else if (input.equalsIgnoreCase("N")) {
            return false;
        } else {
            return promptBinary(msg);
        }
    }

    public void displayMsg(String msg) {
        System.out.println(msg);
    }
}

