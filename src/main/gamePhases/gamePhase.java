package src.main.gamePhases;

import java.util.ArrayList;
import src.main.players.*;
import src.main.stateObject.*;

public abstract class gamePhase {
    ArrayList<player> players;

    public void execute(gameStateObject data){
        // This method is implemented in the inheritor classes
        // Execute methods are void:s, which means that any update to the gameStateObject 
        // are done within the methods themselves, through the appropriate setters in the gameStateObject
    }
}   
