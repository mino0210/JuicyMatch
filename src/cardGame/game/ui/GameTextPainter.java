package cardGame.game.ui;

import java.awt.*;

/**
 * 게임 UI에서 반복되는 외곽선 텍스트 렌더링을 담당합니다.
 * Renders reusable outlined text for game UI components.
 */
public final class GameTextPainter {
    private GameTextPainter() {}

    public static void drawCenteredOutlinedText(Graphics2D g2, String text, int centerX, int y,
                                                Color fill, Color outline) {
        FontMetrics fm = g2.getFontMetrics();
        int x = centerX - fm.stringWidth(text) / 2;
        drawOutlinedText(g2, text, x, y, fill, outline);
    }

    public static void drawOutlinedText(Graphics2D g2, String text, int x, int y,
                                        Color fill, Color outline) {
        g2.setColor(outline);
        g2.drawString(text, x - 2, y);
        g2.drawString(text, x + 2, y);
        g2.drawString(text, x, y - 2);
        g2.drawString(text, x, y + 2);
        g2.setColor(fill);
        g2.drawString(text, x, y);
    }
}
