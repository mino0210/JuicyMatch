package cardGame.entity;

public class Player {
    private String name;
    private int score;
    private int count; 

    public Player(String name) {
        this.name = name;
        this.score = 0;
        this.count = 1;
    }
    public Player(){
        this.score = 0;
        this.count = 1;
    }

    public void setUsername(String username){
        this.name = username;
    }

    public int getCount(){
        return count;
    }

    public void resetCount(){
        count = 1;
    }
    public void resetScore() {
        this.score = 0;
        this.count = 1;
    }
    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public void incrementCount(){
        count++;
    }

    public void incrementScore(int number) {
        score+=number;
    }

    public void addScore(int score) {
        this.score += score;
    }

}
