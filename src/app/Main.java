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
        Tournament tournament = new Tournament(tournamentName, "Ville", "Surface", "Catégorie", year, tournamentPlayers.toArray(new Player[0]), this.referees, null);
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
        players.add(new Player("Djokovic", "Djokovic", "Novak", "Nole", "Serbe", 188, 77, "Droite", "Lacoste", 1, new Coach("Vajda", "Marián", "Marián", null, "Slovaque", 180, 80, 25), "Vert", false));
        players.add(new Player("Nadal", "Nadal", "Rafael", "Rafa", "Espagnol", 185, 85, "Gauche", "Nike", 2, new Coach("Parera", "Toni", "Toni", null, "Espagnol", 180, 80, 30), "Bleu", false));
        players.add(new Player("Alcaraz", "Alcaraz", "Carlos", "Carlitos", "Espagnol", 183, 74, "Droite", "Nike", 3, new Coach("Ferrero", "Juan Carlos", "Juan Carlos", null, "Espagnol", 180, 80, 15), "Jaune", false));
        players.add(new Player("Medvedev", "Medvedev", "Daniil", "Meddy", "Russe", 198, 83, "Droite", "Lacoste", 4, new Coach("Cervara", "Gilles", "Gilles", null, "Francais", 180, 80, 12), "Blanc", false));
        players.add(new Player("Zverev", "Zverev", "Alexander", "Sascha", "Allemand", 198, 90, "Droite", "Adidas", 5, new Coach("Ferrer", "David", "David", null, "Espagnol", 180, 80, 8), "Noir", false));
        players.add(new Player("Sinner", "Sinner", "Jannik", "Jan", "Italien", 188, 76, "Droite", "Nike", 6, new Coach("Vagnozzi", "Simone", "Simone", null, "Italien", 180, 80, 10), "Orange", false));
        players.add(new Player("Rublev", "Rublev", "Andrey", "Rublo", "Russe", 188, 75, "Droite", "Nike", 7, new Coach("Vicente", "Fernando", "Fernando", null, "Espagnol", 180, 80, 9), "Rouge", false));
        players.add(new Player("Ruud", "Ruud", "Casper", "Cappe", "Norvegien", 183, 77, "Droite", "Yonex", 8, new Coach("Ruud", "Christian", "Christian", null, "Norvegien", 180, 80, 20), "Gris", false));
        players.add(new Player("Tsitsipas", "Tsitsipas", "Stefanos", "Stef", "Grec", 193, 89, "Droite", "Adidas", 9, new Coach("Mouratoglou", "Patrick", "Patrick", null, "Francais", 180, 80, 22), "Violet", false));
        players.add(new Player("Federer", "Federer", "Roger", "Rodgeur", "Suisse", 185, 85, "Droite", "Uniqlo", 10, new Coach("Luthi", "Severin", "Severin", null, "Suisse", 180, 80, 20), "Blanc", false));
        players.add(new Player("Fritz", "Fritz", "Taylor", "Fritzy", "Americain", 196, 86, "Droite", "Nike", 11, new Coach("Russell", "Michael", "Michael", null, "Americain", 180, 80, 7), "Bleu Marine", false));
        players.add(new Player("Auger-Aliassime", "Auger-Aliassime", "Felix", "FAA", "Canadien", 193, 88, "Droite", "Adidas", 12, new Coach("Fontang", "Frederic", "Frederic", null, "Francais", 180, 80, 18), "Bordeaux", false));
        players.add(new Player("Norrie", "Norrie", "Cameron", "Cam", "Britannique", 188, 82, "Gauche", "K-Swiss", 13, new Coach("Lugones", "Facundo", "Facundo", null, "Argentin", 180, 80, 6), "Vert Citron", false));
        players.add(new Player("Hurkacz", "Hurkacz", "Hubert", "Hubi", "Polonais", 196, 81, "Droite", "Yonex", 14, new Coach("Craig", "Boynton", "Boynton", null, "Americain", 180, 80, 11), "Cyan", false));
        players.add(new Player("Tiafoe", "Tiafoe", "Frances", "Big Foe", "Americain", 188, 86, "Droite", "Nike", 15, new Coach("Ferreira", "Wayne", "Wayne", null, "Sud-Africain", 180, 80, 14), "Multicolore", false));
        players.add(new Player("Carreno Busta", "Carreno Busta", "Pablo", "PCB", "Espagnol", 188, 78, "Droite", "Joma", 16, new Coach("Cris", "Josep", "Josep", null, "Espagnol", 180, 80, 13), "Jaune Fluo", false));
        players.add(new Player("Shapovalov", "Shapovalov", "Denis", "Shapo", "Canadien", 185, 75, "Gauche", "Nike", 17, new Coach("Youzhny", "Mikhail", "Mikhail", null, "Russe", 180, 80, 5), "Rose", false));
        players.add(new Player("Khachanov", "Khachanov", "Karen", "Kachan", "Russe", 198, 88, "Droite", "Nike", 18, new Coach("Vedran", "Martic", "Martic", null, "Croate", 180, 80, 10), "Argent", false));
        players.add(new Player("De Minaur", "De Minaur", "Alex", "Demon", "Australien", 183, 69, "Droite", "Asics", 19, new Coach("Hewitt", "Lleyton", "Lleyton", null, "Australien", 180, 80, 16), "Vert", false));
        players.add(new Player("Dimitrov", "Dimitrov", "Grigor", "Grisha", "Bulgare", 191, 81, "Droite", "Nike", 20, new Coach("Grozdanov", "Dante", "Dante", null, "Bulgare", 180, 80, 9), "Or", false));
        players.add(new Player("Cilic", "Cilic", "Marin", "Cila", "Croate", 198, 89, "Droite", "Fila", 21, new Coach("Ivanisevic", "Goran", "Goran", null, "Croate", 180, 80, 19), "Blanc", false));
        players.add(new Player("Bautista Agut", "Bautista Agut", "Roberto", "RBA", "Espagnol", 183, 75, "Droite", "Lacoste", 22, new Coach("Bruguera", "Sergi", "Sergi", null, "Espagnol", 180, 80, 17), "Rouge", false));
        players.add(new Player("Korda", "Korda", "Sebastian", "Sebi", "Americain", 196, 82, "Droite", "Nike", 23, new Coach("Stepanek", "Radek", "Radek", null, "Tcheque", 180, 80, 6), "Bleu Ciel", false));
        players.add(new Player("Garin", "Garin", "Cristian", "Gago", "Chilien", 185, 80, "Droite", "Head", 24, new Coach("Franco", "Davin", "Davin", null, "Argentin", 180, 80, 11), "Marron", false));
        players.add(new Player("Opelka", "Opelka", "Reilly", "Opey", "Americain", 211, 102, "Droite", "Fila", 25, new Coach("Delgado", "Jay", "Jay", null, "Americain", 180, 80, 8), "Rayures", false));
        players.add(new Player("Isner", "Isner", "John", "Big John", "Americain", 208, 108, "Droite", "Fila", 26, new Coach("Sell", "David", "David", null, "Americain", 180, 80, 15), "Etoiles", false));
        players.add(new Player("Schwartzman", "Schwartzman", "Diego", "Peque", "Argentin", 170, 64, "Droite", "Fila", 27, new Coach("Chela", "Juan Ignacio", "Juan Ignacio", null, "Argentin", 180, 80, 12), "Orange", false));
        players.add(new Player("Monfils", "Monfils", "Gael", "La Monf", "Francais", 193, 85, "Droite", "Asics", 28, new Coach("Tillstrom", "Mikael", "Mikael", null, "Suedois", 180, 80, 10), "Noir", false));
        players.add(new Player("Brooksby", "Brooksby", "Jenson", "JTB", "Americain", 193, 84, "Droite", "Lotto", 29, new Coach("Joseph", "Gilbert", "Gilbert", null, "Americain", 180, 80, 7), "Vert", false));
        players.add(new Player("Van de Zandschulp", "Van de Zandschulp", "Botic", "Botic", "Hollandais", 191, 85, "Droite", "Diadora", 30, new Coach("Sluiter", "Raemon", "Raemon", null, "Hollandais", 180, 80, 9), "Bleu", false));
        players.add(new Player("Ramos-Vinolas", "Ramos-Vinolas", "Albert", "Ramos", "Espagnol", 188, 80, "Gauche", "Joma", 31, new Coach("Diaz", "Jose", "Jose", null, "Espagnol", 180, 80, 14), "Rouge", false));
        players.add(new Player("Davidovich Fokina", "Davidovich Fokina", "Alejandro", "Foki", "Espagnol", 183, 79, "Droite", "Diadora", 32, new Coach("Jorge", "Aguirre", "Aguirre", null, "Espagnol", 180, 80, 8), "Jaune", false));
        players.add(new Player("Humbert", "Humbert", "Ugo", "Ugo", "Francais", 188, 73, "Gauche", "Lacoste", 33, new Coach("Ascione", "Thierry", "Thierry", null, "Francais", 180, 80, 6), "Blanc", false));
        players.add(new Player("Kecmanovic", "Kecmanovic", "Miomir", "Misha", "Serbe", 183, 75, "Droite", "Nike", 34, new Coach("Nalbandian", "David", "David", null, "Argentin", 180, 80, 5), "Vert", false));
        players.add(new Player("Lehecka", "Lehecka", "Jiri", "Jirka", "Tcheque", 185, 80, "Droite", "Nike", 35, new Coach("Navratil", "Michal", "Michal", null, "Tcheque", 180, 80, 11), "Bleu", false));
        players.add(new Player("Molcan", "Molcan", "Alex", "Molly", "Slovaque", 180, 75, "Gauche", "Adidas", 36, new Coach("Vajda", "Marian", "Marian", null, "Slovaque", 180, 80, 26), "Gris", false));
        players.add(new Player("Giron", "Giron", "Marcos", "Giron", "Americain", 180, 77, "Droite", "Yonex", 37, new Coach("Joyce", "Michael", "Michael", null, "Americain", 180, 80, 9), "Noir", false));
        players.add(new Player("Cressy", "Cressy", "Maxime", "Cressy", "Americain", 198, 84, "Droite", "Fila", 38, new Coach("Gimelstob", "Justin", "Justin", null, "Americain", 180, 80, 7), "Blanc", false));
        players.add(new Player("Bonzi", "Bonzi", "Benjamin", "Bonzi", "Francais", 185, 78, "Droite", "Lacoste", 39, new Coach("Potin", "Lionel", "Lionel", null, "Francais", 180, 80, 10), "Bleu", false));
        players.add(new Player("Rinderknech", "Rinderknech", "Arthur", "Rinder", "Francais", 196, 86, "Droite", "Le Coq Sportif", 40, new Coach("Lisnard", "Jean-Rene", "Jean-Rene", null, "Francais", 180, 80, 12), "Rouge", false));
        players.add(new Player("Griekspoor", "Griekspoor", "Tallon", "Tallon", "Hollandais", 188, 82, "Droite", "The Indian Maharadja", 41, new Coach("Raemon", "Sluiter", "Sluiter", null, "Hollandais", 180, 80, 8), "Orange", false));
        players.add(new Player("Martinez", "Martinez", "Pedro", "Pedro", "Espagnol", 185, 77, "Droite", "Joma", 42, new Coach("Navarro", "Israel", "Israel", null, "Espagnol", 180, 80, 11), "Jaune", false));
        players.add(new Player("Lajovic", "Lajovic", "Dusan", "Dutzee", "Serbe", 183, 78, "Droite", "Asics", 43, new Coach("Zimonjic", "Nenad", "Nenad", null, "Serbe", 180, 80, 6), "Blanc", false));
        players.add(new Player("Nakashima", "Nakashima", "Brandon", "Brandon", "Americain", 185, 77, "Droite", "Fila", 44, new Coach("Cash", "Pat", "Pat", null, "Australien", 180, 80, 13), "Bleu", false));
        players.add(new Player("Baez", "Baez", "Sebastian", "Seba", "Argentin", 170, 66, "Droite", "Topper", 45, new Coach("Gaudio", "Gaston", "Gaston", null, "Argentin", 180, 80, 9), "Celeste", false));
        players.add(new Player("Mannarino", "Mannarino", "Adrian", "Manna", "Francais", 180, 75, "Gauche", "Hydrogen", 46, new Coach("Recouderc", "Laurent", "Laurent", null, "Francais", 180, 80, 15), "Noir", false));
        players.add(new Player("Fucsovics", "Fucsovics", "Marton", "Fucso", "Hongrois", 188, 82, "Droite", "Hydrogen", 47, new Coach("Nagy", "Zoltan", "Zoltan", null, "Hongrois", 180, 80, 10), "Vert", false));
        players.add(new Player("Djere", "Djere", "Laslo", "Laci", "Serbe", 188, 82, "Droite", "Le Coq Sportif", 48, new Coach("Troicki", "Viktor", "Viktor", null, "Serbe", 180, 80, 7), "Rouge", false));
        players.add(new Player("Coria", "Coria", "Federico", "Fede", "Argentin", 180, 76, "Droite", "Lotto", 49, new Coach("Coria", "Guillermo", "Guillermo", null, "Argentin", 180, 80, 12), "Bleu", false));
        players.add(new Player("McDonald", "McDonald", "Mackenzie", "Mackie", "Americain", 178, 73, "Droite", "Fila", 50, new Coach("Martin", "Todd", "Todd", null, "Americaain", 180, 80, 14), "Blanc", false));
        players.add(new Player("Munar", "Munar", "Jaume", "Jaume", "Espagnol", 183, 75, "Droite", "Lotto", 51, new Coach("Salva", "Tomeu", "Tomeu", null, "Espagnol", 180, 80, 8), "Jaune", false));
        players.add(new Player("Gaston", "Gaston", "Hugo", "Gaston", "Francais", 173, 68, "Gauche", "Artengo", 52, new Coach("Grosjean", "Sebastien", "Sebastien", null, "Francais", 180, 80, 16), "Bleu", false));
        players.add(new Player("Ymer", "Ymer", "Mikael", "Mikael", "Suedois", 183, 75, "Droite", "Wilson", 53, new Coach("Hellberg", "Daniel", "Daniel", null, "Suedois", 180, 80, 9), "Jaune", false));
        players.add(new Player("Halys", "Halys", "Quentin", "Quentin", "Francais", 191, 85, "Droite", "Wilson", 54, new Coach("Marx", "Nicolas", "Nicolas", null, "Francais", 180, 80, 11), "Noir", false));
        players.add(new Player("Popyrin", "Popyrin", "Alexei", "Pop", "Australien", 196, 78, "Droite", "Hydrogen", 55, new Coach("Masur", "Wally", "Wally", null, "Australien", 180, 80, 13), "Vert", false));
        players.add(new Player("Vesely", "Vesely", "Jiri", "Jiri", "Tcheque", 198, 92, "Gauche", "Wilson", 56, new Coach("Navratil", "Jaroslav", "Jaroslav", null, "Tcheque", 180, 80, 18), "Bleu", false));
        players.add(new Player("Gojowczyk", "Gojowczyk", "Peter", "GoJo", "Allemand", 188, 82, "Droite", "Babolat", 57, new Coach("Warneke", "Lars", "Lars", null, "Allemand", 180, 80, 7), "Blanc", false));
        players.add(new Player("Moutet", "Moutet", "Corentin", "Moutet", "Francais", 175, 70, "Gauche", "Hydrogen", 58, new Coach("Drouet", "Laurent", "Laurent", null, "Francais", 180, 80, 10), "Noir", false));
        players.add(new Player("Krajinovic", "Krajinovic", "Filip", "Kraj", "Serbe", 185, 75, "Droite", "Head", 59, new Coach("Popovic", "Janko", "Janko", null, "Serbe", 180, 80, 8), "Rouge", false));
        players.add(new Player("Paul", "Paul", "Tommy", "Tommy", "Americain", 185, 82, "Droite", "New Balance", 60, new Coach("Perez-Roldan", "Diego", "Diego", null, "Argentin", 180, 80, 12), "Gris", false));
        players.add(new Player("Thompson", "Thompson", "Jordan", "Thommo", "Australien", 183, 77, "Droite", "New Balance", 61, new Coach("Cash", "Jason", "Jason", null, "Australien", 180, 80, 9), "Vert", false));
        players.add(new Player("Kwon", "Kwon", "Soon-woo", "Kwon", "Sud-Coreen", 180, 73, "Droite", "Fila", 62, new Coach("Yoo", "Jin-sun", "Jin-sun", null, "Sud-Coreen", 180, 80, 14), "Bleu", false));
        players.add(new Player("Ivashka", "Ivashka", "Ilya", "Ilya", "Bielorusse", 193, 84, "Droite", "Asics", 63, new Coach("Mirnyi", "Max", "Max", null, "Bielorusse", 180, 80, 17), "Blanc", false));
        players.add(new Player("Otte", "Otte", "Oscar", "Otte", "Allemand", 193, 86, "Droite", "Diadora", 64, new Coach("Gerlach", "Jan", "Jan", null, "Allemand", 180, 80, 6), "Noir", false));
        players.add(new Player("Altmaier", "Altmaier", "Daniel", "Altmaier", "Allemand", 188, 80, "Droite", "Wilson", 65, new Coach("Puetz", "Tim", "Tim", null, "Allemand", 180, 80, 10), "Jaune", false));
        players.add(new Player("Kokkinakis", "Kokkinakis", "Thanasi", "Kokk", "Australien", 193, 84, "Droite", "Nike", 66, new Coach("Philippoussis", "Mark", "Mark", null, "Australien", 180, 80, 11), "Vert", false));
        players.add(new Player("Gasquet", "Gasquet", "Richard", "Richie", "Francais", 185, 75, "Droite", "Le Coq Sportif", 67, new Coach("Benneteau", "Julien", "Julien", null, "Francais", 180, 80, 19), "Bleu", false));
        players.add(new Player("Murray", "Murray", "Andy", "Muzza", "Britannique", 191, 84, "Droite", "Castore", 68, new Coach("Lendl", "Ivan", "Ivan", null, "Americain", 180, 80, 25), "Blanc", false));
        players.add(new Player("Wawrinka", "Wawrinka", "Stan", "Stan The Man", "Suisse", 183, 81, "Droite", "Yonex", 69, new Coach("Norman", "Magnus", "Magnus", null, "Suedois", 180, 80, 21), "Noir", false));
        players.add(new Player("Thiem", "Thiem", "Dominic", "Domi", "Autrichien", 185, 79, "Droite", "Adidas", 70, new Coach("Massu", "Nicolas", "Nicolas", null, "Chilien", 180, 80, 13), "Rouge", false));
        players.add(new Player("Nishikori", "Nishikori", "Kei", "Kei", "Japonais", 178, 75, "Droite", "Uniqlo", 71, new Coach("Chang", "Michael", "Michael", null, "Americain", 180, 80, 18), "Blanc", false));
        players.add(new Player("Raonic", "Raonic", "Milos", "Milos", "Canadien", 196, 98, "Droite", "New Balance", 72, new Coach("Piatti", "Riccardo", "Riccardo", null, "Italien", 180, 80, 20), "Noir", false));
        players.add(new Player("Paire", "Paire", "Benoit", "La Tige", "Francais", 196, 80, "Droite", "Lacoste", 73, new Coach("Grosjean", "Sebastien", "Sebastien", null, "Francais", 180, 80, 15), "Rose", false));
        players.add(new Player("Verdasco", "Verdasco", "Fernando", "Fer", "Espagnol", 188, 85, "Gauche", "Adidas", 74, new Coach("Fraile", "Diego", "Diego", null, "Espagnol", 180, 80, 14), "Vert", false));
        players.add(new Player("Lopez", "Lopez", "Feliciano", "Feli", "Espagnol", 188, 85, "Gauche", "Joma", 75, new Coach("Vicente", "Fernando", "Fernando", null, "Espagnol", 180, 80, 17), "Jaune", false));
        players.add(new Player("Simon", "Simon", "Gilles", "Gillou", "Francais", 183, 70, "Droite", "Asics", 76, new Coach("Tulasne", "Jean-Philippe", "Jean-Philippe", null, "Francais", 180, 80, 22), "Bleu", false));
        players.add(new Player("Tsonga", "Tsonga", "Jo-Wilfried", "Jo", "Francais", 188, 91, "Droite", "Artengo", 77, new Coach("Escude", "Nicolas", "Nicolas", null, "Francais", 180, 80, 16), "Noir", false));
        players.add(new Player("Chardy", "Chardy", "Jeremy", "Jez", "Francais", 188, 75, "Droite", "Lacoste", 78, new Coach("Clement", "Arnaud", "Arnaud", null, "Francais", 180, 80, 13), "Blanc", false));
        players.add(new Player("Pouille", "Pouille", "Lucas", "La Pouille", "Francais", 185, 84, "Droite", "Adidas", 79, new Coach("Llodra", "Michael", "Michael", null, "Francais", 180, 80, 11), "Bleu", false));
        players.add(new Player("Herbert", "Herbert", "Pierre-Hugues", "P2H", "Francais", 180, 75, "Droite", "Lacoste", 80, new Coach("Santoro", "Fabrice", "Fabrice", null, "Francais", 180, 80, 18), "Rouge", false));
        players.add(new Player("Andujar", "Andujar", "Pablo", "Andu", "Espagnol", 180, 76, "Droite", "Kelme", 81, new Coach("Navarro", "David", "David", null, "Espagnol", 180, 80, 15), "Orange", false));
        players.add(new Player("Sousa", "Sousa", "Joao", "Sousa", "Portugais", 185, 77, "Droite", "Lotto", 82, new Coach("Marques", "Frederico", "Frederico", null, "Portugais", 180, 80, 12), "Vert", false));
        players.add(new Player("Bedene", "Bedene", "Aljaz", "Aljaz", "Slovene", 180, 76, "Droite", "Erke", 83, new Coach("Cagnina", "Alberto", "Alberto", null, "Italien", 180, 80, 10), "Blanc", false));
        players.add(new Player("Cuevas", "Cuevas", "Pablo", "Cuevas", "Uruguayen", 180, 80, "Droite", "Asics", 84, new Coach("Coria", "Facundo", "Facundo", null, "Argentin", 180, 80, 14), "Celeste", false));
        players.add(new Player("Cecchinato", "Cecchinato", "Marco", "Ceck", "Italien", 185, 78, "Droite", "Asics", 85, new Coach("Sartori", "Massimo", "Massimo", null, "Italien", 180, 80, 11), "Bleu", false));
        players.add(new Player("Berrettini", "Berrettini", "Matteo", "Mat", "Italien", 196, 95, "Droite", "Lotto", 86, new Coach("Santopadre", "Vincenzo", "Vincenzo", null, "Italien", 180, 80, 13), "Noir", false));
        players.add(new Player("Fognini", "Fognini", "Fabio", "Fogna", "Italien", 178, 74, "Droite", "Hydrogen", 87, new Coach("Barazzutti", "Corrado", "Corrado", null, "Italien", 180, 80, 19), "Multicolore", false));
        players.add(new Player("Sonego", "Sonego", "Lorenzo", "Sonny", "Italien", 191, 80, "Droite", "Kappa", 88, new Coach("Arbino", "Gipo", "Gipo", null, "Italien", 180, 80, 16), "Bleu", false));
        players.add(new Player("Musetti", "Musetti", "Lorenzo", "Muso", "Italien", 185, 75, "Droite", "Nike", 89, new Coach("Tartarini", "Simone", "Simone", null, "Italien", 180, 80, 9), "Blanc", false));
        players.add(new Player("Travaglia", "Travaglia", "Stefano", "Steto", "Italien", 185, 80, "Droite", "Hydrogen", 90, new Coach("Fanucci", "Simone", "Simone", null, "Italien", 180, 80, 8), "Rouge", false));
        players.add(new Player("Seppi", "Seppi", "Andreas", "Andi", "Italien", 191, 78, "Droite", "Fila", 91, new Coach("Sartori", "Massimo", "Massimo", null, "Italien", 180, 80, 23), "Bleu", false));
        players.add(new Player("Mager", "Mager", "Gianluca", "Mager", "Italien", 188, 80, "Droite", "Legea", 92, new Coach("Galimberti", "Giorgio", "Giorgio", null, "Italien", 180, 80, 7), "Vert", false));
        players.add(new Player("Caruso", "Caruso", "Salvatore", "Salvo", "Italien", 185, 80, "Droite", "Legea", 93, new Coach("Vico", "Uros", "Uros", null, "Italien", 180, 80, 10), "Blanc", false));
        players.add(new Player("Gombos", "Gombos", "Norbert", "Noro", "Slovaque", 191, 85, "Droite", "Mizuno", 94, new Coach("Hrbaty", "Dominik", "Dominik", null, "Slovaque", 180, 80, 12), "Bleu", false));
        players.add(new Player("Martin", "Martin", "Andrej", "Andrej", "Slovaque", 185, 78, "Droite", "Mizuno", 95, new Coach("Mertiňák", "Michal", "Michal", null, "Slovaque", 180, 80, 11), "Rouge", false));
        players.add(new Player("Kovalik", "Kovalik", "Jozef", "Jozef", "Slovaque", 183, 75, "Droite", "Mizuno", 96, new Coach("Kucera", "Karol", "Karol", null, "Slovaque", 180, 80, 15), "Blanc", false));
        players.add(new Player("Bagnis", "Bagnis", "Facundo", "Facu", "Argentin", 183, 80, "Gauche", "Topper", 97, new Coach("Jaite", "Martin", "Martin", null, "Argentin", 180, 80, 13), "Celeste", false));
        players.add(new Player("Delbonis", "Delbonis", "Federico", "Delbo", "Argentin", 193, 90, "Gauche", "Topper", 98, new Coach("Gomez", "Gustavo", "Gustavo", null, "Argentin", 180, 80, 14), "Bleu", false));
        players.add(new Player("Londero", "Londero", "Juan Ignacio", "Topo", "Argentin", 180, 75, "Droite", "Topper", 99, new Coach("Hood", "Mariano", "Mariano", null, "Argentin", 180, 80, 10), "Blanc", false));
        players.add(new Player("Mayer", "Mayer", "Leonardo", "Leo", "Argentin", 188, 80, "Droite", "Nike", 100, new Coach("Dabul", "Brian", "Brian", null, "Argentin", 180, 80, 9), "Celeste", false));
        players.add(new Player("Pella", "Pella", "Guido", "Guido", "Argentin", 183, 78, "Gauche", "Fila", 101, new Coach("Rodriguez", "Jose Luis", "Jose Luis", null, "Argentin", 180, 80, 16), "Bleu", false));
        players.add(new Player("Struff", "Struff", "Jan-Lennard", "Struffi", "Allemand", 193, 92, "Droite", "Diadora", 102, new Coach("Waske", "Alexander", "Alexander", null, "Allemand", 180, 80, 11), "Noir", false));
        players.add(new Player("Koepfer", "Koepfer", "Dominik", "Koepfer", "Allemand", 180, 79, "Gauche", "Hydrogen", 103, new Coach("Smith", "Peter", "Peter", null, "Australien", 180, 80, 8), "Jaune", false));
        players.add(new Player("Hanfmann", "Hanfmann", "Yannick", "Hanfmann", "Allemand", 193, 86, "Droite", "Solinco", 104, new Coach("Wolters", "Lars", "Lars", null, "Allemand", 180, 80, 7), "Blanc", false));
        players.add(new Player("Marterer", "Marterer", "Maximilian", "Maxi", "Allemand", 191, 85, "Gauche", "Babolat", 105, new Coach("Weiss", "Markus", "Markus", null, "Allemand", 180, 80, 10), "Rouge", false));
        players.add(new Player("Gerasimov", "Gerasimov", "Egor", "Egor", "Bielorusse", 196, 85, "Droite", "Joma", 106, new Coach("Voltchkov", "Vladimir", "Vladimir", null, "Bielorusse", 180, 80, 12), "Bleu", false));
        players.add(new Player("Berankis", "Berankis", "Ricardas", "Rico", "Lituanien", 175, 70, "Droite", "Joma", 107, new Coach("Sabeckis", "Remigijus", "Remigijus", null, "Lituanien", 180, 80, 14), "Vert", false));
        players.add(new Player("Kukushkin", "Kukushkin", "Mikhail", "Kukushkin", "Kazakh", 183, 72, "Droite", "Asics", 108, new Coach("Kukushkina", "Anastasia", "Anastasia", null, "Kazakh", 180, 80, 18), "Blanc", false));
        players.add(new Player("Bublik", "Bublik", "Alexander", "Sasha", "Kazakh", 196, 82, "Droite", "Yonex", 109, new Coach("Kunitsyn", "Igor", "Igor", null, "Russe", 180, 80, 9), "Noir", false));
        players.add(new Player("Donskoy", "Donskoy", "Evgeny", "Donskoy", "Russe", 185, 78, "Droite", "Asics", 110, new Coach("Prodon", "Eric", "Eric", null, "Francais", 180, 80, 11), "Bleu", false));
        players.add(new Player("Karlovic", "Karlovic", "Ivo", "Dr. Ivo", "Croate", 211, 104, "Droite", "Head", 111, new Coach("Rasberger", "Petar", "Petar", null, "Croate", 180, 80, 15), "Blanc", false));
        players.add(new Player("Bolelli", "Bolelli", "Simone", "Bole", "Italien", 183, 78, "Droite", "Nike", 112, new Coach("Rianna", "Umberto", "Umberto", null, "Italien", 180, 80, 13), "Bleu", false));
        players.add(new Player("Lorenzi", "Lorenzi", "Paolo", "Paolino", "Italien", 183, 78, "Droite", "Legea", 113, new Coach("Galimberti", "Giorgio", "Giorgio", null, "Italien", 180, 80, 17), "Rouge", false));
        players.add(new Player("Istomin", "Istomin", "Denis", "Denis", "Ouzbek", 188, 88, "Droite", "Tecnifibre", 114, new Coach("Istomina", "Klaudiya", "Klaudiya", null, "Ouzbek", 180, 80, 20), "Bleu", false));
        players.add(new Player("Dzumhur", "Dzumhur", "Damir", "Dzum", "Bosnien", 175, 70, "Droite", "Adidas", 115, new Coach("Dzumhur", "Nerfid", "Nerfid", null, "Bosnien", 180, 80, 19), "Blanc", false));
        players.add(new Player("Basic", "Basic", "Mirza", "Mirza", "Bosnien", 188, 80, "Droite", "Erke", 116, new Coach("Gorcic", "Ismar", "Ismar", null, "Bosnien", 180, 80, 10), "Noir", false));
        players.add(new Player("Albot", "Albot", "Radu", "Radu", "Moldave", 175, 70, "Droite", "Lotto", 117, new Coach("Ciumac", "Andrei", "Andrei", null, "Moldave", 180, 80, 12), "Bleu", false));
        players.add(new Player("Copil", "Copil", "Marius", "Marius", "Roumain", 193, 85, "Droite", "Wilson", 118, new Coach("Pavel", "Andrei", "Andrei", null, "Roumain", 180, 80, 14), "Jaune", false));
        players.add(new Player("Haase", "Haase", "Robin", "Robin", "Hollandais", 191, 80, "Droite", "Babolat", 119, new Coach("Bogaert", "Raymond", "Raymond", null, "Hollandais", 180, 80, 16), "Orange", false));
        players.add(new Player("Klizan", "Klizan", "Martin", "Klizan", "Slovaque", 191, 85, "Gauche", "Wilson", 120, new Coach("Hrbaty", "Dominik", "Dominik", null, "Slovaque", 180, 80, 15), "Bleu", false));
        players.add(new Player("Lacko", "Lacko", "Lukas", "Lukas", "Slovaque", 185, 78, "Droite", "Babolat", 121, new Coach("Mertiňák", "Michal", "Michal", null, "Slovaque", 180, 80, 13), "Blanc", false));
        players.add(new Player("Soeda", "Soeda", "Go", "Go", "Japonais", 178, 70, "Droite", "Yonex", 122, new Coach("Suzuki", "Takahiro", "Takahiro", null, "Japonais", 180, 80, 17), "Rouge", false));
        players.add(new Player("Uchiyama", "Uchiyama", "Yasutaka", "Yasu", "Japonais", 183, 75, "Droite", "Wilson", 123, new Coach("Matsui", "Toshihide", "Toshihide", null, "Japonais", 180, 80, 11), "Bleu", false));
        players.add(new Player("Sugita", "Sugita", "Yuichi", "Sugi", "Japonais", 173, 68, "Droite", "Babolat", 124, new Coach("Sakai", "Toshiro", "Toshiro", null, "Japonais", 180, 80, 19), "Blanc", false));
        players.add(new Player("Daniel", "Daniel", "Taro", "Taro", "Japonais", 191, 76, "Droite", "Tecnifibre", 125, new Coach("Nevolo", "Dennis", "Dennis", null, "Americain", 180, 80, 9), "Noir", false));
        players.add(new Player("Duckworth", "Duckworth", "James", "Ducky", "Australien", 183, 82, "Droite", "Asics", 126, new Coach("Larkham", "Brent", "Brent", null, "Australien", 180, 80, 12), "Vert", false));
        players.add(new Player("Millman", "Millman", "John", "Milly", "Australien", 183, 79, "Droite", "Asics", 127, new Coach("Corrie", "Edward", "Edward", null, "Britannique", 180, 80, 8), "Jaune", false));
        players.add(new Player("Ebden", "Ebden", "Matthew", "Matt", "Australien", 188, 84, "Droite", "Asics", 128, new Coach("Papac", "Peter", "Peter", null, "Australien", 180, 80, 14), "Bleu", false));
        return players;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
