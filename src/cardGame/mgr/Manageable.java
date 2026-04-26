package cardGame.mgr;

import java.util.Scanner;

public interface Manageable {
    public void read(Scanner scan);
    public void print();
    public boolean matches(String kwd);
    public void saveToFile(String filePath);
}
