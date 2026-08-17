package src.main.gamePhases;

import java.util.ArrayList;
import src.main.stateObject.*;
import src.main.players.*;

public class gamePhase_roundend extends gamePhase {
    int winCon;
    
    public gamePhase_roundend(ArrayList<player> players){
        int playerCount = players.size();
        this.players = players;
        this.winCon = (playerCount > 8) ? 4 : 12-playerCount; // Calculate wincon according to rules spec
    }

    @Override public void execute(gameStateObject data){
        for (int i = 0 ; i < players.size() ; i++) {
            while(players.get(i).hand.size() < 7){
                String newCard = data.redDeck.draw();
                players.get(i).addToHand(newCard);
            }
        } // Refill each player's hand to seven cards, on rounds where a player is a judge they will not play a card,
          // and thus not draw cards either

        int winningIDx = data.getWinningApple().playerID;
        players.get(winningIDx).winCard(data.getGreen());
        if (players.get(winningIDx).wonCards.size() >= winCon){
            data.finishGame();
        } // Check if the player who just won an apple now has the required amount to win the game, update state if that is the case
    }
}
