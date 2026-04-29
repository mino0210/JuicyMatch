package cardGame.game.panels;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

import static cardGame.game.GameController.*;


public class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    public BackgroundPanel(String imagePath) {
        this.backgroundImage = new ImageIcon(Objects.requireNonNull(getClass().getResource(MenuScreenPath))).getImage();


    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // 이미지를 패널 크기에 맞게 그리기
        // Draw image to fit panel size
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}
