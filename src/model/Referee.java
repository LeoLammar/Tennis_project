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

    public void announceScore(String score){
        System.out.println("["+ getFirstName() +"] " + score);
    }

    public void announceNewGame(Player server, int gamesP1, int gamesP2) {
        System.out.println("----------------------------------------------------");
        System.out.println("["+ getFirstName() +"] Nouveau jeu. Score du set : " + gamesP1 + " - " + gamesP2 + ". " + server.getFirstName() + " au service.");
        System.out.println("----------------------------------------------------");
    }

    public void announceNewSet(int setsP1, int setsP2) {
        System.out.println("\n====================================================");
        System.out.println("["+ getFirstName() +"] Nouveau Set. Score du match : " + setsP1 + " - " + setsP2);
        System.out.println("====================================================");
    }

    public void annouceFault(){
        System.out.println("Fault !");
    }

    public void annouceDoubleFault(){
        System.out.println("Double fault !");
    }

    public void announceTieBreak() {
        System.out.println("----------------------------------------------------");
        System.out.println("["+ getFirstName() +"] Jeu décisif (Tie-break) !");
        System.out.println("----------------------------------------------------");
    }

    public void announceSetWinner(Player winner) {
        System.out.println("["+ getFirstName() +"] Set, " + winner.getFirstName() + ".");
    }

    public void announceMatchWinner(Player winner) {
        System.out.println("["+ getFirstName() +"] Jeu, Set et Match, " + winner.getFirstName() + " !");
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
