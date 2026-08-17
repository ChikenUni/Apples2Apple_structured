package src.main.gamePhases;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.concurrent.*;
import java.util.Random;
import src.main.players.*;
import src.main.stateObject.*;
import src.main.apples.playedApple;

public class gamePhase_play extends gamePhase{
    
    public gamePhase_play(ArrayList<player> players){
        this.players = players;
    }

    @Override public void execute(gameStateObject data){
        List<playedApple> playedApplesMutex;
        playedApplesMutex = Collections.synchronizedList(new ArrayList<>());
        // We use a synchronizedList here as the List of played apples will be accessed over several threads during the play phase

        ExecutorService threadpool = Executors.newFixedThreadPool(players.size());	

        for (int i = 0 ; i < players.size() ; i++ ){
            if (players.get(i).playerID != data.getJudge()){
                player currentPlayer = players.get(i);
                
                Runnable task = new Runnable() {
                    @Override
                    public void run() {
                        currentPlayer.play(playedApplesMutex);
                    }
                };
                threadpool.execute(task);
            }
        }  // Prompt each player on a separate thread, allowing players to make choices simultaneously
        threadpool.shutdown();
        while(!threadpool.isTerminated()){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.out.println("Interruption occured");
            }
        }

        ArrayList<playedApple> playedApples = new ArrayList<>(playedApplesMutex);
        // Reconvert from synchronizedList to regular ArrayList once mutex requirement has passed
        shufflePlayedApples(playedApples);
        // Shuffle the apples
        data.setAllPlayed(playedApples);
    }   // Shuffle the list of played apples, then write it into the gameStateObject to make it reachable for the judge phase

    public void shufflePlayedApples(ArrayList<playedApple> playedApples){
        Random rnd = ThreadLocalRandom.current();
        for(int i=playedApples.size()-1; i>0; i--) {
				int index = rnd.nextInt(i+1);
				playedApple a = playedApples.get(index); playedApples.set(index, playedApples.get(i)); playedApples.set(i, a); // SWAP
		}
    }   // Same shuffling logic as in the cardDeck class, just now applied to a list of playedApples instead
}
