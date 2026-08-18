package src.main.gamePhases;

import java.util.ArrayList;
import src.main.stateObject.*;

public class GamePhaseManager {
    ArrayList<GamePhase> gamePhases;
    int currentPhase;
    int lastPhase;

    public GamePhaseManager(ArrayList<GamePhase> phases){
        this.gamePhases = phases;
        this.currentPhase = 0;
        this.lastPhase = phases.size();
    }

    public void iterate(GameStateObject data){
        gamePhases.get(currentPhase).execute(data);
        currentPhase++; // Next time we call this we use execute the next phase
        currentPhase = currentPhase % lastPhase; // If this was the last phase in the order we reset to the first phase of the round
    }
}
