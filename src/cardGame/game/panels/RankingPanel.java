package cardGame.game.panels;

import cardGame.entity.User;
import cardGame.game.GameController;
import cardGame.game.Sound;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static cardGame.game.GameController.*;

public class RankingPanel extends JPanel{
    private GameController gameController;
    private User loginedUser;
    private Sound sound = new Sound();

    public RankingPanel(GameController gameController, User loginedUser) {
        this.gameController = gameController;
        this.loginedUser = loginedUser;
    }

    public JPanel showRanking() {
        gameController.setTitle("기록");

        JPanel panel = new JPanel(new BorderLayout());
        TablePanel tablePane = TablePanel.GetInstance();
        tablePane.initUI();
        gameController.getContentPane().add(tablePane);

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        backButton.setBackground(new Color(238, 238, 238));
        backButton.setForeground(new Color(255, 115, 0));

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sound.Sound(SoundPath +"/BtnClick.wav", false, -10.0f);
                gameController.switchToPanel("gameMenu",loginedUser);
            }
        });

        panel.add(tablePane, BorderLayout.CENTER);
        panel.add(backButton, BorderLayout.SOUTH);

        return panel;
    }
}
