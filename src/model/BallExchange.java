package model;

public class BallExchange {
    
    private Player winner;   

    // Getter

    public Player getWinner() {
        return winner;
    }

    // Setter

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    // Constructor
    public BallExchange() {
        this.winner = null;
    }

    // Methode 

    @Override   
    public String toString(){
        return "The winner is " + getWinner().getFirstName();
    }


}
