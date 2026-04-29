package cardGame.entity;

import cardGame.mgr.Manageable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class User extends Player implements Manageable {
    private String username;
    private String password;
    private String nickname;
    private String gender;
    public ArrayList<Record> recordList = new ArrayList<>();
    private int maxScore;
    private int totalScore;

    
    
    private int score;

    public User() {};

    public User(String username, String password, String nickname, String gender) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.gender = gender;

        
        
        super.setUsername(username);
    }

    
    

    public void addScore(int amount) {
        this.score += amount;
    }

    public int getScore() {
        return this.score;
    }

    public int getTopScore() {
        return this.maxScore;
    }
    public void setTopScore(int score) {
        this.maxScore = score;
    }

    

    @Override
    public String getName() {
        return nickname;
    }

    public String getUsername() {
        return username;
    }
    
    
    
    public String getUserId() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }
    public String getGender() {
        return gender;
    }

    public void setName(String nickname) {
        this.nickname = nickname;
    }
    public void setUsername(String username) {
        this.username = username;
        super.setUsername(username); 
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }

    public void addRecord(Record record){
        recordList.add(record);
    }

    @Override
    public void read(Scanner scan) {
        username = scan.next();
        password = scan.next();
        nickname = scan.next();
        gender = scan.next();
        super.setUsername(username);
    }

    @Override
    public void print() {
        System.out.printf("%s %s\n", username, password);
    }

    public void printAllRecord(){
        for(Record record : recordList){
            System.out.print(" ");
            record.print();
        }
    }

    public void calculateScore(){
        int total = 0;
        int val =0;
        for(Record record : recordList){
            val = record.getScore();
            maxScore = Math.max(maxScore,val);
            total+= val;
        }
        totalScore = total;
    }

    @Override
    public boolean matches(String kwd) {
        return username.equals(kwd);
    }

    public boolean matchePw(String kwd){
        return password.equals(kwd);
    }

    @Override
    public void saveToFile(String filePath) {
        
        
        try{
            File file = new File(filePath);

            FileWriter fw = new FileWriter(file, true);
            fw.write(username + " " + password + " " + nickname + " " + gender + "\n");

            fw.flush();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public int getTotalScore() {
        calculateScore();
        return totalScore;
    }

    @Override
    public void resetScore() {
        this.score = 0;      
        maxScore = 0;
        totalScore = 0;
        super.resetScore();
        recordList.clear();
    }
}