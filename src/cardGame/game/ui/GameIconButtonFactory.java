package cardGame.game.ui;

import cardGame.game.GameController;

import javax.swing.*;
import java.awt.*;

/**
 * 인게임 이미지 버튼 생성을 담당합니다.
 * Creates image-based buttons used in the in-game UI.
 */
public final class GameIconButtonFactory {
    private GameIconButtonFactory() {}

    public static JButton create(String path, int size) {
        JButton button = new JButton();
        setIcon(button, path, size);
        button.setPreferredSize(new Dimension(50, 50));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setRolloverEnabled(false);
        return button;
    }

    public static void setIcon(JButton button, String path, int size) {
        try {
            ImageIcon icon = new ImageIcon(GameIconButtonFactory.class.getResource(GameController.FrontImagePath + path));
            Image img = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(img));
            button.setText(null);
        } catch (Exception e) {
            button.setIcon(null);
            button.setText(path.replace("/", "").replace(".png", ""));
            System.err.println("이미지 로드 실패: " + path);
        }
    }
}
