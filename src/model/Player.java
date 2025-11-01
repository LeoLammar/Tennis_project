package model;

public class Player extends Person {

    // Attribut

    private String playingHand;
    private String sponsor;
    private int ranking;
    private Coach coach;
    private String outfitColor;
    private boolean isFemale;

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

    public Player(String birthLastName, String currentLastName, String firstName, String nickname, String nationality, int height, int weight, String playingHand, String sponsor, int ranking, Coach coach, String outfitColor, boolean isFemale){
        super(birthLastName, currentLastName, firstName, nickname, nationality, height, weight);
        this.playingHand = playingHand;
        this.sponsor = sponsor;
        this.ranking = ranking;
        this.coach = coach;
        this.outfitColor = outfitColor;
        this.isFemale = isFemale;
    }

    // Display

    @Override
    public String toString() {
        return super.toString().replaceFirst("]$", ", playingHand=" + playingHand + ", sponsor=" + sponsor + ", ranking=" + ranking + ", coach=" + coach + ", outfitColor=" + outfitColor + ", isFemale=" + isFemale + "]");
    }
}
