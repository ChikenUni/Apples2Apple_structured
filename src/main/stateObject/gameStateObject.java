package src.main.stateObject;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import src.main.apples.*;

public class gameStateObject {
    private String newGreen;
    private playedApple winningApple;
    private ArrayList<playedApple> allPlayedApples;
    private int judgeID;
    private int hostID;
    private boolean gameFinished;
    public cardDeck greenDeck;
    public cardDeck redDeck;

    public gameStateObject(cardDeck greenDeck, cardDeck redDeck, int playerCount){
        Random rnd = ThreadLocalRandom.current();
        this.judgeID = rnd.nextInt(playerCount); // randomize starting judge
        this.gameFinished = false;
        this.greenDeck = greenDeck;
        this.redDeck = redDeck;
    } // Shared state class where other classes can transmit data between each other without needing direct references
    // Any alteration needs to be done through the appropriate getters and setters
    // (With the exception of the cardDecks, as those already have methods that help isolate their data)

    // ------ SETTERS ------ //
    public void setGreen(String newGreen){
        this.newGreen = newGreen;
    }

    public void setWinning(playedApple winning){
        this.winningApple = winning;
    }

    public void setAllPlayed(ArrayList<playedApple> allApples){
        this.allPlayedApples = allApples;
    }

    public void setHostID(int ID){
        this.hostID = ID;
    }

    public void setJudgeID(int newJudge){
        this.judgeID = newJudge;
    }

    public void finishGame(){
        this.gameFinished = true;
    }

    // ------ GETTERs ------ //

    public String getGreen(){
        return this.newGreen;
    }

    public ArrayList<playedApple> getAllPlayed(){
        return this.allPlayedApples;
    }

    public playedApple getWinningApple(){
        return this.winningApple;
    }

    public int getJudge(){
        return this.judgeID;
    }

    public int getHost(){
        return this.hostID;
    }

    public boolean isFinished(){
        return this.gameFinished;
    }
}
