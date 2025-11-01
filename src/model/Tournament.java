package model;

public class Tournament {
    private String name;
    private String city;
    private String surface;
    private String category;
    private int year;
    private Player players[];
    private Referee referees[];
    private Spectator spectators[];
    private Match matches[];

    // Getter

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getSurface() {
        return surface;
    }

    public String getCategory() {
        return category;
    }

    public int getYear() {
        return year;
    }

    public Player[] getPlayers() {
        return players;
    }

    public Referee[] getReferees() {
        return referees;
    }

    public Spectator[] getSpectators() {
        return spectators;
    }

    public Match[] getMatches() {
        return matches;
    }

    // Setter

    public void setName(String name) {
        this.name = name;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setSurface(String surface) {
        this.surface = surface;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setPlayers(Player[] players) {
        this.players = players;
    }

    public void setReferee(Referee[] referees) {
        this.referees = referees;
    }

    public void setSpectators(Spectator[] spectators) {
        this.spectators = spectators;
    }

    public void setMatches(Match[] matches) {
        this.matches = matches;
    }


    // Constructor
    public Tournament(String name, String city, String surface, String category, int year, Player[] players, Referee[] referees, Spectator[] spectators) {
        this.name = name;
        this.city = city;
        this.surface = surface;
        this.category = category;
        this.year = year;
        this.players = players;
        this.referees = referees;
        this.spectators = spectators;
    }

    // Display  

    @Override
    public String toString() {
        return "Tournament [name=" + name + ", city=" + city + ", surface=" + surface + ", year=" + year + ", players=" + players + ", referees=" + referees + ", spectators=" + spectators + ", matches=" + matches + "]";
    }

}
