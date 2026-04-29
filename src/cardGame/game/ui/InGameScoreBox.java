package cardGame.game.ui;

import javax.swing.*;
import java.awt.*;

/**
 * 플레이어 현재 점수를 표시하는 우측 상단 점수 박스입니다.
 * Top-right score box that displays the player's current score.
 */
public class InGameScoreBox extends GameImagePanel {
    private final JLabel scoreLabel;

    public InGameScoreBox() {
        super("/ingame_score.png");
        setLayout(null);

        scoreLabel = new JLabel("スコア:0", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Yu Gothic UI", Font.BOLD, 23));
        scoreLabel.setForeground(new Color(255, 250, 240));
        scoreLabel.setBounds(0, 0, 250, 70);
        add(scoreLabel);
    }

    public JLabel getScoreLabel() {
        return scoreLabel;
    }
}
