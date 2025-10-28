package model;

import java.util.Random;

public class BallExchange {
    public boolean isfault;
    public boolean isLet;
    public Player winner;

    // Getter

    public boolean getIsfault() {
        return isfault;
    }

    public boolean getIsLet() {
        return isLet;
    }

    public Player getWinner() {
        return winner;
    }

    // Setter

    public void setIsfault(boolean isfault) {
        this.isfault = isfault;
    }

    public void setIsLet(boolean isLet) {
        this.isLet = isLet;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    // Methode

    public void playExchange(Player player1, Player player2) {
 
        Random random = new Random();

        int winnerChoice = random.nextInt(2);

        switch (winnerChoice) {
            case 0:
                setWinner(player1);
                break;
            case 1:
                setWinner(player2);
                break;
        }
    }


    // Constructor 

    public BallExchange(boolean isfault, boolean isLet, Player winner){
        this.isfault = false;
        this.isLet = false;
        this.winner = null;
    }

    // Display

    @Override
    public String toString() {
        return "BallExchange [Winner is " + winner + "]";
    }
}
