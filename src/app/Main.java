package app;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import model.*;
import services.TournamentService;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import javafx.concurrent.Task;
import java.util.Collections;

public class Main extends Application {

    // Variables pour gérer l'état du tournoi interactif
    private Tournament currentTournament;
    private List<Player> currentRoundPlayers;
    private List<Match> currentRoundMatches;
    private List<Player> nextRoundPlayers;
    private List<List<Match>> allRoundsMatches; // Pour garder l'historique de l'arbre
    private int roundNumber;
    private boolean isTournamentOver;
    private List<String> grandSlamChronologicalOrder;
    private int currentTournamentIndex;
    private Referee[] referees;
    private TextArea outputArea;
    private Label statusLabel;
    private Button nextMatchButton;
    private GridPane tournamentBracketPane; // Nouveau conteneur pour le tableau
    private HBox tournamentButtonBox;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Simulation de Match de Tennis");

        // Initialiser les arbitres une seule fois
        this.referees = createReferees();
        // Ordre chronologique des tournois du Grand Chelem
        this.grandSlamChronologicalOrder = List.of("Australian Open", "Roland Garros", "Wimbledon", "US Open");
        this.currentTournamentIndex = 0;


        // Création des composants de l'interface
        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setWrapText(true);
        // On force la hauteur de la zone de texte à être fixe en définissant ses tailles min, max et préférée.
        outputArea.setPrefHeight(250); 
        outputArea.setMinHeight(250);
        outputArea.setMaxHeight(250);

