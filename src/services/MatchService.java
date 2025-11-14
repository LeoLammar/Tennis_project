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

    public Match playMatch(Match match, int setsToWin) {
        Player player1 = match.getPlayer1();
        Player player2 = match.getPlayer2();
        Referee referee = match.getReferee();
        Statistics matchStats = match.getStatistics(); // On utilise les stats du match passé en paramètre
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
