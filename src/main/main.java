package src.main;

import src.main.engines.*;

public class main {
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
                gameEngine_client game = new gameEngine_client(args[0],2048);
                game.runGame();
            }
        }
    } // Initialize the game according to spec, either creating a client or a host depending on the arguments supplied

    static void setupHost(int onlinePlayers){
        gameEngine_host game = new gameEngine_host(onlinePlayers);
        game.createPlayers(onlinePlayers);
        game.createPhases(onlinePlayers);
        game.initialDeal();
        game.mainLoop();
    }
}