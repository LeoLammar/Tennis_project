package services;

import model.Player;
import model.PlayStyle;

import java.util.Random;

/**
 * Stratégie pour un joueur au style agressif.
 * Ce style favorise la puissance et la prise de risque. Il augmente les chances de gagner
 * un point directement, mais introduit aussi un risque d'erreur non forcée.
 */
public class AggressivePlayerStrategy implements PlayerStrategy {

    private final Random random = new Random();
    private final RankingBasedStrategy fallbackStrategy = new RankingBasedStrategy();

    @Override
    public Player getPointWinner(Player player1, Player player2) {
        // On suppose que la classe Player a une méthode getPlayStyle()
        boolean p1IsAggressive = player1.getPlayStyle() == PlayStyle.AGGRESSIVE;
        boolean p2IsAggressive = player2.getPlayStyle() == PlayStyle.AGGRESSIVE;

        // Si un seul joueur est agressif, il a une forte chance de gagner le point...
        if (p1IsAggressive && !p2IsAggressive) {
            // 70% de chance de gagner le point grâce à l'agressivité
            return random.nextInt(100) < 70 ? player1 : player2;
        }

        if (p2IsAggressive && !p1IsAggressive) {
            // 70% de chance de gagner le point grâce à l'agressivité
            return random.nextInt(100) < 70 ? player2 : player1;
        }

        // Si les deux joueurs ont le même style (agressif ou non),
        // on se rabat sur la stratégie de base basée sur le classement.
        return fallbackStrategy.getPointWinner(player1, player2);
    }
}