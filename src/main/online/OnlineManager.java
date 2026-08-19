package src.main.online;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import src.main.players.*;

public class OnlineManager {
    // This class mostly exists as a way to guarantee that the connection stays in scope
    // And to provide a method that lets us close the server
    ServerSocket connection;
    
    // Set up as many online players as was requested when the program was started
    public void setUpOnlinePlayers(int onlinePlayerCount, ArrayList<Player> players, int port) {  // Create the players that require connection to the internet
        int playersAdded = 0; // Tracking how many players were actually added, in case of an error we can fill the remaining spots with bots
        try {
            connection = new ServerSocket(port);
            for (int i = 0 ; i < onlinePlayerCount ; i++) {
                Socket connectionSocket = connection.accept();
			    BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			    DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                PlayerOnline nextPlayer = new PlayerOnline(players.size(), inFromClient, outToClient);
                players.add(nextPlayer);
                playersAdded++;
            }
        } catch (Exception e) {
            System.err.println("Something went wrong in setting up connections: "+e);
            System.out.println("Setting up connections went wrong, creating bots to fill expected slots");
            for (int i = playersAdded ; i < onlinePlayerCount ; i++) {
                // If we expected 4 online clients but got an error after 2 joined, we create two bots here to fill the expected amount of spots
                players.add(new PlayerBot(players.size()));
            }
        }
    } 

    public void closeServer() {
        try {
            connection.close();
        } catch (Exception e) {
            System.err.println("Something went wrong in closing the server: "+e);
        }
    }
}
