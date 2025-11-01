package model;

import java.util.List;

public class Game {
    private Player server;
    private Player receiver;
    private List<BallExchange> ballExchanges;
    private Player winner;

    // Getter

    public Player getServer() {
        return server;
    }

    public Player getReceiver() {
        return receiver;
    }

    public List<BallExchange> getBallExchanges() {
        return ballExchanges;
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

    public void setBallExchanges(List<BallExchange> ballExchanges) {
        this.ballExchanges = ballExchanges;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    // Constructor

    public Game(Player server, Player receiver, List<BallExchange> ballExchanges) {
        this.server = server;
        this.receiver = receiver;
        this.ballExchanges = ballExchanges;
        this.winner = null;
    }

    


}
