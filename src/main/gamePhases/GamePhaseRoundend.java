package src.main.gamePhases;

import java.util.ArrayList;
import src.main.stateObject.*;
import src.main.players.*;

public class GamePhaseRoundend extends GamePhase {
    int winCon;
    int maxHandSize;

    // These can be changed to alter the winCon calculation
    int minWins = 4;
    int maxWinsBeforePlayers = 12;
    
    public GamePhaseRoundend(ArrayList<Player> players, int maxHandSize) {
        int playerCount = players.size();
        this.players = players;
        this.maxHandSize = maxHandSize;
        this.winCon = (playerCount > 8) ? minWins : maxWinsBeforePlayers - playerCount; // Calculate wincon according to rules spec
    }

    @Override public void execute(GameStateObject data) {
        // Refill each player's hand to the set amount of cards, on rounds where a player is a judge they will not play a card,
        // and thus not draw cards either
        for (int i = 0 ; i < players.size() ; i++) {
            while(players.get(i).hand.size() < maxHandSize) {
                String newCard = data.redDeck.draw();
                players.get(i).addToHand(newCard);
            }
        } 

        // Check if the player who just won an apple now has the required amount to win the game, update state if that is the case
        int winningID = data.getWinningApple().playerID;
        players.get(winningID).winCard(data.getGreen());
        if (players.get(winningID).wonCards.size() >= winCon) {
            data.finishGame();
        } 
    }
}
