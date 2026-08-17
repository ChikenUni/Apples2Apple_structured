package src.main.engines;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import src.main.players.*;
import src.main.apples.*;

public class gameEngine_client {
    ArrayList<String> hand;
    BufferedReader inFromHost;
    DataOutputStream outToHost;
    Socket socket;
    Scanner inputReader;
    playerLocal clientPlayer;
    boolean gameOver = false;

    public gameEngine_client(String hostAddress, int port){
        clientPlayer = new playerLocal(0); 
        // The player ID of the client player does not matter locally
        // Keeping track of IDs across the network would be a hassle so it is tracked by the host only
        try {
            socket = new Socket(hostAddress, port);
            inFromHost = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			outToHost = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            System.out.println("Error in connecting to host: "+e);
        }
    }

    public void runGame(){
        while(!gameOver){
            dispatchMessage();
        } 
    } // Keep reading and handling messages until the server tells the client to stop.

    public void dispatchMessage(){
        String[] splitMessage;
        try{
            String message = inFromHost.readLine();
            splitMessage = message.split("[|]");

            // Messages are always constructed according to the format: classifier|payload
            // Payloads may be further divided into more sections, also separated using the | sign
            switch (splitMessage[0]) {
            case "draw":
                // Add card transmitted in the message
                clientPlayer.addToHand(splitMessage[1]);
                break;
        
            case "winCard":
                clientPlayer.winCard(splitMessage[1]);
                break;

            case "play":
                clientPlay();
                break;

            case "judge":
                clientJudge(splitMessage);
                break;
            
            case "start":
                roundStart(splitMessage);
                break;

            case "apples":
                showApples(splitMessage);
                break;

            case "wonGame":
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                System.out.println(splitMessage[1]);
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                gameOver = true;
                break;
            
            case "wonRound":
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                System.out.println(splitMessage[1]);
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                break;

            default:
                System.out.println(message);
                break;
            }
        } catch (Exception e){
            System.out.println("Client error: "+e);
            gameOver =  true;
            // If the server loses connection we close the game
        }
    }  // Handle each incoming message according to their prefix, simpler instructions can keep their logic in the switch
       // More complex tasks are better to break into separate methods and invoked from the switch instead.

    private void clientPlay(){
        // Prompt user to play a card, then send it to the server
        ArrayList<playedApple> clientApple = new ArrayList<>();
        clientPlayer.play(clientApple);
        String output = clientApple.get(0).apple;
        try {
            outToHost.writeBytes(output+"\n");
        } catch (Exception e) {
            System.out.println("Error in sending played card to host: " +e);
        }
    }  // Scaffolding around playerLocal's play() method, and then sending the apple back to the server as a string.

    private void clientJudge(String[] message){
        // judge message is formatted: classifier|apple1|id1|apple2|id2 and so on
        playedApple winningApple = new playedApple("placeHolder", 0);
        ArrayList<playedApple> allPlayed = new ArrayList<>();

        for (int i = 1 ; i < message.length ; i+=2) {
            int ID = Integer.parseInt(message[i+1]);
            allPlayed.add(new playedApple(message[i], ID));
        }   // Reformat text string into the apple format needed for judging
            // An alternative way of handling this process would be to use a json format and a serializer method
            // I opted for this method to use as few new imports as possible
        
        clientPlayer.judge(winningApple, allPlayed);
        String output = winningApple.apple+"|"+winningApple.playerID;
            // Format response message to send back to the host
        try {
            outToHost.writeBytes(output+"\n");    
        } catch (Exception e) {
            System.out.println("Error in sending winning card to host: " +e);
        }
    }  // Scaffolding around playerLocal's judge method, and sending it back to the server

    private void roundStart(String[] message){
        System.out.println("****************************************************");
        System.out.println(message[2]);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("The green apple is: "+message[1]);
        System.out.println("****************************************************");
    }  // Announce judge and green apple

    private void showApples(String[] message){
        ArrayList<playedApple> allPlayed = new ArrayList<>();

        for (int i = 1 ; i < message.length ; i++) {
            allPlayed.add(new playedApple(message[i], 0));
        }
        clientPlayer.showApples(allPlayed);
    } // Scaffolding around playerLocal's showApple method
}
