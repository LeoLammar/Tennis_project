package services;

import java.util.ArrayList;
import java.util.List;

import model.Match;
import model.Player;
import model.Referee;
import model.Set;
import model.Statistics;

public class MatchService {
    private final SetService setService = new SetService();

    /**
     * Simule un match complet entre deux joueurs.
     * @param player1 Le premier joueur.
     * @param player2 Le deuxième joueur.
     * @param referee L'arbitre du match.
     * @param matchStats Les statistiques globales du match.
     * @param setsToWin Le nombre de sets requis pour gagner le match (ex: 2 pour un "best-of-3", 3 pour un "best-of-5").
     * @return L'objet Match avec le résultat final.
     */
    public Match playMatch(Player player1, Player player2, Referee referee, Statistics matchStats, int setsToWin) {
        Match match = new Match(player1, player2, referee, null, null, matchStats);
        List<Set> playedSets = new ArrayList<>();

        int setsWonP1 = 0;
        int setsWonP2 = 0;

        while (match.getWinner() == null) {
            referee.announceNewSet(setsWonP1, setsWonP2);
            Set playedSet = setService.playSet(player1, player2, referee, matchStats);
            playedSets.add(playedSet);

            if (playedSet.getWinner().equals(player1)) {
                setsWonP1++;
            } else {
                setsWonP2++;
            }

            if (setsWonP1 == setsToWin) {
                match.setWinner(player1);
            }
            if (setsWonP2 == setsToWin) {
                match.setWinner(player2);
            }
        }

        match.setSets(playedSets.toArray(new Set[0]));
        referee.announceMatchWinner(match.getWinner());
        return match;
    }
}
