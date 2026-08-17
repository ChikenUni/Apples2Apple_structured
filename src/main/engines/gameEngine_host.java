package src.main.engines;

import java.util.ArrayList;
import src.main.gamePhases.*;
import src.main.stateObject.*;
import src.main.players.*;
import src.main.online.*;
import src.main.apples.*;

public class gameEngine_host {
    gamePhaseManager phaseManager;
    public gameStateObject GSO;
    public ArrayList<player> players;
    onlineManager server;
    
    public gameEngine_host(int onlinePlayers) {
        players = new ArrayList<>();
        int playerCount = (onlinePlayers<4)? 4 : onlinePlayers+1;
        cardDeck greenApples = new cardDeck("greenApples.txt");
        cardDeck redApples = new cardDeck("redApples.txt");
        this.GSO = new gameStateObject(greenApples, redApples, playerCount);
        // Initialize Decks and GameStateObject
    }

    public void createPlayers(int onlinePlayers){
        for (int i = 0 ; i < 3-onlinePlayers ; i++){
            player botPlayer = new playerBot(players.size());
            players.add(botPlayer);
        } // Add enough bot players to have 4 total players

        if(onlinePlayers > 0){
            server = new onlineManager();
            server.setUpOnlinePlayers(onlinePlayers, players, 2048);
        } // Set up our online players

        player thisPlayer = new playerLocal(players.size());
        players.add(thisPlayer); // Generate the local player
        GSO.setHostID(thisPlayer.playerID);
    } // Create players of online and offline varieties, padding with bots until 4 players are present

    public void createPhases(int onlinePlayers){
        ArrayList<gamePhase> phases = new ArrayList<>();

        phases.add(new gamePhase_roundstart(players));
        phases.add(new gamePhase_play(players));
        phases.add(new gamePhase_judge(players));
        phases.add(new gamePhase_roundend(players));
        this.phaseManager = new gamePhaseManager(phases);
        // Create the game phases and add them to the manager
    }   // To add new phases into the game loop, slot them into this function at the appropriate spot in the phase order

    public void initialDeal(){
        for ( int i = 0 ; i < 7 ; i++ ) {
            for ( int j = 0 ; j < players.size() ; j++) {
                players.get(j).addToHand(GSO.redDeck.draw());
            }
        } // Deal 7 cards to each player
    }

    public void mainLoop(){
        while(!this.GSO.isFinished()){
            phaseManager.iterate(this.GSO);
        }
        int winnerID = this.GSO.getWinningApple().playerID;
        for (int i = 0 ; i < players.size() ; i++){
            players.get(i).announceWinner(winnerID, true, "");
        } 
        if (server != null) {
            server.closeServer();
        }
    } // Run the game until someone wins, then annouce who won to all players
}
