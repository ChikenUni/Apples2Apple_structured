package src.main.players;

import java.util.ArrayList;
import java.util.List;
import src.main.apples.playedApple;

public class playerBot extends player {
    // The bot player always plays and votes for the card with index zero in both methods
    // Since it is a bot and cannot read, the methods that communicate textual information remain unimplemented
    public playerBot(int playerID){
        super(playerID);
    }

    @Override public void play(List<playedApple> apples){
        String selectedCard = removeFromHand(0);
        playedApple justPlayed = new playedApple(selectedCard, playerID);
        apples.add(justPlayed);
        // add selected card to total list of played cards through some means
    }
    @Override public void judge(playedApple winningApple, ArrayList<playedApple> apples) {
        int winningID = apples.get(0).playerID;
        String winningText = apples.get(0).apple;
        winningApple.setValue(winningText, winningID);
        // communicate to game manager through some means
    }
}
