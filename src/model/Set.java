package model;

public class Set {
    private Game games[];
    private Player winner;
    private int gameswonPlayer1;
    private int gameswonPlayer2;

    // Getter

    public Game[] getGames() {
        return games;
    }

    public Player getWinner() {
        return winner;
    }

    public int getGameswonPlayer1() {
        return gameswonPlayer1;
    }

    public int getGameswonPlayer2() {
        return gameswonPlayer2;
    }

    // Setter

    public void setGames(Game[] games) {
        this.games = games;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    public  void setGameswonPlayer1(int gameswonPlayer1) {
        this.gameswonPlayer1 = gameswonPlayer1;
    }

    public void setGameswonPlayer2(int gameswonPlayer2) {
        this.gameswonPlayer2 = gameswonPlayer2;
    }

    // Constructor

    public Set(Game[] games, Player winner, int gameswonPlayer1, int gameswonPlayer2) {
        this.games = games;
        this.winner = winner;
        this.gameswonPlayer1 = gameswonPlayer1;
        this.gameswonPlayer2 = gameswonPlayer2;
    }

    // Methode

    public void playSet(Player player1, Player player2, Referee referee) {
        games = new Game[7];
        for (int i = 0; i < games.length; i++) {
            games[i] = new Game(player1, player2, null);
        }
    }


    
}