        // Redirection de la sortie console vers notre TextArea
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) {
                javafx.application.Platform.runLater(() -> outputArea.appendText(String.valueOf((char) b)));
            }
        };
        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(out, true));

        // Label pour afficher le statut du tournoi
        statusLabel = new Label("Veuillez sélectionner un tournoi pour commencer.");

        // Conteneur pour les boutons de tournoi
        tournamentButtonBox = new HBox(10);
        tournamentButtonBox.setAlignment(Pos.CENTER);

        // Bouton pour jouer le match suivant
        nextMatchButton = new Button("Jouer le match suivant");
        nextMatchButton.setDisable(true); // Désactivé au début

        // Bouton unique pour lancer toute la séquence du Grand Chelem
        Button startGrandSlamButton = new Button("Lancer le Grand Chelem (2025)");
        startGrandSlamButton.setOnAction(event -> {
            statusLabel.setText("Début du Grand Chelem !");
            startGrandSlamButton.setDisable(true); // On désactive le bouton principal
            startNextTournamentInSequence();
        });
        tournamentButtonBox.getChildren().add(startGrandSlamButton);

        nextMatchButton.setOnAction(event -> {
            playNextMatch();
            statusLabel.setText(currentTournament.getName() + " - Tour " + roundNumber);
        });

        // Initialisation du GridPane pour le tableau du tournoi
        tournamentBracketPane = new GridPane();
        tournamentBracketPane.setHgap(20); // Espace horizontal entre les tours
        tournamentBracketPane.setVgap(10); // Espace vertical entre les matchs
        tournamentBracketPane.setPadding(new Insets(10));

        // On encapsule le GridPane dans un ScrollPane pour gérer le dépassement
        ScrollPane bracketScrollPane = new ScrollPane(tournamentBracketPane);
        bracketScrollPane.setFitToWidth(true); // S'assure que le scrollpane prend la largeur disponible
        VBox.setVgrow(bracketScrollPane, Priority.ALWAYS); // On dit au ScrollPane de prendre tout l'espace vertical restant

        // Mise en page
        VBox root = new VBox(10, statusLabel, tournamentButtonBox, nextMatchButton, outputArea, bracketScrollPane);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
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
        Collections.shuffle(allPlayers); // Mélange les joueurs pour un tirage au sort aléatoire
        List<Player> tournamentPlayers = new ArrayList<>(allPlayers.subList(0, 128));

        int year = 2025;
        Tournament tournament = new Tournament(tournamentName, "Ville", "Surface", "Catégorie", year, tournamentPlayers.toArray(new Player[0]), this.referees);
        System.out.println("Tournoi '" + tournament.getName() + "' créé avec " + tournament.getPlayers().length + " joueurs.");
        return tournament;
    }

    private void startTournament(Tournament tournament) {
        outputArea.clear();
        tournamentBracketPane.getChildren().clear(); // On efface le tableau précédent
        this.currentTournament = tournament;
        this.roundNumber = 1;
        this.currentRoundPlayers = new ArrayList<>(List.of(tournament.getPlayers()));
        this.isTournamentOver = false; // On réinitialise le drapeau au début de chaque tournoi
        this.nextRoundPlayers = new ArrayList<>();
        this.allRoundsMatches = new ArrayList<>();

        TournamentService tournamentService = new TournamentService();
        this.currentRoundMatches = tournamentService.generateTournamentRound(this.currentRoundPlayers, tournament.getReferees());

        System.out.println("Début du tournoi : " + tournament.getName());
        tournamentButtonBox.getChildren().forEach(node -> node.setDisable(true));
        System.out.println("\n--- Tour " + roundNumber + " ---");
        allRoundsMatches.add(new ArrayList<>(currentRoundMatches)); // On stocke les matchs du 1er tour
        System.out.println("Matchs à jouer :");
        for (Match match : currentRoundMatches) {
            System.out.println(match.getPlayer1().getFirstName() + " vs " + match.getPlayer2().getFirstName());
        }
        updateTournamentBracket(); // On affiche l'état initial du tableau
        nextMatchButton.setDisable(false); // On active le bouton
    }

    private void playNextMatch() {
        // Si le tournoi précédent est terminé, ce clic lance le suivant.
        if (isTournamentOver) {
            startNextTournamentInSequence();
            return;
        }

        outputArea.clear(); // On efface le contenu de la zone de texte
        if (currentRoundMatches.isEmpty()) {
            // Le tour est terminé, on passe au suivant
            if (nextRoundPlayers.size() == 1) {
                // Fin du tournoi : on affiche le vainqueur et on attend le prochain clic.
                displayTournamentWinner(); // Affiche le vainqueur du tournoi qui vient de se finir
                isTournamentOver = true; // On positionne le drapeau
                nextMatchButton.setDisable(false); // On s'assure que le bouton est cliquable
                return;
            }

            // Préparation du tour suivant
            roundNumber++;
            currentRoundPlayers = new ArrayList<>(nextRoundPlayers);
            nextRoundPlayers.clear();
            Collections.shuffle(currentRoundPlayers);

            TournamentService tournamentService = new TournamentService();
            currentRoundMatches = tournamentService.generateTournamentRound(currentRoundPlayers, currentTournament.getReferees());
            allRoundsMatches.add(new ArrayList<>(currentRoundMatches)); // On stocke les matchs du nouveau tour
            
            System.out.println("\n--- Tour " + roundNumber + " ---");
            System.out.println("Matchs à jouer :");
            for (Match match : currentRoundMatches) {
                System.out.println(match.getPlayer1().getFirstName() + " vs " + match.getPlayer2().getFirstName());
            }
        }

        if (!currentRoundMatches.isEmpty()) {
            nextMatchButton.setDisable(true); // On désactive le bouton
            Match matchToPlay = currentRoundMatches.remove(0);

            // On utilise un Task pour exécuter la simulation en arrière-plan
            Task<Match> matchTask = new Task<>() {
                @Override
                protected Match call() throws Exception {
                    // La simulation du match se fait ici, en dehors du thread UI
                    return new TournamentService().playMatch(matchToPlay, 2);
                }
            };

            // Actions à effectuer une fois la tâche terminée (avec succès)
            matchTask.setOnSucceeded(e -> {
                Match playedMatch = matchTask.getValue();
                nextRoundPlayers.add(playedMatch.getWinner());

                String matchDescription = playedMatch.getPlayer1().getFirstName() + " vs " + playedMatch.getPlayer2().getFirstName();
                System.out.println("Résultat du match " + matchDescription + " : Vainqueur = " + playedMatch.getWinner().getFirstName());

                displayMatchStatistics(playedMatch); // On affiche les statistiques
                nextMatchButton.setDisable(false); // On réactive le bouton après chaque match
                
                updateTournamentBracket(); // On met à jour l'affichage du tableau
            });

            // On lance la tâche dans un nouveau thread
            new Thread(matchTask).start();
        }
    }

    private void startNextTournamentInSequence() {
        isTournamentOver = false; // On réinitialise le drapeau
        if (currentTournamentIndex < grandSlamChronologicalOrder.size()) {
            String tournamentName = grandSlamChronologicalOrder.get(currentTournamentIndex);
            currentTournamentIndex++;

            Tournament newTournament = createTournament(tournamentName);
            this.statusLabel.setText(newTournament.getName() + " - Tour 1");
            startTournament(newTournament);
        } else {
            // Tous les tournois du Grand Chelem sont terminés
            System.out.println("\n\n====================================================");
            System.out.println("La saison du Grand Chelem 2025 est terminée !");
            System.out.println("====================================================");
            nextMatchButton.setDisable(true);
            tournamentButtonBox.getChildren().get(0).setDisable(false); // Réactiver le bouton "Lancer le Grand Chelem"
            currentTournamentIndex = 0; // Réinitialiser pour la prochaine fois
        }
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
            System.out.println("Pourcentage de 1er service : " + String.format("%.2f", (double)stats.getFirstServes() / stats.getTotalPoints() * 100) + "%");
            System.out.println("Pourcentage de 2nd service : " + String.format("%.2f", (double)stats.getSecondServes() / stats.getTotalPoints() * 100) + "%");
            System.out.println("Vitesse moyenne du service : " + String.format("%.2f", (double)stats.getAvgServeSpeed()) + " km/h");
            System.out.println("Total des points joués : " + stats.getTotalPoints());
            System.out.println("-----------------------------\n");
        } else {
            System.out.println("Aucune statistique disponible pour ce match.");
        }
    }

    private void displayTournamentWinner() {
        // Affiche le vainqueur du tournoi dans la zone de texte
        if (nextRoundPlayers.size() == 1 && currentTournament != null) {
            Player winner = nextRoundPlayers.get(0);
            System.out.println("\n\n====================================================");
            System.out.println("Le vainqueur du tournoi " + currentTournament.getName() + " est " + winner.getFirstName() + " " + winner.getCurrentLastName() + " !");
            System.out.println("====================================================");
        }
    }

    private void updateTournamentBracket() {
        tournamentBracketPane.getChildren().clear(); // On efface tout pour redessiner proprement
    
        for (int round = 0; round < allRoundsMatches.size(); round++) {
            // Ajout du titre du tour
            Label roundTitle = new Label("Tour " + (round + 1));
            roundTitle.setStyle("-fx-font-weight: bold; -fx-underline: true;");
            tournamentBracketPane.add(roundTitle, round, 0);
    
            List<Match> roundMatches = allRoundsMatches.get(round);
            int rowIndex = 1;
            for (Match match : roundMatches) {
                Label player1Label = new Label(match.getPlayer1().getFirstName());
                Label player2Label = new Label(match.getPlayer2().getFirstName());
    
                if (match.getWinner() != null) {
                    if (match.getWinner().equals(match.getPlayer1())) {
                        player1Label.setStyle("-fx-font-weight: bold; -fx-text-fill: green;");
                        player2Label.setStyle("-fx-strikethrough: true;");
                    } else {
                        player2Label.setStyle("-fx-font-weight: bold; -fx-text-fill: green;");
                        player1Label.setStyle("-fx-strikethrough: true;");
                    }
                }
    
                VBox matchBox = new VBox(5, player1Label, player2Label);
                matchBox.setPadding(new Insets(5));
                matchBox.setStyle("-fx-border-color: lightgray; -fx-border-width: 1;");
                
                // La position verticale dépend de la taille des tours précédents
                // Pour simplifier, on augmente l'espacement à chaque tour
                int verticalPosition = rowIndex * (int)Math.pow(2, round);

                tournamentBracketPane.add(matchBox, round, verticalPosition);
                rowIndex++;
            }
        }
    }

    private List<Player> createPlayers() {
        List<Player> players = new ArrayList<>();
        String csvFile = "/app/players.csv"; // Chemin relatif dans les ressources

        try (InputStream is = Main.class.getResourceAsStream(csvFile);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            // Lire et ignorer l'en-tête
            br.readLine();

            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",", -1); // -1 pour inclure les champs vides
                
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
    
    
                players.add(new Player(currentLastName, birthName, firstName, nickname, nationality, height, weight, handedness, sponsor, rank, isFemale, playStyle));
                
            }
        } catch (Exception e) {
            e.printStackTrace();
            // En cas d'erreur, on retourne une liste vide pour éviter de planter
            return new ArrayList<>();
        }
         return players;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
