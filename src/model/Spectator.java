package model;

public class Spectator extends Person{

    // Atribut

    private int seatNumber;
    private int ticketPrice;
    private boolean isFemale;
    private String shirtColor;
    private boolean wearsGlasses;

    // Getter

    public int getSeatNumber(){
        return seatNumber;
    }

    public int getTicketPrice(){
        return ticketPrice;
    }

    public boolean getIsFemale(){
        return isFemale;
    }

    public String getShirtColor(){
        return shirtColor;
    }

    public boolean getWearsGlasses(){
        return wearsGlasses;
    }

    // Setter

    public void setSeatNumber(int seatNumber){
        this.seatNumber = seatNumber;
    }

    public void setTicketPrice(int ticketPrice){
        this.ticketPrice = ticketPrice;
    }

    public void setIsFemale(boolean isFemale){
        this.isFemale = isFemale;
    }

    public void setShirtColor(String shirtColor){
        this.shirtColor = shirtColor;
    }

    public void setWearsGlasses(boolean wearsGlasses){
        this.wearsGlasses = wearsGlasses;
    }   

    // Constructor

    public Spectator(String birthLastName, String currentLastName, String firstName, String nickname, String nationality, int height, int weight, int seatNumber, int ticketPrice, boolean isFemale, String shirtColor, boolean wearsGlasses){
        super(birthLastName, currentLastName, firstName, nickname, nationality, height, weight);
        this.seatNumber = seatNumber;
        this.ticketPrice = ticketPrice;
        this.isFemale = isFemale;
        this.shirtColor = shirtColor;
        this.wearsGlasses = wearsGlasses;
    }

    // Methode

    public void applaud(){
        System.out.println("clap clap clap !");
    }

    public void boo(){
        System.out.println("You're bad !");
    }

    public void shout(){
        System.out.println("You can do it !");
    }

    public void sleep(){
        System.out.println("this person is sleeping");
    }


    // Display

    @Override
    public String toString() {
        return super.toString().replaceFirst("]$", ", seatNumber=" + seatNumber + ", ticketPrice=" + ticketPrice + ", isFemale=" + isFemale + ", shirtColor=" + shirtColor + ", wearsGlasses=" + wearsGlasses + "]");   
    }

}
