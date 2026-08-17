package src.main.players;

import java.util.ArrayList;
import java.util.List;
import src.main.apples.playedApple;


public abstract class player {
    // The abstract player class contains a set of shared methods used by both playerLocal and playerBot, 
    // as well as methods that all player implementations need to invoke, but do so differently
    public ArrayList<String> hand;
    public ArrayList<String> wonCards;
    public int playerID;

    public player(int playerID){
        this.playerID = playerID;
        this.hand = new ArrayList<>();
        this.wonCards = new ArrayList<>();
    }

    public void addToHand(String card){
        hand.add(card);
    }
    public void winCard(String card){
        wonCards.add(card);
    }
    public String removeFromHand(int index){
        return hand.remove(index);
    }
    public void play(List<playedApple> apples){
        // This is implemented in the child classes for Bot, Host, and Client.
    }
    public void judge(playedApple winningApple, ArrayList<playedApple> apples){
        // This is implemented in the child classes for Bot, Host, and Client.
    }
    public void startRound(String greenApple, int ID){
        // This is implemented in the child classes for Host and Client.
    }
    public void showApples(ArrayList<playedApple> apples){
        // This is implemented in the child classes for Host and Client.
    }
    public void announceWinner(int winnerID, boolean wonMatch, String apple){
        // This is implemented in the child classes for Host and Client, Bots do not care...
    }
}
