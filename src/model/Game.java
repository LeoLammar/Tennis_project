package model;

public class Game {
    private Player server;
    private Player receiver;
    private BallExchange ballExchange[];
    private Player winner;

    // Getter

    public Player getServer() {
        return server;
    }

    public Player getReceiver() {
        return receiver;
    }

    public BallExchange[] getBallExchange() {
        return ballExchange;
    }

    public Player getWinner() {
        return winner;
    }

    // Setter

    public void setServer(Player server) {
        this.server = server;
    }

    public void setReceiver(Player receiver) {
        this.receiver = receiver;
    }

    public void setBallExchange(BallExchange[] ballExchange) {
        this.ballExchange = ballExchange;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    // Constructor

    public Game(Player server, Player receiver, BallExchange[] ballExchange) {
        this.server = server;
        this.receiver = receiver;
        this.ballExchange = ballExchange;
        this.winner = null;
    }

    


}
