package src.main.engines;

import java.util.ArrayList;
import src.main.gamePhases.*;
import src.main.stateObject.*;
import src.main.players.*;
import src.main.online.*;
import src.main.apples.*;

public class GameEngineHost {
    GamePhaseManager phaseManager;
    public GameStateObject GSO;
    public ArrayList<Player> players;
    OnlineManager server;
    // Maximum hand size can be varied here, default is 7
    int maxHandSize = 7;
    // If no online players are present, bots are created so 4 players are in the game
    // This can be configured here
    int defaultPlayerCount = 4;
    int port;
    
    public GameEngineHost(int onlinePlayers, int port) {
        // Initialize Decks and GameStateObject
        players = new ArrayList<>();
        int playerCount = (onlinePlayers<defaultPlayerCount)? defaultPlayerCount : onlinePlayers+1;
        CardDeck greenApples = new CardDeck("greenApples.txt");
        CardDeck redApples = new CardDeck("redApples.txt");
        this.GSO = new GameStateObject(greenApples, redApples, playerCount);
        this.port = port;
        createPlayers(onlinePlayers);
        createPhases(onlinePlayers);
    }

    // Create players of online and offline varieties, padding with bots until 4 players are present
    public void createPlayers(int onlinePlayers) {
        // Add enough bot players to reach the default player count
        // We subtract 1 from defaultPlayerCount here to account for the local player
        for (int i = 0 ; i < defaultPlayerCount - 1 - onlinePlayers ; i++) {
            Player botPlayer = new PlayerBot(players.size());
            players.add(botPlayer);
        } 

        // Set up our online players
        if(onlinePlayers > 0) {
            server = new OnlineManager();
            server.setUpOnlinePlayers(onlinePlayers, players, port);
        } 

        Player thisPlayer = new PlayerLocal(players.size());
        players.add(thisPlayer); // Generate the local player
        GSO.setHostID(thisPlayer.playerID);
    } 

    // To add new phases into the game loop, slot them into this function at the appropriate spot in the phase order
    public void createPhases(int onlinePlayers) {
        ArrayList<GamePhase> phases = new ArrayList<>();
        // Create the game phases and add them to the manager
        phases.add(new GamePhaseRoundstart(players));
        phases.add(new GamePhasePlay(players));
        phases.add(new GamePhaseJudge(players));
        phases.add(new GamePhaseRoundend(players, maxHandSize));
        this.phaseManager = new GamePhaseManager(phases);
        
    }   
    public void initialDeal() {
        // Deal the amount of cards defined by maxHandSize to each player
        for (int i = 0 ; i < maxHandSize ; i++) {
            for (int j = 0 ; j < players.size() ; j++) {
                players.get(j).addToHand(GSO.redDeck.draw());
            }
        } 
    }

    // Run the game until someone wins, then annouce who won to all players
    public void mainLoop() {
        while(!this.GSO.isFinished()) {
            phaseManager.iterate(this.GSO);
        }
        int winnerID = this.GSO.getWinningApple().playerID;
        for (int i = 0 ; i < players.size() ; i++) {
            players.get(i).announceWinner(winnerID, true, "");
        } 
        if (server != null) {
            server.closeServer();
        }
    } 
}
