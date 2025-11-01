package model;

public class Score {
    private int setWined;
    private int gameWined;
    private int exchangeWined;

    // Getter   

    public int getSetWined() {
        return setWined;
    }

    public int getGameWined() {
        return gameWined;
    }

    public int getExchangeWined() {
        return exchangeWined;
    }

    // Setter

    public void setSetWined(int setWined) {
        this.setWined = setWined;
    }

    public void setGameWined(int gameWined) {
        this.gameWined = gameWined;
    }

    public void setExchangeWined(int exchangeWined) {
        this.exchangeWined = exchangeWined;
    }


    // Constructor

    public Score(int setWined, int gameWined, int exchangeWined) {
        this.setWined = setWined;
        this.gameWined = gameWined;
        this.exchangeWined = exchangeWined;
    }

    // Display

    @Override
    public String toString() {
        return "Score [setWined=" + setWined + ", gameWined=" + gameWined + ", exchangeWined=" + exchangeWined + "]";
    }
}
