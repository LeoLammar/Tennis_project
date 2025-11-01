package app;

import model.Coach;
import model.Game;
import model.Player;
import model.Referee;
import model.Statistics;
import services.GameService;

public class Main {
    public static void main(String[] args) {
        System.out.println("--- Début de la simulation de jeu de tennis ---");

        // --- 1. Création des acteurs ---

        // On a besoin de coachs pour créer des joueurs
        Coach coachNadal = new Coach("Parera", "Parera", "Toni", "Toni", "Espagnol", 180, 80, 30);
        Coach coachFederer = new Coach("Luthi", "Luthi", "Severin", "Seve", "Suisse", 182, 82, 20);

        // Création de deux joueurs avec des classements différents
        Player player1 = new Player("Nadal", "Nadal", "Rafael", "Rafa", "Espagnol", 185, 85, "Gauche", "Nike", 2, coachNadal, "Bleu", false);
        Player player2 = new Player("Federer", "Federer", "Roger", "Rodgeur", "Suisse", 185, 85, "Droite", "Uniqlo", 10, coachFederer, "Blanc", false);

        // Création de l'arbitre
        Referee referee = new Referee("Lahyani", "Lahyani", "Mohamed", "Mo", "Suédois", 178, 75, true, 25, true);

        System.out.println("\nMatch à venir : " + player1.getFirstName() + " (classement " + player1.getRanking() + ") vs " + player2.getFirstName() + " (classement " + player2.getRanking() + ")");
        System.out.println("Arbitre : " + referee.getFirstName() + "\n");

        // --- 2. Création des objets de contexte ---

        // L'objet qui stockera les statistiques du match
        Statistics matchStatistics = new Statistics();

        // Le service qui contient la logique du jeu
        GameService gameService = new GameService();

        // --- 3. Lancement de la simulation ---
        System.out.println(">>> Le jeu commence ! " + player1.getFirstName() + " au service. <<<");
        Game playedGame = gameService.playGame(player1, player2, referee, matchStatistics);

        // --- 4. Affichage des résultats ---
        System.out.println("\n>>> Fin du jeu ! <<<");
        System.out.println("Le gagnant du jeu est : " + playedGame.getWinner().getFirstName());
        System.out.println("\nStatistiques finales du match après ce jeu :");
        System.out.println(matchStatistics.toString());
    }
}
