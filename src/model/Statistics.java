package model;

public class Statistics {
    private int aces;
    private int doublesFaults;
    private int firstServes;
    private int secondServes;
    private int gamesWon;
    private int setWon;
    private int totalPoints;
    private int avgServeSpeed;

    // Getter

    public int getAces() {
        return aces;
    }

    public int getDoublesFaults() {
        return doublesFaults;
    }

    public int getFirstServes() {
        return firstServes;
    }

    public int getSecondServes() {
        return secondServes;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public int getSetWon() {
        return setWon;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public int getAvgServeSpeed() {
        return avgServeSpeed;
    }

    // Setter

    public void setAces(int aces) {
        this.aces = aces;
    }

    public void setDoublesFaults(int doublesFaults) {
        this.doublesFaults = doublesFaults;
    }

    public  void setFirstServes(int firstServes) {
        this.firstServes = firstServes;
    }

    public void setSecondServes(int secondServes) {
        this.secondServes = secondServes;
    }

    public void setGamesWon(int gamesWon) {
        this.gamesWon = gamesWon;
    }

    public void setSetWon(int setWon) {
        this.setWon = setWon;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public void setAvgServeSpeed(int avgServeSpeed) {
        this.avgServeSpeed = avgServeSpeed;
    }

    // Constructor

    public  Statistics() {
        this.aces = 0;
        this.doublesFaults = 0;
        this.firstServes = 0;
        this.secondServes = 0;
        this.gamesWon = 0;
        this.setWon = 0;
        this.totalPoints = 0;
        this.avgServeSpeed = 0;
    }

    // Methode

    public void updateAces(){
        this.aces++;
    }

    public void updateDoublesFaults(){
        this.doublesFaults++;
    }

    public void updateFirstServes(){
        this.firstServes++;
    }

    public void updateSecondServes(){
        this.secondServes++;
    }

    public void updateGamesWon(){
        this.gamesWon++;
    }

    public void updateSetWon(){
        this.setWon++;
    }

    public void updateTotalPoints(){
        this.totalPoints++;
    }

    public void updateAvgServeSpeed(int speed){
        this.avgServeSpeed += speed;
        this.avgServeSpeed /= 2;
    }

    // Display

    @Override
    public String toString() {  
        return "Statistics [aces=" + aces + ", doublesFaults=" + doublesFaults + ", firstServes=" + firstServes + ", secondServes=" + secondServes + ", gamesWon=" + gamesWon + ", setWon=" + setWon + ", totalPoints=" + totalPoints + ", avgServeSpeed=" + avgServeSpeed + "]";
    }

}
