package model;

public class Match {
    private Player player1;
    private Player player2;
    private Referee referee;
    private Set sets[];
    private Player winner;
    private Statistics statistics;
    private boolean manualMode;

    // Getter

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public Referee getReferee() {
        return referee;
    }

    public Set[] getSets() {
        return sets;
    }

    public Player getWinner() {
        return winner;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public boolean getManualMode() {
        return manualMode;
    }

    // Setter

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }

    public void setReferee(Referee referee) {
        this.referee = referee;
    }

    public void setSets(Set[] sets) {
        this.sets = sets;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }

    public void setManualMode(boolean manualMode) {
        this.manualMode = manualMode;
    }

    // Constructor

    public Match(Player player1, Player player2, Referee referee, Set[] sets, Player winner, Statistics statistics) {
        this.player1 = player1;
        this.player2 = player2;
        this.referee = referee;
        this.sets = sets;
        this.winner = winner;
        this.statistics = statistics;
        this.manualMode = false;
    }



    
}
