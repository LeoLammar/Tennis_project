package services;

import model.Game;
import model.Player;
import model.Referee;
import model.Set;
import model.Statistics;

import java.util.ArrayList;
import java.util.List;

public class SetService {

    private final GameService gameService = new GameService();

    public Set playSet(Player player1, Player player2, Referee referee, Statistics matchStats) {
        List<Game> playedGames = new ArrayList<>();
        Set set = new Set(null, 0, 0); // On cré un set vide pour l'instant

        int gamesWonP1 = 0;
        int gamesWonP2 = 0;

        // On décide que player1 commence à servir dans le set
        Player currentServer = player1;
        Player currentReceiver = player2;

        while (set.getWinner() == null) {
            referee.announceNewGame(currentServer, gamesWonP1, gamesWonP2);
            Game playedGame = gameService.playGame(currentServer, currentReceiver, referee, matchStats);
            playedGames.add(playedGame);

            if (playedGame.getWinner().equals(player1)) {
                gamesWonP1++;
            } else {
                gamesWonP2++;
            }

            // On inverse le serveur pour le prochain jeu
            Player temp = currentServer;
            currentServer = currentReceiver;
            currentReceiver = temp;

            // Ajout de la logique du Tie-Break
            if (gamesWonP1 == 6 && gamesWonP2 == 6) {
                referee.announceTieBreak();
                Player tieBreakWinner = gameService.playTieBreak(player1, player2, referee, matchStats);
                set.setWinner(tieBreakWinner);
            }
            // Vérification des conditions de victoire du set (sans tie-break pour l'instant)
            if (gamesWonP1 >= 6 && gamesWonP1 >= gamesWonP2 + 2) {
                set.setWinner(player1);
                referee.announceSetWinner(player1);
            } else if (gamesWonP2 >= 6 && gamesWonP2 >= gamesWonP1 + 2) {
                set.setWinner(player2);
                referee.announceSetWinner(player2);
            }
        }

        set.setGames(playedGames.toArray(new Game[0]));
        matchStats.updateSetWon();
        return set;
    }
}
