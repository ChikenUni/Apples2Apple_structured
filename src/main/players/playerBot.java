package src.main.players;

import java.util.ArrayList;
import java.util.List;
import src.main.apples.PlayedApple;

public class PlayerBot extends Player {
    // The bot player always plays and votes for the card with index zero in both methods
    // Since it is a bot and cannot read, the methods that communicate textual information remain unimplemented
    public PlayerBot(int playerID){
        super(playerID);
    }

    // play the card at index zero
    @Override public void play(List<PlayedApple> apples) {
        String selectedCard = removeFromHand(0);
        PlayedApple justPlayed = new PlayedApple(selectedCard, playerID);
        apples.add(justPlayed);
    }

    // vote for the card at index zero, list is shuffled before so this is fair
    @Override public void judge(PlayedApple winningApple, ArrayList<PlayedApple> apples) {
        int winningID = apples.get(0).playerID;
        String winningText = apples.get(0).apple;
        winningApple.setValue(winningText, winningID);
    }
}
