package src.main.players;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import src.main.apples.PlayedApple;

public class PlayerLocal extends Player {
    // The local player is the one that runs on the server, as well as locally on each client
    // It is responsible for displaying game information to the player, as well as handling inputs from the player
    public PlayerLocal(int playerID) {
        super(playerID);
    }
   
    // Prompt the player to select a red apple from their hand, then add it to the mutex list
    @Override public void play(List<PlayedApple> apples) {
        System.out.println("Your cards: ");
        for (int i = 0 ; i < hand.size() ; i++) {
            System.out.println("("+i+") "+hand.get(i));
        }

        System.out.println("\n Select a card to play:");
        int choice = 0;
        boolean validchoice = false;
        // Keep prompting the player until they have made a valid card selection
        while(!validchoice) {
        try {
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				String input=br.readLine();
				choice = Integer.parseInt(input);
                // do not permit invalid choices
                if (choice < hand.size() && choice >= 0) {
                    validchoice = true;
                } 
                else { 
                    System.out.println("please select an index within bounds");
                }
			} catch (NumberFormatException e) {
                System.out.println("please select a valid option");
            } catch (Exception e) {}
        }
        // add the selected card to the mutex list of this round's played apples and remove it from our hand
        String selectedCard = removeFromHand(choice);
        PlayedApple justPlayed = new PlayedApple(selectedCard, playerID);
        System.out.println("You played: " + selectedCard);
        apples.add(justPlayed);
    }

    // Prompt the player to select a winning apple, then add it to the state object
    @Override public void judge(PlayedApple winningApple, ArrayList<PlayedApple> apples) {
        System.out.println("Select a winner:");
        showApples(apples);

        int choice = 0;
        boolean validchoice = false;
        // Same as in play, keep prompting the player until they've made a valid selection
        while(!validchoice) {
        try {
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				String input=br.readLine();
				choice = Integer.parseInt(input);
                // do not permit invalid choices
                if (choice < apples.size() && choice >= 0) {
                    validchoice = true;
                } 
                else { System.out.println("please select an index within bounds");}
			} catch (NumberFormatException e) {
                System.out.println("please select a valid option");
            } catch (Exception e) {}
        }

        // Update the value of a supplied PlayedApple to match the apple that was selected as the winner
        int winningID = apples.get(choice).playerID;
        String winningText = apples.get(choice).apple;
        winningApple.setValue(winningText, winningID);
    } 


    // Functions below just print game information to the player

    // Print roundstart information
    @Override public void startRound(String greenApple, int ID) {
        
        System.out.println("****************************************************");
        if (this.playerID == ID){
            System.out.println("You are the judge!");
        } else {
            System.out.println("Player "+ID+" is the judge!");
        }
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("The green apple is: "+greenApple);
        System.out.println("****************************************************");
    }

    // Print the apples that was played this round, so that each player can see them
    @Override public void showApples(ArrayList<PlayedApple> apples) {
        System.out.println("The selected cards are: ");
        for (int i = 0 ; i < apples.size() ; i++){
            System.out.println("("+i+") "+apples.get(i).apple);
        }
    }

    // Print who won the round/game
    @Override public void announceWinner(int winnerID, boolean wonMatch, String apple) {
        System.out.println("\n"+"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        String outputString;
        if (winnerID == playerID) {
            outputString = "You won the ";
        } else {
            outputString = "Player "+winnerID+" won the ";
        }
        if (wonMatch) {
            outputString += "game, congratulations!";
        } else {
            outputString += "round with: "+apple;
        }
        System.out.println(outputString);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"+"\n");

    }
}
