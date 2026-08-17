package src.main.apples;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class cardDeck { // Deck class that works for any kind of card, filename is supplied to the constructor
    private ArrayList<String> cards;
    private ArrayList<String> usedCards;

    public cardDeck(String file){
        try {
            //System.out.println(new File(".").getAbsolutePath());
            this.cards = new ArrayList<>(Files.readAllLines(Paths.get("./src/main/apples/", file), StandardCharsets.ISO_8859_1));
        } catch(Exception e){
            System.out.println("Something went wrong: " + e);
        }
        this.usedCards = new ArrayList<>();
        shuffleDeck();
    } // Read a file from the local folder, storing each line as an item in an ArrayList

    final void shuffleDeck() {
        Random rnd = ThreadLocalRandom.current();
        for(int i=cards.size()-1; i>0; i--) {
			int index = rnd.nextInt(i+1);
			String a = cards.get(index); cards.set(index, cards.get(i)); cards.set(i, a); // SWAP
		}
    } // Randomize the index of each card in the deck

    public String draw() {
        if (cards.isEmpty()){
            cards = usedCards;
            shuffleDeck();
        } // If the deck has no more cards, we reshuffle it and start dealing again.
        String card = this.cards.removeLast();
        return card;
    } // Remove the "top" card from the deck, then return it

    public void putAwayCards(ArrayList<playedApple> cards){
        for (int i = 0 ; i < cards.size() ; i++ ){
            usedCards.add(cards.get(i).apple);
        } // we take all cards that have been played and put them into the "used" pile, so that they can be reshuffled if need be
    } // Add card into a second pile for reshuffling later

    public int getCardAmt() {
        return cards.size();
    } // Return the current size of the main deck
}
