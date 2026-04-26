package cardGame.entity;

import cardGame.mgr.Manageable;
import java.util.Scanner;

public class Record implements Manageable {
    private int recordId;
    private int score;
    private User user;
    private String playTime;
    private int level;

    public Record() {}

    // [추가] 1. GameController 등 기존 코드 호환용 (에러 해결의 핵심!)
    public Record(User user, int score) {
        this.user = user;
        this.score = score;
        this.level = 1; // 레벨이 안 넘어오면 기본값 1 저장
    }

    // 2. 레벨 정보까지 확실히 저장할 때 (GameWindow용)
    public Record(User user, int score, int level) {
        this.user = user;
        this.score = score;
        this.level = level;
    }

    // 3. DB에서 불러올 때 (RecordDAO용)
    public Record(int recordId, User user, int score, String playTime, int level) {
        this.recordId = recordId;
        this.user = user;
        this.score = score;
        this.playTime = playTime;
        this.level = level;
    }

    // Getter & Setter
    public int getRecordId() { return recordId; }
    public void setRecordId(int recordId) { this.recordId = recordId; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getPlayTime() { return playTime; }
    public void setPlayTime(String playTime) { this.playTime = playTime; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    @Override
    public void print() {
        System.out.printf("[%s] %s: %d (Lv.%d)\n", playTime, user.getName(), score, level);
    }

    @Override public void read(Scanner scan) {}
    @Override public void saveToFile(String filename) {}
    @Override
    public boolean matches(String kwd) {
        if (user != null) {
            return user.getUsername().contains(kwd) || user.getName().contains(kwd);
        }
        return false;
    }
}