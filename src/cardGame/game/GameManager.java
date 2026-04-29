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

    
    
    public void callInfo() {
        
        

        
        
        RecordDAO recordDAO = new RecordDAO();
        List<Record> loadedRecords = recordDAO.getAllRecords();

        
        
        if (loadedRecords != null) {
            for (Record r : loadedRecords) {
                recordMgr.addMList(r); 
            }
            System.out.println("[시스템] DB에서 " + loadedRecords.size() + "개의 기록을 로드했습니다.");
        }
    }


}
