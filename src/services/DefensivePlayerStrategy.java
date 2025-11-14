package services;

import model.Player;
import model.PlayStyle;

import java.util.Random;

/**
 * Stratégie pour un joueur au style défensif.
 * Ce style est basé sur la régularité et la capacité à contrer les attaques.
 * Un joueur défensif a un avantage contre un joueur agressif en le poussant à la faute.
 */
public class DefensivePlayerStrategy implements PlayerStrategy {

    private final Random random = new Random();
    private final RankingBasedStrategy fallbackStrategy = new RankingBasedStrategy();

    @Override
    public Player getPointWinner(Player player1, Player player2) {
        // On suppose que la classe Player a une méthode getPlayStyle()
        PlayStyle styleP1 = player1.getPlayStyle();
        PlayStyle styleP2 = player2.getPlayStyle();

        // Le défenseur a un avantage sur l'agressif
        if (styleP1 == PlayStyle.DEFENSIVE && styleP2 == PlayStyle.AGGRESSIVE) {
            // 65% de chance pour le défenseur de remporter le point en contrant
            return random.nextInt(100) < 65 ? player1 : player2;
        }

        if (styleP2 == PlayStyle.DEFENSIVE && styleP1 == PlayStyle.AGGRESSIVE) {
            return random.nextInt(100) < 65 ? player2 : player1;
        }

        // Dans les autres cas de figure (défensif vs défensif, etc.),
        // on utilise la logique standard basée sur le classement.
        return fallbackStrategy.getPointWinner(player1, player2);
    }
}