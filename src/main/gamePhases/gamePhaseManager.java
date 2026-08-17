package src.main.gamePhases;

import java.util.ArrayList;
import src.main.stateObject.*;

public class gamePhaseManager {
    ArrayList<gamePhase> gamePhases;
    int currentPhase;
    int lastPhase;

    public gamePhaseManager(ArrayList<gamePhase> phases){
        this.gamePhases = phases;
        this.currentPhase = 0;
        this.lastPhase = phases.size();
    }

    public void iterate(gameStateObject data){
        gamePhases.get(currentPhase).execute(data);
        currentPhase++; // Next time we call this we use execute the next phase
        currentPhase = currentPhase % lastPhase; // If this was the last phase in the order we reset to the first phase of the round
    }
}
