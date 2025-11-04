package services;

import model.BallExchange;
import model.Game;
import model.Player;
import model.Statistics;
import model.Referee;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameService {

    private final Random random = new Random();
    
    
    public Game playGame(Player server, Player receiver, Referee referee, Statistics matchStats) {
        List<BallExchange> exchanges = new ArrayList<>();
        Game game = new Game(server, receiver, null); // On mettra le tableau à la fin

        int scoreServer = 0; 
        int scoreReceiver = 0;

        while (game.getWinner() == null) {
            // Simuler un échange de balle
            Player pointWinner = simulatePoint(server, receiver, matchStats, referee);
            BallExchange exchange = new BallExchange();
            exchange.setWinner(pointWinner);
            exchanges.add(exchange);

            if (pointWinner.equals(server)) {
                scoreServer++;
            } else {
                scoreReceiver++;
            }

            // L'arbitre annonce le score après chaque point
            String scoreString = convertScoreToString(scoreServer, scoreReceiver, server, receiver);
            referee.announceScore(scoreString);

            // Vérifier si le jeu est terminé
            if (scoreServer >= 4 && scoreServer >= scoreReceiver + 2) {
                game.setWinner(server);
            } else if (scoreReceiver >= 4 && scoreReceiver >= scoreServer + 2) {
                game.setWinner(receiver);
            }
        }

        game.setBallExchanges(exchanges);
        matchStats.updateGamesWon();
        return game;
    }

    
    private String convertScoreToString(int scoreP1, int scoreP2, Player p1, Player p2) {
        // On vérifie d'abord si le jeu est terminé
        if (scoreP1 >= 4 && scoreP1 >= scoreP2 + 2) {
            return "Jeux pour, " + p1.getFirstName();
        }
        if (scoreP2 >= 4 && scoreP2 >= scoreP1 + 2) {
            return "Jeux pour, " + p2.getFirstName();
        }

        if (scoreP1 >= 3 && scoreP2 >= 3) {
            if (scoreP1 == scoreP2) {
                return "Egalité"; 
            } else if (scoreP1 > scoreP2) {
                return "Advantage " + p1.getFirstName(); // Avantage serveur
            } else {
                return "Advantage " + p2.getFirstName(); // Avantage receveur
            }
        } else {
            // Scores de base : 0, 15, 30, 40
            String[] points = {"0", "15", "30", "40"};
            return points[scoreP1] + " - " + points[scoreP2];
        }
    }

    public Player playTieBreak(Player player1, Player player2, Referee referee, Statistics matchStats) {
        int scoreP1 = 0;
        int scoreP2 = 0;
        Player winner = null;

        // Dans un tie-break, le service alterne différemment, mais pour simplifier,
        // nous allons garder une simulation de point simple.
        while (winner == null) {
            // On simule un point sans se soucier du serveur pour cette version simplifiée
            Player pointWinner = playRally(player1, player2);
            if (pointWinner.equals(player1)) {
                scoreP1++;
            } else {
                scoreP2++;
            }

            referee.announceScore(scoreP1 + " - " + scoreP2);

            if (scoreP1 >= 7 && scoreP1 >= scoreP2 + 2) {
                winner = player1;
            } else if (scoreP2 >= 7 && scoreP2 >= scoreP1 + 2) {
                winner = player2;
            }
        }
        
        // On met à jour les stats de jeu gagné pour le vainqueur du tie-break
        if(winner.equals(player1)) {
            matchStats.updateGamesWon();
        } else {
            matchStats.updateGamesWon();
        }

        return winner;
    }

    private Player simulatePoint(Player server, Player receiver, Statistics stats, Referee referee) {
        stats.updateTotalPoints(); // Un point est joué quoi qu'il arrive.

        // --- 1. Premier service ---
        boolean firstServeIn = random.nextInt(100) < 70; // 70% de chance que le 1er service soit bon

        if (firstServeIn) {
            stats.updateFirstServes();
            stats.updateAvgServeSpeed(random.nextInt(50) + 180); // Vitesse entre 180 et 229 km/h

            // Chance d'ace sur premier service
            boolean isAce = random.nextInt(100) < 15; // 15% de chance de faire un ace
            if (isAce) {
                stats.updateAces();
                return server; // Le serveur gagne le point
            }
            // Si pas d'ace, l'échange se joue normalement
            return playRally(server, receiver);
        }

        // --- 2. Second service (si le premier est faute) ---
        referee.annouceFault(); // Annonce de la faute sur le premier service

        boolean secondServeIn = random.nextInt(100) < 90; // 90% de chance que le 2nd service soit bon (plus prudent)

        if (secondServeIn) {
            stats.updateSecondServes();
            stats.updateAvgServeSpeed(random.nextInt(30) + 150); // Vitesse entre 150 et 179 km/h
            // L'échange se joue normalement
            return playRally(server, receiver);
        }

        // --- 3. Double faute ---
        referee.annouceDoubleFault(); // Annonce de la double faute
        stats.updateDoublesFaults();
        return receiver; // Le receveur gagne le point
    }


    private Player playRally(Player p1, Player p2) {
        // Nouvelle logique de probabilité plus équilibrée
        final int BASE_CHANCE = 100; // Un poids de base pour chaque joueur
        final double ADVANTAGE_FACTOR = 2.5; // Facteur d'avantage par point de classement d'écart

        Player betterPlayer = p1.getRanking() < p2.getRanking() ? p1 : p2;
        Player worsePlayer = p1.getRanking() < p2.getRanking() ? p2 : p1;

        int rankDifference = worsePlayer.getRanking() - betterPlayer.getRanking();

        // Le poids du meilleur joueur est augmenté en fonction de l'écart de classement
        double betterPlayerWeight = BASE_CHANCE + (rankDifference * ADVANTAGE_FACTOR);
        double totalWeight = betterPlayerWeight + BASE_CHANCE;

        // On tire un nombre aléatoire dans la somme des poids
        double randomValue = random.nextDouble() * totalWeight; // Un nombre entre 0.0 et totalWeight

        // Si la valeur aléatoire tombe dans la "part" du meilleur joueur, il gagne.
        if (randomValue < betterPlayerWeight) {
            return betterPlayer;
        } else {
            return worsePlayer;
        }
    }
}
