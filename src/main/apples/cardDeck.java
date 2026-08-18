package src.main.apples;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class CardDeck { // Deck class that works for any kind of card, filename is supplied to the constructor
    private ArrayList<String> cards;
    private ArrayList<String> usedCards;

    // Read a file from the local folder, storing each line as an item in an ArrayList
    public CardDeck(String file) {
        try {
            this.cards = new ArrayList<>(Files.readAllLines(Paths.get("./resources/", file), StandardCharsets.ISO_8859_1));
        } catch(Exception e){
            System.out.println("Something went wrong: " + e);
        }
        this.usedCards = new ArrayList<>();
        shuffleDeck();
    } 

    // Randomize the index of each card in the deck
    final void shuffleDeck() {
        Random rnd = ThreadLocalRandom.current();
        for (int i = cards.size()-1 ; i>0 ; i--) {
			int index = rnd.nextInt(i+1);
			String a = cards.get(index); 
            cards.set(index, cards.get(i));
             cards.set(i, a); // SWAP
		}
    } 

    // Remove the "top" card from the deck, then return it
    public String draw() {
        // If the deck has no more cards, we reshuffle it and start dealing again.
        if (cards.isEmpty()){
            cards = usedCards;
            shuffleDeck();
            usedCards = new ArrayList<>();
        } 
        String card = this.cards.removeLast();
        return card;
    } 

    // Add card into a second pile for reshuffling later
    public void putAwayCards(ArrayList<PlayedApple> cards) {
        // we take all cards that have been played and put them into the "used" pile, so that they can be reshuffled if need be
        for (int i = 0 ; i < cards.size() ; i++) {
            usedCards.add(cards.get(i).apple);
        } 
    } 

    // Return the current size of the main deck
    public int getCardAmt() {
        return cards.size();
    } 
}
