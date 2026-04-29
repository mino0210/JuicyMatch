package cardGame.mgr;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class Manager {
    public ArrayList<Manageable> mList = new ArrayList<>();

    public void printAll(){
        for (Manageable m : mList) {
            m.print();
        }
    }

    public void readAll(String filename, Factory fac){
        Scanner filein = openFile(filename);
        Manageable m = null;

        while(filein.hasNext()){
            m = fac.create(filein);
            m.read(filein);
            mList.add(m);
        }
        filein.close();
    }

    public Scanner openFile(String filename) {








        Scanner filein = null;
        try {
            
            
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filename);
            if (inputStream == null) {
                throw new IOException("파일을 찾을 수 없습니다: " + filename);
            }
            filein = new Scanner(inputStream);
        } catch (IOException e) {
            System.out.println("파일 입력 오류: " + e.getMessage());
            System.exit(0);
        }
        return filein;
    }

    public void addMList(Manageable m){
        mList.add(m);
    }
    
    

    public Manageable findManageable(String kwd){
        for (Manageable m : mList) {
            if(m.matches(kwd)){
                return m;
            }
        }
        return null;
    }

}
