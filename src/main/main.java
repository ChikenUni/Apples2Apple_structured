package src.main;

import src.main.engines.*;

public class main {

    static int defaultPort = 2048;

    // Initialize the game according to spec, either creating a client or a host depending on the arguments supplied
    public static void main(String[] args) {
        if(args.length == 0){
            System.out.println("Running the game with only bots!");
            setupHost(0);
        } else {
            try {
                int onlinePlayers = Integer.parseInt(args[0]);
                System.out.println("Running the game with "+onlinePlayers+" online player(s)");
                setupHost(onlinePlayers);
            } catch (Exception e) {
                System.out.println("Running the game as a client!");
                GameEngineClient game = new GameEngineClient(args[0], defaultPort);
                game.runGame();
            }
        }
    } 

    static void setupHost(int onlinePlayers){
        GameEngineHost game = new GameEngineHost(onlinePlayers, defaultPort);
        game.createPlayers(onlinePlayers);
        game.createPhases(onlinePlayers);
        game.initialDeal();
        game.mainLoop();
    }
}