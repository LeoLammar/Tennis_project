package model;

public class Set {
    private Game games[];
    private Player winner;

    // Getter

    public Game[] getGames() {
        return games;
    }

    public Player getWinner() {
        return winner;
    }

    // Setter

    public void setGames(Game[] games) {
        this.games = games;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    // Constructor

    public Set(Game[] games, int gameswonPlayer1, int gameswonPlayer2) {
        this.games = games;
        this.winner = null;
    }
    
}
