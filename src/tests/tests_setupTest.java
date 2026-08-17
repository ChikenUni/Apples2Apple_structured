package src.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import src.main.stateObject.*;
import src.main.players.*;
import src.main.apples.*;
import src.main.engines.gameEngine_host;

public class tests_setupTest {
    
    @Test // REQ 1 & 2
    void testReadCards() {
        gameEngine_host game = new gameEngine_host(0);
        cardDeck red = game.GSO.redDeck;
        cardDeck green = game.GSO.greenDeck;

        int greenCards = green.getCardAmt();
        int redCards = red.getCardAmt();
        
        assertTrue(greenCards == 614);
        assertTrue(redCards == 1826);
    }

    @Test // REQ 3
    void testShuffleDecks() {

        cardDeck red = new cardDeck("redApples.txt");
        cardDeck green = new cardDeck("greenApples.txt");
        ArrayList<String> redCards = new ArrayList<>();
        ArrayList<String> greenCards = new ArrayList<>();
        for (int i = 0 ; i < 100 ; i++){
            redCards.add(red.draw());
            greenCards.add(green.draw());
        }

        gameEngine_host game = new gameEngine_host(0);
        cardDeck shuffledRed = game.GSO.redDeck;
        cardDeck shuffledGreen = game.GSO.greenDeck;
        // The gameEngine_host constructor initializes red and green decks and shuffles them
        ArrayList<String> shuffledCardsRed = new ArrayList<>();
        ArrayList<String> shuffledCardsGreen = new ArrayList<>();
        for (int i = 0 ; i < 100 ; i++){
            shuffledCardsRed.add(shuffledRed.draw());
            shuffledCardsGreen.add(shuffledGreen.draw());
        }

        boolean foundMismatchRed = false;
        boolean foundMismatchGreen = false;
        for (int i = 0 ; i < 100 ; i++){
            String red1 = redCards.get(i);
            String red2 = shuffledCardsRed.get(i);
            if (red1 != red2){
                foundMismatchRed = true;
            } // compare content of strings to show that the first 100 cards in the two red decks do not match

            String green1 = greenCards.get(i);
            String green2 = shuffledCardsGreen.get(i);
            if (green1 != green2){
                foundMismatchGreen = true;
            } // same for the green decks
        }
        assertTrue(foundMismatchRed);
        assertTrue(foundMismatchGreen);
    }

    @Test // REQ 4
    void testInitialDeal(){
        gameEngine_host game = new gameEngine_host(0);
        game.createPlayers(0);
        game.initialDeal();

        for(int i = 0 ; i < game.players.size() ; i++){
            player thisPlayer = game.players.get(i);
            assertTrue(thisPlayer.hand.size() == 7);
        }
    }

    @Test // REQ 5
    void testRandomizeJudge(){
        cardDeck red = new cardDeck("redApples.txt");
        cardDeck green = new cardDeck("greenApples.txt");
        gameStateObject firstGSO = new gameStateObject(red, green, 7);
        int previousJudge = firstGSO.getJudge();
        boolean foundMismatch = false;
        for (int i = 0 ; i < 200 ; i++){
            gameStateObject newGSO = new gameStateObject(red, green, 7);
            foundMismatch = (newGSO.getJudge() != previousJudge);
            previousJudge = newGSO.getJudge();
        }
        assertTrue(foundMismatch);
    }
    // Deterministically testing randomization is difficult as there is a cosmically small chance of the test failing despite the underlying code working as intended,
    // In this case each playing being selected as the judge is a 1/7 chance
    // We select 201 different judges, giving us a (1/7)^201 chance of picking the same judge each time
}
