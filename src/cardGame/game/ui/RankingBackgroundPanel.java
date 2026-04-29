package cardGame.game.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * 랭킹 보드 배경 이미지를 그리는 전용 패널입니다.
 * Dedicated panel for painting the ranking board background.
 */
public class RankingBackgroundPanel extends JPanel {
    private BufferedImage rankingPanelImage;

    public RankingBackgroundPanel() {
        setOpaque(false);
        setLayout(null);
        try {
            rankingPanelImage = ImageIO.read(new File("src/cardGame/img/ranking_panel.png"));
        } catch (IOException e) {
            System.err.println("랭킹 패널 이미지 로드 실패: " + e.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (rankingPanelImage != null) {
            g2.drawImage(rankingPanelImage, 0, 0, getWidth(), getHeight(), null);
        } else {
            GradientPaint gp = new GradientPaint(
                    0, 0, new Color(115, 63, 25, 235),
                    getWidth(), getHeight(), new Color(85, 42, 15, 235)
            );
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 35, 35);
            g2.setColor(new Color(255, 201, 44));
            g2.setStroke(new BasicStroke(6));
            g2.drawRoundRect(4, 4, getWidth() - 8, getHeight() - 8, 35, 35);
        }
        g2.dispose();
    }
}
