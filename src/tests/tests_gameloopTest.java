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
        ArrayList<Player> players = new ArrayList<>();
        players.add(new PlayerBot(0));
        players.add(new PlayerBot(1));

        CardDeck greenDeck = new CardDeck("greenApples.txt");
        CardDeck redDeck = new CardDeck("redApples.txt");
        GameStateObject GSO = new GameStateObject(greenDeck, redDeck, 2);

        
        // Green apple is unloaded before card is drawn at round start
        // In future rounds this will represent the previous green apple until roundstart overwrites it
        GamePhase start = new GamePhaseRoundstart(players);
        assertTrue(GSO.getGreen() == null);

        // This is where round start logic is executed, drawing a new green apple
        // Roundstart's execute also runs player.startRound on each player, which prints the apple for the host
        // as well as transmitting the data to online players, but we generally don't test prints(?)
        start.execute(GSO);

        // Ensuring that the apple has been updated
        assertTrue(GSO.getGreen().length() > 0);
    }

    @Test // REQ 7 & 9
    void testPlayApples(){
        ArrayList<Player> players = new ArrayList<>();
        players.add(new PlayerBot(0));
        players.add(new PlayerBot(1));
        players.add(new PlayerBot(2));
        players.add(new PlayerBot(3));

        CardDeck greenDeck = new CardDeck("greenApples.txt");
        CardDeck redDeck = new CardDeck("redApples.txt");
        GameStateObject GSO = new GameStateObject(greenDeck, redDeck, 4);
        genericDeal(players, GSO);
        assertTrue(GSO.getAllPlayed() == null);

        GamePhase play = new GamePhasePlay(players);
        play.execute(GSO);

        // With 4 players in the game, three cards should have been played
        assertTrue(GSO.getAllPlayed().size() == 3);
    }

    @Test // REQ 8
    void testShuffledPlayed(){
        ArrayList<Player> players = new ArrayList<>();
        players.add(new PlayerBot(0));
        players.add(new PlayerBot(1));
        players.add(new PlayerBot(2));
        players.add(new PlayerBot(3));

        CardDeck greenDeck = new CardDeck("greenApples.txt");
        CardDeck redDeck = new CardDeck("redApples.txt");
        GameStateObject GSO = new GameStateObject(greenDeck, redDeck, 4);
        genericDeal(players, GSO);

        assertTrue(GSO.getAllPlayed() == null);

        GamePhasePlay play = new GamePhasePlay(players);
        play.execute(GSO);
        // Setting up a set of played cards

        int firstID = GSO.getAllPlayed().get(0).playerID;
        boolean foundMismatch = false;

        // Attempt to shuffle the deck 100 times, most likely the same apple won't be in the first slot 100 times
        // ( (1/3)^100 chance of this happening )
        for (int i = 0 ; i < 100 ; i++) {
            play.shufflePlayedApples(GSO.getAllPlayed());
            if (firstID != GSO.getAllPlayed().get(0).playerID) {
                foundMismatch = true;
            }
        }
        assertTrue(foundMismatch);
    }

    @Test // REQ 10
    void testJudge(){
        ArrayList<Player> players = new ArrayList<>();
        players.add(new PlayerBot(0));
        players.add(new PlayerBot(1));
        players.add(new PlayerBot(2));
        players.add(new PlayerBot(3));

        CardDeck greenDeck = new CardDeck("greenApples.txt");
        CardDeck redDeck = new CardDeck("redApples.txt");
        GameStateObject GSO = new GameStateObject(greenDeck, redDeck, 4);
        genericDeal(players, GSO);

        GamePhase play = new GamePhasePlay(players);
        play.execute(GSO);

        // ensure that we have no winning apple before a new one is set
        // in future rounds it will remain as the previous winner until a new one is selected
        assertTrue(GSO.getWinningApple() == null);
        
        // The ID of the player whose apple is at index 0 of the list of all played apples, the bot will always let this player win
        int IdAtIndex0 = GSO.getAllPlayed().get(0).playerID;
     
        GamePhase judge = new GamePhaseJudge(players);
        judge.execute(GSO);

        // Checking that the ID on the winning apple is as intended
        assertTrue(IdAtIndex0 == GSO.getWinningApple().playerID);
    }

    @Test // REQ 11 & 12
    void testDiscardAndRefill(){
        ArrayList<Player> players = new ArrayList<>();
        players.add(new PlayerBot(0));
        players.add(new PlayerBot(1));
        players.add(new PlayerBot(2));
        players.add(new PlayerBot(3));

        CardDeck greenDeck = new CardDeck("greenApples.txt");
        CardDeck redDeck = new CardDeck("redApples.txt");
        GameStateObject GSO = new GameStateObject(greenDeck, redDeck, 4);
        genericDeal(players, GSO);

        GamePhase play = new GamePhasePlay(players);
        play.execute(GSO);

        // All non-judge players should only have 6 cards in hand
        for (int i = 0 ; i < players.size() ; i++) {
            if (i != GSO.getJudge()) {
                assertTrue(players.get(i).hand.size() == 6);
            } 
        }

        GamePhase judge = new GamePhaseJudge(players);
        judge.execute(GSO); // We run this to make sure end phase can be executed correctly

        GamePhase end = new GamePhaseRoundend(players, 7);
        end.execute(GSO);
        // After each round, players fill their hands back to seven cards
        for (int i = 0 ; i < players.size() ; i++) {
            assertTrue(players.get(i).hand.size() == 7 );
        }
    }

    @Test // REQ 13
    void testNewJudgeIsPicked(){
        ArrayList<Player> players = new ArrayList<>();
        players.add(new PlayerBot(0));
        players.add(new PlayerBot(1));
        players.add(new PlayerBot(2));
        players.add(new PlayerBot(3));

        CardDeck greenDeck = new CardDeck("greenApples.txt");
        CardDeck redDeck = new CardDeck("redApples.txt");
        GameStateObject GSO = new GameStateObject(greenDeck, redDeck, 4);
        GamePhase start = new GamePhaseRoundstart(players);
        
        // judge ID increments by 1 at each round start
        // if it exceeds the highest ID (total player amount minus 1) we want it to loop back to 0
        // trying it 100 times is just overkill for fun :)
        for (int i = 0 ; i < 100 ; i++) {
            int previousJudge = GSO.getJudge();        
            int expectedJudge = (previousJudge == 3)? 0 : previousJudge+1;
            start.execute(GSO);
            assertTrue(GSO.getJudge() == expectedJudge);
        } 
    }

    @Test // REQ 14 
    void testGiveApple(){
        ArrayList<Player> players = new ArrayList<>();
        players.add(new PlayerBot(0));
        players.add(new PlayerBot(1));
        players.add(new PlayerBot(2));
        players.add(new PlayerBot(3));

        CardDeck greenDeck = new CardDeck("greenApples.txt");
        CardDeck redDeck = new CardDeck("redApples.txt");
        GameStateObject GSO = new GameStateObject(greenDeck, redDeck, 4);
        genericDeal(players, GSO);
        GamePhase start = new GamePhaseRoundstart(players);
        GamePhase play = new GamePhasePlay(players);
        GamePhase judge = new GamePhaseJudge(players);
        GamePhase end = new GamePhaseRoundend(players, 7);

        for (int i = 1 ; i <= 10 ; i++) {
            int expectedTotalWins = i;
            int actualWins = 0;

            start.execute(GSO);
            play.execute(GSO);
            judge.execute(GSO);
            end.execute(GSO);

            for (int j = 0 ; j < players.size() ; j++) {
                actualWins += players.get(j).wonCards.size();
            }
            // One card will be won in each round, thus the total amount of won cards will always equal the amount of rounds played
            assertTrue(expectedTotalWins == actualWins);
        } 
    }

    // Simulate games for 4,5,6,7,8, and 9 players, checking that the game is declared finished when the appropriate amount of wins have been achieved by a player
    @Test// REQ 15
    void testWinCons(){
        // Create an increasing amount of players each round;
        for (int i = 4 ; i < 10 ; i++) {
            ArrayList<Player> players = new ArrayList<>();
            for (int j = 0 ; j < i ; j++) {
                players.add(new PlayerBot(j));
            } 

            CardDeck greenDeck = new CardDeck("greenApples.txt");
            CardDeck redDeck = new CardDeck("redApples.txt");
            GameStateObject GSO = new GameStateObject(greenDeck, redDeck, i);
            genericDeal(players, GSO);
            GamePhase start = new GamePhaseRoundstart(players);
            GamePhase play = new GamePhasePlay(players);
            GamePhase judge = new GamePhaseJudge(players);
            GamePhaseRoundend end = new GamePhaseRoundend(players, 7);

            // Simulate playing the game until a winner is decided
            while(!GSO.isFinished()) {
                start.execute(GSO);
                play.execute(GSO);
                judge.execute(GSO);
                end.execute(GSO);
            } 

            Player winner = players.get(GSO.getWinningApple().playerID);
            int expectedWins = (i > 8)? 4 : 12-i; // The amount of apples the winning player should have by the end of a game
            assertTrue(winner.wonCards.size() == expectedWins);
        }
    } 

    // Dealing cards manually since we're testing outside of gameEngine_host (where we could use initialDeal instead)
    void genericDeal(ArrayList<Player> players, GameStateObject GSO){
        for (int i = 0 ; i < 4 ; i++) {
            Player dealTo = players.get(i);
            for (int j = 0 ; j < 7 ; j++) {
                dealTo.addToHand(GSO.redDeck.draw());
            } 
        }
    }
}
