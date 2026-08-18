package src.main.gamePhases;

import java.util.ArrayList;
import src.main.stateObject.*;
import src.main.players.*;

public class GamePhaseRoundstart extends GamePhase {
    
    int judgeID;

    public GamePhaseRoundstart(ArrayList<Player> players) {
        this.players = players;
    }

    // Increment judgeID and draw a new green apple, updating the gameStateObject with the new information
    @Override public void execute(GameStateObject data) {
        String newGreen = data.greenDeck.draw();
        judgeID = data.getJudge() + 1; // 
        if (judgeID >= players.size()) {
            judgeID = 0;
        }
        data.setGreen(newGreen);
        data.setJudgeID(judgeID);
        for (int i = 0 ; i < players.size() ; i++) {
            players.get(i).startRound(newGreen, judgeID);
        }
    }
}
