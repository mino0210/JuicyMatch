package cardGame.game.ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

/**
 * 랭킹 목록 전용 스크롤바 모양을 정의합니다.
 * Defines the custom scrollbar style for the ranking list.
 */
public class RankingScrollBarUI extends BasicScrollBarUI {
    @Override
    protected void configureScrollBarColors() {
        thumbColor = new Color(230, 240, 255, 170);
        trackColor = new Color(255, 255, 255, 35);
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroButton();
    }

    private JButton createZeroButton() {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(0, 0));
        button.setMinimumSize(new Dimension(0, 0));
        button.setMaximumSize(new Dimension(0, 0));
        return button;
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(trackColor);
        g2.fillRoundRect(trackBounds.x + 4, trackBounds.y, trackBounds.width - 8, trackBounds.height, 10, 10);
        g2.dispose();
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (!scrollbar.isEnabled() || thumbBounds.width <= 0 || thumbBounds.height <= 0) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(thumbColor);
        g2.fillRoundRect(thumbBounds.x + 3, thumbBounds.y, thumbBounds.width - 6, thumbBounds.height, 10, 10);
        g2.dispose();
    }
}
