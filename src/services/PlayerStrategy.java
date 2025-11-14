package services;

import model.Player;

/**
 * Interface pour définir une stratégie ou un comportement de joueur lors d'un échange.
 * Chaque implémentation peut simuler un style de jeu différent.
 */
public interface PlayerStrategy {
    Player getPointWinner(Player player1, Player player2);
}
