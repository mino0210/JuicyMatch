package cardGame.game.ui;

import cardGame.game.components.WoodButton;

import javax.swing.*;
import java.awt.*;

/**
 * 좌우 플레이어 패널과 매칭된 카드 표시 영역을 구성합니다.
 * Builds side panels that show matched cards for each player.
 */
public class MatchedScorePanel extends GameImagePanel {
    private final JPanel matchedCardsArea;
    private WoodButton bonusButton;

    public MatchedScorePanel(String playerName, boolean isUser, int panelW, int panelH, Runnable bonusAction) {
        super("/ingame_sorce_panel.png");
        setLayout(null);

        matchedCardsArea = new JPanel(new GridLayout(0, 2, 5, 5));
        matchedCardsArea.setOpaque(false);
        matchedCardsArea.setBounds(22, 90, 156, isUser ? 495 : 575);
        add(matchedCardsArea);

        if (isUser) {
            bonusButton = new WoodButton("BONUS", 156, 50);
            bonusButton.setBounds(22, panelH - 78, 156, 50);
            bonusButton.addActionListener(e -> bonusAction.run());
            add(bonusButton);
        }

        putClientProperty("playerName", playerName);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(new Font("Yu Gothic UI", Font.BOLD, 26));
        String playerName = String.valueOf(getClientProperty("playerName"));
        GameTextPainter.drawCenteredOutlinedText(
                g2,
                playerName,
                getWidth() / 2,
                62,
                new Color(255, 219, 64),
                new Color(70, 35, 8)
        );
        g2.dispose();
    }

    public JPanel getMatchedCardsArea() {
        return matchedCardsArea;
    }

    public WoodButton getBonusButton() {
        return bonusButton;
    }
}
