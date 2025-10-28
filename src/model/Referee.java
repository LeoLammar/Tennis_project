package model;

public class Referee extends Person{
    private boolean micActive;
    private int experience;
    private boolean isChairUmpire;

    //Getter

    public boolean getMicActive(){
        return micActive;
    }

    public int getExperience(){
        return experience;
    }

    public boolean getIsChairUmpire(){
        return isChairUmpire;
    }

    //Setter

    public void setMicActive(boolean micActive){
        this.micActive = micActive;
    }

    public void setExperience(int experience){
        this.experience = experience;
    }

    public void setIsChairUmpire(boolean isChairUmpire){
        this.isChairUmpire = isChairUmpire;
    }

    // Methode

    public void annouceScore(String score){
        System.out.println("Score : " + score);
    }

    public void annoucefault(String type){
        System.out.println("Fault !");
    }

    public void annouceWinner(/* Player player */){
        System.out.println(" ... Win");
    }

    public void resolveDispute(/*Player p1, Player p2 */){
        System.out.println("Dispute resolved");
    }

    // Constructor

    public Referee(String birthLastName, String currentLastName, String firstName, String nickname, String nationality, int height, int weight, boolean micActive, int experience, boolean isChairUmpire){
        super(birthLastName, currentLastName, firstName, nickname, nationality, height, weight);
        this.micActive = micActive;
        this.experience = experience;
        this.isChairUmpire = isChairUmpire;
    }

    // Display info

}
