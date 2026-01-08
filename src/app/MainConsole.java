package app;

import model.*;
import services.TournamentService;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class MainConsole {

    // État tournoi
    private Tournament currentTournament;
    private List<Player> currentRoundPlayers;
    private List<Match> currentRoundMatches;
    private List<Player> nextRoundPlayers;
    private List<List<Match>> allRoundsMatches;
    private int roundNumber;
    private boolean isTournamentOver;

    // Séquence Grand Chelem
    private List<String> grandSlamChronologicalOrder;
    private int currentTournamentIndex;

    private Referee[] referees;

    public MainConsole() {
        this.referees = createReferees();
        this.grandSlamChronologicalOrder = List.of("Australian Open", "Roland Garros", "Wimbledon", "US Open");
        this.currentTournamentIndex = 0;
    }

    public void run() {
        System.out.println("=== Simulation de Match de Tennis (Console) ===");
        System.out.println("Appuie sur Entrée pour jouer le match suivant.");
        System.out.println("Tape 'q' puis Entrée pour quitter.\n");

        startNextTournamentInSequence(); // démarre le 1er tournoi direct

        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                String line = sc.nextLine().trim();
                if (line.equalsIgnoreCase("q")) {
                    System.out.println("Arrêt demandé. Au revoir.");
                    break;
                }

                // “Entrée” => match suivant
                playNextMatchConsole();
            }
        }
    }

    private Referee[] createReferees() {
        Referee[] refereesArray = new Referee[5];
        refereesArray[0] = new Referee("Lahyani", "Lahyani", "Mohamed", "Mo", "Suédois", 178, 75, true, 25, true);
        refereesArray[1] = new Referee("Larsson", "Larsson", "Eva", "Eva", "Suédois", 170, 65, true, 20, true);
        refereesArray[2] = new Referee("Ravi", "Ravi", "Ravi", "Ravi", "Indien", 180, 80, true, 30, true);
        refereesArray[3] = new Referee("Zhang", "Zhang", "Wei", "Wei", "Chinois", 175, 70, true, 22, true);
        refereesArray[4] = new Referee("Johnson", "Johnson", "John", "John", "Américain", 185, 90, true, 28, true);
        return refereesArray;
    }

    private Tournament createTournament(String tournamentName) {
        List<Player> allPlayers = createPlayers();
        Collections.shuffle(allPlayers);

        // sécurité si ton CSV a moins de 128 joueurs
        int n = Math.min(128, allPlayers.size());
        List<Player> tournamentPlayers = new ArrayList<>(allPlayers.subList(0, n));

        int year = 2025;
        Tournament tournament = new Tournament(
                tournamentName, "Ville", "Surface", "Catégorie", year,
                tournamentPlayers.toArray(new Player[0]),
                this.referees
        );

        System.out.println("Tournoi '" + tournament.getName() + "' créé avec " + tournament.getPlayers().length + " joueurs.");
        return tournament;
    }

    private void startTournament(Tournament tournament) {
        this.currentTournament = tournament;
        this.roundNumber = 1;
        this.currentRoundPlayers = new ArrayList<>(List.of(tournament.getPlayers()));
        this.isTournamentOver = false;
        this.nextRoundPlayers = new ArrayList<>();
        this.allRoundsMatches = new ArrayList<>();

        TournamentService tournamentService = new TournamentService();
        this.currentRoundMatches = tournamentService.generateTournamentRound(this.currentRoundPlayers, tournament.getReferees());
        this.allRoundsMatches.add(new ArrayList<>(currentRoundMatches));

        System.out.println("\n====================================================");
        System.out.println("Début du tournoi : " + tournament.getName());
        System.out.println("====================================================");
        printRoundHeaderAndMatches();
        printBracketText();
    }

    private void playNextMatchConsole() {
        // Si le tournoi précédent est terminé, ce “clic” lance le suivant
        if (isTournamentOver) {
            startNextTournamentInSequence();
            return;
        }

        if (currentRoundMatches == null) {
            System.out.println("Aucun tournoi en cours.");
            return;
        }

        if (currentRoundMatches.isEmpty()) {
            // Fin de tour -> prochain tour ou fin du tournoi
            if (nextRoundPlayers.size() == 1) {
                displayTournamentWinner();
                isTournamentOver = true;
                System.out.println("\n(Entrée = passer au tournoi suivant)");
                return;
            }

            roundNumber++;
            currentRoundPlayers = new ArrayList<>(nextRoundPlayers);
            nextRoundPlayers.clear();
            Collections.shuffle(currentRoundPlayers);

            TournamentService tournamentService = new TournamentService();
            currentRoundMatches = tournamentService.generateTournamentRound(currentRoundPlayers, currentTournament.getReferees());
            allRoundsMatches.add(new ArrayList<>(currentRoundMatches));

            printRoundHeaderAndMatches();
            printBracketText();
        }

        if (!currentRoundMatches.isEmpty()) {
            Match matchToPlay = currentRoundMatches.remove(0);

            // Console => on joue en synchrone (pas de Task JavaFX)
            Match playedMatch = new TournamentService().playMatch(matchToPlay, 2);

            nextRoundPlayers.add(playedMatch.getWinner());

            String matchDescription = playedMatch.getPlayer1().getFirstName() + " vs " + playedMatch.getPlayer2().getFirstName();
            System.out.println("\nRésultat du match " + matchDescription + " : Vainqueur = " + playedMatch.getWinner().getFirstName());

            displayMatchStatistics(playedMatch);
            printBracketText();
        }
    }

    private void startNextTournamentInSequence() {
        isTournamentOver = false;

        if (currentTournamentIndex < grandSlamChronologicalOrder.size()) {
            String tournamentName = grandSlamChronologicalOrder.get(currentTournamentIndex);
            currentTournamentIndex++;

            Tournament newTournament = createTournament(tournamentName);
            startTournament(newTournament);
        } else {
            System.out.println("\n\n====================================================");
            System.out.println("La saison du Grand Chelem 2025 est terminée !");
            System.out.println("====================================================\n");

            // reset pour recommencer si tu veux
            currentTournamentIndex = 0;
            System.out.println("Tape Entrée pour relancer une saison, ou 'q' pour quitter.");
        }
    }

    private void printRoundHeaderAndMatches() {
        System.out.println("\n--- Tour " + roundNumber + " ---");
        System.out.println("Matchs à jouer :");
        for (Match match : currentRoundMatches) {
            System.out.println("- " + match.getPlayer1().getFirstName() + " vs " + match.getPlayer2().getFirstName());
        }
    }

    /**
     * Remplace ton GridPane: affiche un “tableau” en texte.
     * Simple, lisible, et 100% console.
     */
    private void printBracketText() {
        System.out.println("\n===== Tableau (texte) : " + currentTournament.getName() + " =====");
        for (int round = 0; round < allRoundsMatches.size(); round++) {
            System.out.println("Tour " + (round + 1) + ":");
            List<Match> roundMatches = allRoundsMatches.get(round);
            for (int i = 0; i < roundMatches.size(); i++) {
                Match m = roundMatches.get(i);
                String p1 = m.getPlayer1().getFirstName();
                String p2 = m.getPlayer2().getFirstName();

                String line = "  [" + (i + 1) + "] " + p1 + " vs " + p2;

                if (m.getWinner() != null) {
                    line += "  ->  Gagnant: " + m.getWinner().getFirstName();
                } else {
                    // Si le match a déjà été joué via playMatch(matchToPlay, ...),
                    // ton objet 'playedMatch' a un winner, mais l'objet stocké dans allRoundsMatches
                    // est peut-être celui d'avant. Si tu veux afficher le winner ici,
                    // il faut que playMatch modifie le même objet Match (ou que tu remplaces l'objet).
                    line += "  ->  (pas encore joué / pas propagé)";
                }
                System.out.println(line);
            }
            System.out.println();
        }
        System.out.println("=============================================\n");
    }

    private void displayMatchStatistics(Match match) {
        Statistics stats = match.getStatistics();
        if (stats != null) {
            System.out.println("\n--- Statistiques du Match ---");
            System.out.println("Joueurs : " + match.getPlayer1().getFirstName() + " vs " + match.getPlayer2().getFirstName());
            System.out.println("Vainqueur : " + match.getWinner().getFirstName());
            System.out.println("-----------------------------");
            System.out.println("Aces : " + stats.getAces());
            System.out.println("Doubles Fautes : " + stats.getDoublesFaults());
            System.out.println("Pourcentage de 1er service : " + String.format("%.2f",
                    (double) stats.getFirstServes() / stats.getTotalPoints() * 100) + "%");
            System.out.println("Pourcentage de 2nd service : " + String.format("%.2f",
                    (double) stats.getSecondServes() / stats.getTotalPoints() * 100) + "%");
            System.out.println("Vitesse moyenne du service : " + String.format("%.2f", (double) stats.getAvgServeSpeed()) + " km/h");
            System.out.println("Total des points joués : " + stats.getTotalPoints());
            System.out.println("-----------------------------\n");
        } else {
            System.out.println("Aucune statistique disponible pour ce match.");
        }
    }

    private void displayTournamentWinner() {
        if (nextRoundPlayers.size() == 1 && currentTournament != null) {
            Player winner = nextRoundPlayers.get(0);
            System.out.println("\n\n====================================================");
            System.out.println("Le vainqueur du tournoi " + currentTournament.getName() + " est "
                    + winner.getFirstName() + " " + winner.getCurrentLastName() + " !");
            System.out.println("====================================================");
        }
    }

    private List<Player> createPlayers() {
        List<Player> players = new ArrayList<>();
        String csvFile = "/app/players.csv";

        try (InputStream is = MainConsole.class.getResourceAsStream(csvFile)) {
            if (is == null) {
                System.err.println("Impossible de trouver la ressource: " + csvFile);
                return players;
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                br.readLine(); // skip header
                String line;
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",", -1);

                    String currentLastName = values[0];
                    String birthName = values[1];
                    String firstName = values[2];
                    String nickname = values[3];
                    String nationality = values[4];
                    int height = Integer.parseInt(values[5]);
                    int weight = Integer.parseInt(values[6]);
                    String handedness = values[7];
                    String sponsor = values[8];
                    int rank = Integer.parseInt(values[9]);
                    boolean isFemale = Boolean.parseBoolean(values[10]);
                    PlayStyle playStyle = PlayStyle.valueOf(values[11]);

                    players.add(new Player(
                            currentLastName, birthName, firstName, nickname, nationality,
                            height, weight, handedness, sponsor, rank, isFemale, playStyle
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return players;
    }

    public static void main(String[] args) {
        new MainConsole().run();
    }
}
