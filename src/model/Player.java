package model;

public class Player extends Person {

    // Attribut

    private String playingHand;
    private String sponsor;
    private int ranking;
    private boolean isFemale;
    private PlayStyle playStyle;

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

    public boolean getIsFemale(){
        return isFemale;
    }

    public PlayStyle getPlayStyle() {
        return playStyle;
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

    public void setIsFemale(boolean isFemale){
        this.isFemale = isFemale;
    }

    public void setPlayStyle(PlayStyle playStyle) {
        this.playStyle = playStyle;
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

    public void celebrateVictory(){
        System.out.println("Victory !");
    }

    public void disputeCall(){
        System.out.println("You're wrong !");
    }

    // Constructor

    public Player(String birthLastName, String currentLastName, String firstName, String nickname, String nationality, int height, int weight, String playingHand, String sponsor, int ranking, boolean isFemale, PlayStyle playStyle){
        super(birthLastName, currentLastName, firstName, nickname, nationality, height, weight);
        this.playingHand = playingHand;
        this.sponsor = sponsor;
        this.ranking = ranking;
        this.isFemale = isFemale;
        this.playStyle = playStyle;
    }

    // Display

    @Override
    public String toString() {
        return super.toString().replaceFirst("]$", ", playingHand=" + playingHand + ", sponsor=" + sponsor + ", ranking=" + ranking + ", isFemale=" + isFemale + ", playStyle=" + playStyle + "]");
    }
}
