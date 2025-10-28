package model;

public class Coach extends Person {

    private int experience;

    // Getter

    public int getExperience() {
        return experience;
    }

    // Setter

    public void setExperience(int experience) {
        this.experience = experience;
    }

    // Constructor

    public Coach(String birthLastName, String currentLastName, String firstName, String nickname, String nationality, int height, int weight, int experience) {
        super(birthLastName, currentLastName, firstName, nickname, nationality, height, weight);
        this.experience = experience;
    }


    // Display

    @Override
    public String toString() {
        return super.toString().replaceFirst("]$", ", experience=" + experience + "]");
    }
    
}
