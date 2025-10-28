package model;

public class Player extends Person {

    // Attribut

    private String playingHand;
    private String sponsor;
    private int ranking;
    private Coach coach;
    private String outfitColor;
    private boolean isFemale;
    private int totalPoints;

    // Getter

    public String getPlayingHand(){
        return playingHand;
    }

    public String getSponsor(){
        return sponsor;
    }

    public int getRanking(){
        return ranking;
    }

    public Coach getCoach(){
        return coach;
    }

    public String getOutfitColor(){
        return outfitColor;
    }

    public boolean getIsFemale(){
        return isFemale;
    }

    public int getTotalPoints(){
        return totalPoints;
    }

    // Setter

    public void setPlayingHand(String playingHand){
        this.playingHand = playingHand;
    }

    public void setSponsor(String sponsor){
        this.sponsor = sponsor;
    }

    public void setRanking(int ranking){
        this.ranking = ranking;
    }

    public void setCoach(Coach coach){
        this.coach = coach;
    }

    public void setOutfitColor(String outfitColor){
        this.outfitColor = outfitColor;
    }

    public void setIsFemale(boolean isFemale){
        this.isFemale = isFemale;
    }
    
    public void setTotalPoints(int totalPoints){
        this.totalPoints = totalPoints;
    }

    // Methode

    public void serve(){
        System.out.println("serve");
    }

    public void returnBall(){
        System.out.println("ball returned");
    }

    public void shout(){
        System.out.println("Ah !");
    }

    public void changeOutfitColor(String color){
        setOutfitColor(color);
    }

    public void celebrateVictory(){
        System.out.println("Victory !");
    }

    public void disputeCall(){
        System.out.println("You're wrong !");
    }

    // Constructor

    public Player(String birthLastName, String currentLastName, String firstName, String nickname, String nationality, int height, int weight, String playingHand){
        super(birthLastName, currentLastName, firstName, nickname, nationality, height, weight);
        this.playingHand = playingHand;

    }

    // Display

    @Override
    public String toString() {
        return super.toString().replaceFirst("]$", ", playingHand=" + playingHand + ", sponsor=" + sponsor + ", ranking=" + ranking + ", coach=" + coach + ", outfitColor=" + outfitColor + ", isFemale=" + isFemale + ", totalPoints=" + totalPoints + "]");
    }
}
