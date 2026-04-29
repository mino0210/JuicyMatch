package cardGame.game.ui;

import cardGame.game.GameController;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 리소스 이미지를 배경으로 그리는 공통 패널입니다.
 * Common panel that paints an image resource as its background.
 */
public class GameImagePanel extends JPanel {
    private BufferedImage image;

    public GameImagePanel(String imagePath) {
        setOpaque(false);
        loadImage(imagePath);
    }

    private void loadImage(String imagePath) {
        try {
            java.net.URL url = getClass().getResource(GameController.FrontImagePath + imagePath);
            if (url != null) {
                image = ImageIO.read(url);
            }
        } catch (Exception e) {
            System.err.println("이미지 패널 로드 실패: " + imagePath + " / " + e.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image == null) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        g2.dispose();
    }
}
