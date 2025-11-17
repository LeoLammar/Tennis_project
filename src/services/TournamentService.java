package services;

import model.Match;
import model.Player;
import model.Referee;
import model.Statistics;
import model.Tournament;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TournamentService {

    private final MatchService matchService = new MatchService();
    public Tournament createTournament(String name, int year, List<Player> players, Referee[] referees) {
        // Créer un tournoi avec les joueurs et arbitres spécifiés
       Tournament tournament = new Tournament(name, "Ville", "Surface", "Catégorie", year, players.toArray(new Player[0]), referees);
       return tournament;
    }

    public Match generateMatch(Player player1, Player player2, Referee referee, Statistics matchStats, int setsToWin) {
        // Générer un match entre deux joueurs
        return new Match(player1, player2, referee, null, null, matchStats);
    }

    public Match playMatch(Match match, int setsToWin) {        
        // On appelle playMatch de MatchService, mais on met à jour l'objet Match original
        return matchService.playMatch(match, setsToWin);
    }

    public List<Match> generateTournamentRound(List<Player> players, Referee[] referees) {
        // Générer un tour de matches
        List<Match> matches = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < players.size(); i += 2) {
            Player player1 = players.get(i);
            Player player2 = players.get(i + 1);
            Referee referee = referees[random.nextInt(referees.length)]; // Sélectionner un arbitre au hasard
            Statistics matchStatistics = new Statistics(); // Créer un nouvel objet de statistiques pour chaque match

            Match match = generateMatch(player1, player2, referee, matchStatistics, 2); // Par exemple, des matchs en 3 sets gagnants
            matches.add(match);
        }

        return matches;
    }

}
