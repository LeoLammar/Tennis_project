package model;
import java.time.LocalDate;

public abstract class Person {

    // Attribut
    private String birthLastName;
    private String currentLastName;
    private String firstName;
    private String nickname;
    private LocalDate dateOfBirth;
    private String nationality;
    private int height;
    private int weight;

    // Getter 
    public String getBirthLastName() {
        return birthLastName;
    }

    public String getCurrentLastName() {
        return currentLastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getNickname() {
        return nickname;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getNationality() {
        return nationality;
    }

    public int getHeight() {
        return height;
    }

    public int getWeight() {
        return weight;
    }

    // Setter

    public void setBirthLastName(String birthLastName) {
        this.birthLastName = birthLastName;
    }

    public void setCurrentLastName(String currentLastName) {
        this.currentLastName = currentLastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }   

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    // Methode

    public int CalculateAge() {
        return LocalDate.now().getYear() - dateOfBirth.getYear();
    }

    // Constructor

    public Person(String birthLastName, String currentLastName, String firstName, String nickname, String nationality,
            int height, int weight) {
                this.birthLastName = birthLastName;
                this.currentLastName = currentLastName;
                this.firstName = firstName;
                this.nickname = nickname;
                this.nationality = nationality;
                this.height = height;
                this.weight = weight;
    }

    // Display

    @Override
    public String toString() {
        return "Person [birthLastName=" + birthLastName + ", currentLastName=" + currentLastName + ", firstName="
                + firstName + ", nickname=" + nickname + ", dateOfBirth=" + dateOfBirth + ", nationality=" + nationality
                + ", height=" + height + ", weight=" + weight + "]";
    }
}
