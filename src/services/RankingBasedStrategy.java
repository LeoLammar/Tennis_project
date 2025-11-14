package services;

import model.Player;

import java.util.Random;

public class RankingBasedStrategy implements PlayerStrategy {

    private final Random random = new Random();

    @Override
    public Player getPointWinner(Player p1, Player p2) {
        final int BASE_CHANCE = 100;
        final double ADVANTAGE_FACTOR = 2.5;

        Player betterPlayer = p1.getRanking() < p2.getRanking() ? p1 : p2;
        Player worsePlayer = p1.getRanking() < p2.getRanking() ? p2 : p1;

        int rankDifference = worsePlayer.getRanking() - betterPlayer.getRanking();

        double betterPlayerWeight = BASE_CHANCE + (rankDifference * ADVANTAGE_FACTOR);
        double totalWeight = betterPlayerWeight + BASE_CHANCE;

        double randomValue = random.nextDouble() * totalWeight;

        if (randomValue < betterPlayerWeight) {
            return betterPlayer;
        } else {
            return worsePlayer;
        }
    }
}
