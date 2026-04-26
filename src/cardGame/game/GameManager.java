package cardGame.game;


import cardGame.database.RecordDAO;
import cardGame.entity.Record;
import cardGame.entity.*;
import cardGame.mgr.*;

import java.util.List;
import java.util.Scanner;

import static cardGame.game.GameController.*;

public class GameManager {
    private Manager recordMgr;

    public GameManager(Manager recordMgr) {
        this.recordMgr = recordMgr;
    }

    public static User findUser(String username){
        User user;
        for(Manageable m : userMgr.mList){
            user = (User) m;
            if(user.matches(username)) return user;
        }
        return null;
    }

    // GameManager.java 의 callInfo 메서드 내부
    // GameManager.java
    public void callInfo() {
        // 1. 기존 파일 읽기 로직은 모두 주석 처리되어 있어야 합니다.

        // 2. DB에서 모든 기록 불러오기
        RecordDAO recordDAO = new RecordDAO();
        List<Record> loadedRecords = recordDAO.getAllRecords();

        // 3. 불러온 데이터를 Manager의 리스트(mList)에 담기
        if (loadedRecords != null) {
            for (Record r : loadedRecords) {
                recordMgr.addMList(r); // Manager 클래스의 addMList 메서드 사용
            }
            System.out.println("[시스템] DB에서 " + loadedRecords.size() + "개의 기록을 로드했습니다.");
        }
    }


}
