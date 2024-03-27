package ui;

import java.util.Scanner;

import static ui.EscapeSequences.SET_TEXT_COLOR_MAGENTA;
import static ui.EscapeSequences.SET_TEXT_COLOR_WHITE;

public class ServerFacade {
  public static void main(String[] args) {
    System.out.printf("%sWelcome to 240 chess. Type 'help' to get started.%n", SET_TEXT_COLOR_WHITE);
    while (true) {
      System.out.printf("%n%s[LOGGED_OUT] >>> ", SET_TEXT_COLOR_WHITE);
      Scanner scanner = new Scanner(System.in);
      String line = scanner.nextLine();
      if(line.strip().equalsIgnoreCase("help")) {
        System.out.printf("  %sregister <USERNAME> <PASSWORD> <EMAIL>%s - to create an account", SET_TEXT_COLOR_MAGENTA, SET_TEXT_COLOR_WHITE);
        System.out.printf("%n  %slogin <USERNAME> <PASSWORD>%s - to play chess", SET_TEXT_COLOR_MAGENTA, SET_TEXT_COLOR_WHITE);
        System.out.printf("%n  %squit%s - quit the program", SET_TEXT_COLOR_MAGENTA, SET_TEXT_COLOR_WHITE);
        System.out.printf("%n  %shelp%s - show help menu%n", SET_TEXT_COLOR_MAGENTA, SET_TEXT_COLOR_WHITE);
      }
      else if(line.strip().equalsIgnoreCase("quit")){
        System.exit(0);
      }
      else if(line.strip().split(" ")[0].equalsIgnoreCase("register") && line.strip().split(" ").length == 4){

      }
      else {
        System.out.printf("%nNot a valid command. Type 'help' for command options.%n");
      }

    }
  }
}
