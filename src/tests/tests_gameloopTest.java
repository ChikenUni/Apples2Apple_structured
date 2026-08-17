package src.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import src.main.gamePhases.*;
import src.main.stateObject.*;
import src.main.players.*;
import src.main.apples.*;

public class tests_gameloopTest {
    
    @Test // REQ 6
    void testDrawGreen() {
        ArrayList<player> players = new ArrayList<>();
        players.add(new playerBot(0));
        players.add(new playerBot(1));

        cardDeck greenDeck = new cardDeck("greenApples.txt");
        cardDeck redDeck = new cardDeck("redApples.txt");
        gameStateObject GSO = new gameStateObject(greenDeck, redDeck, 2);

        gamePhase start = new gamePhase_roundstart(players);
        assertTrue(GSO.getGreen() == null);
        // Green apple is unloaded before card is drawn at round start
        // In future rounds this will represent the previous green apple until roundstart overwrites it

        start.execute(GSO);
        // This is where round start logic is executed, drawing a new green apple
        // Roundstart's execute also runs player.startRound on each player, which prints the apple for the host
        // as well as transmitting the data to online players, but we generally don't test prints(?)

        assertTrue(GSO.getGreen().length() > 0);
        // Ensuring that the apple has been updated
    }

    @Test // REQ 7 & 9
    void testPlayApples(){
        ArrayList<player> players = new ArrayList<>();
        players.add(new playerBot(0));
        players.add(new playerBot(1));
        players.add(new playerBot(2));
        players.add(new playerBot(3));

        cardDeck greenDeck = new cardDeck("greenApples.txt");
        cardDeck redDeck = new cardDeck("redApples.txt");
        gameStateObject GSO = new gameStateObject(greenDeck, redDeck, 4);
        genericDeal(players, GSO);
        assertTrue(GSO.getAllPlayed() == null);

        gamePhase play = new gamePhase_play(players);
        play.execute(GSO);

        assertTrue(GSO.getAllPlayed().size() == 3);
        // With 4 players in the game, three cards should have been played
    }

    @Test // REQ 8
    void testShuffledPlayed(){
        ArrayList<player> players = new ArrayList<>();
        players.add(new playerBot(0));
        players.add(new playerBot(1));
        players.add(new playerBot(2));
        players.add(new playerBot(3));

        cardDeck greenDeck = new cardDeck("greenApples.txt");
        cardDeck redDeck = new cardDeck("redApples.txt");
        gameStateObject GSO = new gameStateObject(greenDeck, redDeck, 4);
        genericDeal(players, GSO);

        assertTrue(GSO.getAllPlayed() == null);

        gamePhase_play play = new gamePhase_play(players);
        play.execute(GSO);
        // Setting up a set of played cards

        int firstID = GSO.getAllPlayed().get(0).playerID;
        boolean foundMismatch = false;

        for (int i = 0 ; i < 100 ; i++){
            play.shufflePlayedApples(GSO.getAllPlayed());
            if (firstID != GSO.getAllPlayed().get(0).playerID){
                foundMismatch = true;
            }
        } // Attempt to shuffle the deck 100 times, most likely the same apple won't be in the first slot 100 times
          // ( (1/3)^100 chance of this happening )
        assertTrue(foundMismatch);
    }

    @Test // REQ 10
    void testJudge(){
        ArrayList<player> players = new ArrayList<>();
        players.add(new playerBot(0));
        players.add(new playerBot(1));
        players.add(new playerBot(2));
        players.add(new playerBot(3));

        cardDeck greenDeck = new cardDeck("greenApples.txt");
        cardDeck redDeck = new cardDeck("redApples.txt");
        gameStateObject GSO = new gameStateObject(greenDeck, redDeck, 4);
        genericDeal(players, GSO);

        gamePhase play = new gamePhase_play(players);
        play.execute(GSO);

        assertTrue(GSO.getWinningApple() == null);
        // ensure that we have no winning apple before a new one is set
        // in future rounds it will remain as the previous winner until a new one is selected

        int IdAtIndex0 = GSO.getAllPlayed().get(0).playerID;
        // The ID of the player whose apple is at index 0 of the list of all played apples, the bot will always let this player win


        gamePhase judge = new gamePhase_judge(players);
        judge.execute(GSO);

        assertTrue(IdAtIndex0 == GSO.getWinningApple().playerID);
        // Checking that the ID on the winning apple is as intended
    }

