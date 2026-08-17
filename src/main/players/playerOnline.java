package src.main.players;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;
import src.main.apples.playedApple;

public class playerOnline extends player {
    // The playerOnline class is the server's representation of a client, it contains the same fields as a regular player along with
    // the objects required to communicate with the client.
    // Methods in this class are responsible for communicating data to the client, prompting the client player to take game actions,
    // as well as then parsing the information the client sends back to the server. 
    // In case the connection breaks, the playerOnline class defaults to bot-like behaviour, such that the game can continute unimpeded.
    public BufferedReader inFromClient;
    public DataOutputStream outToClient;

    public playerOnline(int playerID, BufferedReader in, DataOutputStream out){
        super(playerID);
        this.inFromClient = in;
        this.outToClient = out;
    }

    @Override public void addToHand(String card){ // Alert online player of their new card
        hand.add(card); // Bookkeeping for default response
        String output = "draw|"+card;
        try {
            outToClient.writeBytes(output+'\n');
        } catch (Exception e) {
            System.out.println("Communication error with player "+playerID+" in addToHand: "+e);
        }
    }

    @Override public void winCard(String card){ // Alert online player that they've won a card and track locally
        String output = "winCard|"+card;
        try {
            outToClient.writeBytes(output+'\n');
        } catch (Exception e) {
            System.out.println("Communication error with player "+playerID+" in winCard: "+e);
        }
        wonCards.add(card);
    }

    @Override public void play(List<playedApple> playedCards){ // Await online player's red apple response
        try {
            outToClient.writeBytes("play|\n");
        } catch (Exception e){
            System.out.println("Error in transmitting to player "+playerID+" in play");
        }
        try {
            String clientApple = inFromClient.readLine();
            playedApple apple = new playedApple(clientApple, playerID);
            hand.remove(hand.size()-1);
            playedCards.add(apple);
        } catch (Exception e) {
            System.out.println("Error in receiving from player "+playerID+" in play: "+e);
            int lastIDX = hand.size()-1;
            playedApple apple = new playedApple(hand.get(lastIDX), playerID);
            playedCards.add(apple);
            hand.remove(lastIDX); 
            // In the case of a player disconnecting, they will automatically play the newest card in their hand
            // This is done in order to avoid a situation where a card they've previously played is used again
        }
    }

    @Override public void judge(playedApple winningApple, ArrayList<playedApple> allApples){
        String output = "judge";
        for (int i = 0 ; i < allApples.size() ; i++){
            playedApple apple = allApples.get(i);
            output += '|' + apple.apple + '|' + apple.playerID; // Add text and player information for each apple that is to be judged to the output string
        }
        try {
            outToClient.writeBytes(output+'\n');
        } catch (Exception e) {
            System.out.println("Error transmitting to player "+playerID+" in judge: "+e);
        }

        try {
            String judgeString = inFromClient.readLine();
            String[] cardData = judgeString.split("[|]"); // Deconstruct return data from judge (formatted as "cardText|playerID")
            winningApple.setValue(cardData[0], Integer.parseInt(cardData[1])); // Update  value of the winning apple to correspond
        } catch (Exception e) {
            System.out.println("Error in receiving from player "+playerID+" in judge: "+e);
            winningApple.setValue(allApples.get(0).apple, allApples.get(0).playerID);
            // Default act as bot in judging to make game progress
        }       
    }

    @Override public void startRound(String greenApple, int ID){
        // Announce the round's green apple as well as the judge's ID
        String output = "start|"+greenApple;

        if (ID == playerID){
            output += "|You are the judge!";
        } else {
            output += "|Player "+ID+" is the judge!";
        } // Add whether or not the judge ID corresponds to the player we are transmitting the message to

         try {
            outToClient.writeBytes(output+'\n');
        } catch (Exception e) {
            System.out.println("Error transmitting to player "+playerID+" in showGreen: "+e);
        }
    }

    @Override public void showApples(ArrayList<playedApple> apples){
        String output = "apples";
        for (int i = 0 ; i < apples.size() ; i++){
            output += "|" + apples.get(i).apple;
        }
        try {
            outToClient.writeBytes(output+'\n');
        } catch (Exception e) {
            System.out.println("Error in transmitting to player "+playerID+" in showApples "+e);
        }
    }

    @Override public void announceWinner(int winnerID, boolean wonMatch, String apple){
        String outputString;
        if (winnerID == playerID) {
            outputString = "You won the ";
        } else {
            outputString = "Player "+winnerID+" won the ";
        }
        if (wonMatch) {
            outputString = "wonGame|"+outputString+"game, congratulations!";
        } else {
            outputString = "wonRound|"+outputString+"round with: "+apple;
        }
        
        try {
            outToClient.writeBytes(outputString+'\n');
        } catch (Exception e) {
            System.err.println("Error in transmitting to player "+playerID+" in announceWinner: "+e);
        }
    }
}
