package model;

public class Game {
    private Player server;
    private Player receiver;
    private BallExchange ballExchange;
    private int pointsPlayer1;
    private int pointsPlayer2;

    // Getter

    public Player getServer() {
        return server;
    }

    public Player getReceiver() {
        return receiver;
    }

    public BallExchange getBallExchange() {
        return ballExchange;
    }

    public int getPointsPlayer1() {
        return pointsPlayer1;
    }

    public int getPointsPlayer2() {
        return pointsPlayer2;
    }

    // Setter

    public void setServer(Player server) {
        this.server = server;
    }

    public void setReceiver(Player receiver) {
        this.receiver = receiver;
    }

    public void setBallExchange(BallExchange ballExchange) {
        this.ballExchange = ballExchange;
    }

    public void setPointsPlayer1(int pointsPlayer1) {
        this.pointsPlayer1 = pointsPlayer1;
    }

    public void setPointsPlayer2(int pointsPlayer2) {
        this.pointsPlayer2 = pointsPlayer2;
    }

    // Constructor

    public Game(Player server, Player receiver, BallExchange ballExchange) {
        this.server = server;
        this.receiver = receiver;
        this.ballExchange = ballExchange;
        this.pointsPlayer1 = 0;
        this.pointsPlayer2 = 0;
    }

    // Methode

    public void playGame() {

    }

    public void updateScore(Player winner){
       
    }

    public void announceScore(Referee referee){
        referee.annouceScore(pointsPlayer1 + "-" + pointsPlayer2);
    }

}
