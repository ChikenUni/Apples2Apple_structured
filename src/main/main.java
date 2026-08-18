package src.main;

import src.main.engines.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class main {

    static int defaultPort = 2048;

    // Initialize the game according to spec, either creating a client or a host depending on the arguments supplied
    public static void main(String[] args) {
        // Default play the game with all bots
        if(args.length == 0){
            System.out.println("Running the game with only bots!");
            setupHost(0);
        } else {
            // If the argument is just a number, await that many online players
            try {
                int onlinePlayers = Integer.parseInt(args[0]);
                System.out.println("Running the game with "+onlinePlayers+" online player(s)");
                setupHost(onlinePlayers);
            } catch (Exception e) {
                if (isValidIP(args[0])) { // Checking that the ip is formatted in a valid way
                    System.out.println("Running the game as a client!");
                    GameEngineClient game = new GameEngineClient(args[0], defaultPort);
                    game.runGame();
                } else {
                    System.out.println("IP address invalid, please supply a valid address and run the program again");
                }
            }
        }
    } 

    // Space saving method to setup the game as a host 
    static void setupHost(int onlinePlayers){
        GameEngineHost game = new GameEngineHost(onlinePlayers, defaultPort);
        game.initialDeal();
        game.mainLoop();
    }

    // Regex pattern checker to make sure the IP address is valid before starting a client
    static boolean isValidIP(String mightBeIp){
        Pattern ipRegex = Pattern.compile("^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}$");
        Matcher ipMatcher = ipRegex.matcher(mightBeIp);
        return ipMatcher.matches();
    }
}