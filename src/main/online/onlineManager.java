package src.main.online;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import src.main.players.*;

public class onlineManager {
    // This class mostly exists as a way to guarantee that the connection stays in scope
    // And to provide a method that lets us close the server
    ServerSocket connection;
    
    public void setUpOnlinePlayers(int amt, ArrayList<player> players, int socketValue){  // Create the players that require connection to the internet
        try {
        connection = new ServerSocket(2048);
        for (int i = 0 ; i < amt ; i++ ){
            Socket connectionSocket = connection.accept();
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            playerOnline nextPlayer = new playerOnline(players.size(), inFromClient, outToClient);
            players.add(nextPlayer);
        }
        } catch (Exception e) {
            System.out.println("Something went wrong in setting up connections: "+e);
        }
    } // Set up as many online players as was requested when the program was started

    public void closeServer(){
        try {
            connection.close();
        } catch (Exception e) {
            System.out.println("Something went wrong in closing the server: "+e);
        }
    }
}