    @Test // REQ 11 & 12
    void testDiscardAndRefill(){
        ArrayList<player> players = new ArrayList<>();
        players.add(new playerBot(0));
        players.add(new playerBot(1));
        players.add(new playerBot(2));
        players.add(new playerBot(3));

        cardDeck greenDeck = new cardDeck("greenApples.txt");
        cardDeck redDeck = new cardDeck("redApples.txt");
        gameStateObject GSO = new gameStateObject(greenDeck, redDeck, 4);
        genericDeal(players, GSO);

        gamePhase play = new gamePhase_play(players);
        play.execute(GSO);

        for (int i = 0 ; i < players.size() ; i++){
            if (i != GSO.getJudge()){
                assertTrue(players.get(i).hand.size() == 6);
            } // All non-judge players should only have 6 cards in hand
        }

        gamePhase judge = new gamePhase_judge(players);
        judge.execute(GSO); // We run this to make sure end phase can be executed correctly

        gamePhase end = new gamePhase_roundend(players);
        end.execute(GSO);
        for (int i = 0 ; i < players.size() ; i++){
            assertTrue(players.get(i).hand.size() == 7 );
            // After each round, players fill their hands back to seven cards
        }
    }

    @Test // REQ 13
    void testNewJudgeIsPicked(){
        ArrayList<player> players = new ArrayList<>();
        players.add(new playerBot(0));
        players.add(new playerBot(1));
        players.add(new playerBot(2));
        players.add(new playerBot(3));

        cardDeck greenDeck = new cardDeck("greenApples.txt");
        cardDeck redDeck = new cardDeck("redApples.txt");
        gameStateObject GSO = new gameStateObject(greenDeck, redDeck, 4);
        gamePhase start = new gamePhase_roundstart(players);
        
        for (int i = 0 ; i < 100 ; i++){
            int previousJudge = GSO.getJudge();        
            int expectedJudge = (previousJudge == 3)? 0 : previousJudge+1;
            start.execute(GSO);
            assertTrue(GSO.getJudge() == expectedJudge);
        } // judge ID increments by 1 at each round start
          // if it exceeds the highest ID (total player amount minus 1) we want it to loop back to 0
          // trying it 100 times is just overkill for fun :)
    }

    @Test // REQ 14 
    void testGiveApple(){
        ArrayList<player> players = new ArrayList<>();
        players.add(new playerBot(0));
        players.add(new playerBot(1));
        players.add(new playerBot(2));
        players.add(new playerBot(3));

        cardDeck greenDeck = new cardDeck("greenApples.txt");
        cardDeck redDeck = new cardDeck("redApples.txt");
        gameStateObject GSO = new gameStateObject(greenDeck, redDeck, 4);
        genericDeal(players, GSO);
        gamePhase start = new gamePhase_roundstart(players);
        gamePhase play = new gamePhase_play(players);
        gamePhase judge = new gamePhase_judge(players);
        gamePhase end = new gamePhase_roundend(players);

        for (int i = 1 ; i <= 10 ; i++){
            int expectedTotalWins = i;
            int actualWins = 0;

            start.execute(GSO);
            play.execute(GSO);
            judge.execute(GSO);
            end.execute(GSO);

            for(int j = 0 ; j < players.size() ; j++){
                actualWins += players.get(j).wonCards.size();
            }
            assertTrue(expectedTotalWins == actualWins);
        } // One card will be won in each round, thus the total amount of won cards will always equal the amount of rounds played
    }

    @Test// REQ 15
    void testWinCons(){
        for (int i = 4 ; i < 10 ; i++){
            ArrayList<player> players = new ArrayList<>();
            for (int j = 0 ; j < i ; j++){
                players.add(new playerBot(j));
            } // Create an increasing amount of players each round;

            cardDeck greenDeck = new cardDeck("greenApples.txt");
            cardDeck redDeck = new cardDeck("redApples.txt");
            gameStateObject GSO = new gameStateObject(greenDeck, redDeck, i);
            genericDeal(players, GSO);
            gamePhase start = new gamePhase_roundstart(players);
            gamePhase play = new gamePhase_play(players);
            gamePhase judge = new gamePhase_judge(players);
            gamePhase_roundend end = new gamePhase_roundend(players);

            while(!GSO.isFinished()){
                start.execute(GSO);
                play.execute(GSO);
                judge.execute(GSO);
                end.execute(GSO);
            } // Simulate playing the game until a winner is decided

            player winner = players.get(GSO.getWinningApple().playerID);
            int expectedWins = (i > 8)? 4 : 12-i; // The amount of apples the winning player should have by the end of a game
            assertTrue(winner.wonCards.size() == expectedWins);
        }
    } // Simulate games for 4,5,6,7,8, and 9 players, checking that the game is declared finished when the appropriate amount of wins have been achieved by a player

    void genericDeal(ArrayList<player> players, gameStateObject GSO){
        for (int i = 0 ; i < 4 ; i++){
            player dealTo = players.get(i);
            for (int j = 0 ; j < 7 ; j++){
                dealTo.addToHand(GSO.redDeck.draw());
            } // Dealing cards manually since we're testing outside of gameEngine_host (where we could use initialDeal instead)
        }
    }
}
