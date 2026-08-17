package src.main.gamePhases;

import java.util.ArrayList;
import src.main.players.*;
import src.main.stateObject.*;
import src.main.apples.playedApple;

public class gamePhase_judge extends gamePhase {

    public gamePhase_judge(ArrayList<player> players){
        this.players = players;
    }

    @Override public void execute(gameStateObject data){

        
        playedApple winningApple = new playedApple("", 0);
        for (int i = 0 ; i < players.size() ; i++ ){
            if (players.get(i).playerID == data.getJudge()){
                players.get(i).judge(winningApple, data.getAllPlayed());
            } else {
                players.get(i).showApples(data.getAllPlayed());
            }
        }
        data.redDeck.putAwayCards(data.getAllPlayed()); // Adding cards to the "played apples" pile

        int winningIDx = winningApple.playerID;
        String apple = winningApple.apple;
        
        data.setWinning(winningApple);
        for(int i = 0 ; i < players.size() ; i++){
            // Announce round winner, we always use false here as we always want to announce which apple a player won with!
            players.get(i).announceWinner(winningIDx, false, apple);
        }
    } // Show the played apples to each player, prompt the judge to make a choice regarding which apple they liked the most
}
