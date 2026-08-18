package src.main.gamePhases;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.concurrent.*;
import java.util.Random;
import src.main.players.*;
import src.main.stateObject.*;
import src.main.apples.PlayedApple;

public class GamePhasePlay extends GamePhase{
    
    public GamePhasePlay(ArrayList<Player> players) {
        this.players = players;
    }

    // Prompt each non-judge player to select a red apple, then shuffle them and place them in the GameStateObject
    @Override public void execute(GameStateObject data) {
        List<PlayedApple> PlayedApplesMutex;
        // We use a synchronizedList here as the List of played apples will be accessed over several threads during the play phase
        PlayedApplesMutex = Collections.synchronizedList(new ArrayList<>());

        ExecutorService threadpool = Executors.newFixedThreadPool(players.size());	

        // Prompt each player on a separate thread, allowing players to make choices simultaneously
        for (int i = 0 ; i < players.size() ; i++) {
            if (players.get(i).playerID != data.getJudge()) {
                Player currentPlayer = players.get(i);
                
                Runnable task = new Runnable() {
                    @Override
                    public void run() {
                        currentPlayer.play(PlayedApplesMutex);
                    }
                };
                threadpool.execute(task);
            }
        }  
        threadpool.shutdown();
        // Wait until all threads have finished executing their play methods to progress
        while(!threadpool.isTerminated()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.err.println("Interruption occured");
            }
        }

        // Reconvert from synchronizedList to regular ArrayList once mutex requirement has passed
        ArrayList<PlayedApple> PlayedApples = new ArrayList<>(PlayedApplesMutex);
        
        // Shuffle the list of played apples, then write it into the gameStateObject to make it reachable for the judge phase
        shufflePlayedApples(PlayedApples);
        data.setAllPlayed(PlayedApples);
    }   

    // Same shuffling logic as in the cardDeck class, just now applied to a list of PlayedApples instead
    public void shufflePlayedApples(ArrayList<PlayedApple> PlayedApples) {
        Random rnd = ThreadLocalRandom.current();
        for(int i=PlayedApples.size()-1; i>0; i--) {
				int index = rnd.nextInt(i+1);
				PlayedApple a = PlayedApples.get(index); 
                PlayedApples.set(index, PlayedApples.get(i)); 
                PlayedApples.set(i, a); // SWAP
		}
    }   
}
