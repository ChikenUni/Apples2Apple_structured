package src.main.apples;

public class playedApple {
    // The playedApple class serves to associate an apple with the ID that played it
    // In the future this class could be expanded to contain a cardType identifier 
    // if new types such as pears or wild apples are implemented, as those would require different logic in other parts of the code
    public String apple;
    public int playerID;

    public playedApple(String apple, int ID){
        this.apple = apple;
        this.playerID = ID;
    } 

    public void setValue(String apple, int ID){
        this.apple = apple;
        this.playerID = ID;
    }
}
