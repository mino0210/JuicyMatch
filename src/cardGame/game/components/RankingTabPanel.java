package cardGame.game.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * ランキング画面のタブ構造
 * 全体ランキング / マイレコード / ユーザー検索
 */
public class RankingTabPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private MapleButton allRankingBtn;
    private MapleButton myRecordBtn;
    private MapleButton userSearchBtn;
    
    private static final Color MAPLE_ORANGE = new Color(255, 143, 0);
    private static final Color INACTIVE_COLOR = new Color(200, 200, 200);
    
    public RankingTabPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        
        // タブボタンパネル
        JPanel tabButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        tabButtonPanel.setOpaque(false);
        
        allRankingBtn = new MapleButton("全体ランキング", 200, 50);
        myRecordBtn = new MapleButton("マイレコード", 200, 50);
        userSearchBtn = new MapleButton("ユーザー検索", 200, 50);
        
        // ボタンフォントサイズ調整
        Font tabFont = new Font("Meiryo", Font.BOLD, 16);
        allRankingBtn.setFont(tabFont);
        myRecordBtn.setFont(tabFont);
        userSearchBtn.setFont(tabFont);
        
        tabButtonPanel.add(allRankingBtn);
        tabButtonPanel.add(myRecordBtn);
        tabButtonPanel.add(userSearchBtn);
        
        // コンテンツパネル (CardLayout)
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false);
        
        // タブ切り替えアクション
        allRankingBtn.addActionListener(e -> {
            cardLayout.show(contentPanel, "ALL");
            updateTabButtonStates(allRankingBtn);
        });
        
        myRecordBtn.addActionListener(e -> {
            cardLayout.show(contentPanel, "MY");
            updateTabButtonStates(myRecordBtn);
        });
        
        userSearchBtn.addActionListener(e -> {
            cardLayout.show(contentPanel, "SEARCH");
            updateTabButtonStates(userSearchBtn);
        });
        
        add(tabButtonPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        
        // 初期状態: 全体ランキング選択
        updateTabButtonStates(allRankingBtn);
    }
    
    /**
     * タブボタンの状態更新
     */
    private void updateTabButtonStates(JButton activeButton) {
        // すべてのボタンを非アクティブ状態に
        allRankingBtn.setEnabled(true);
        myRecordBtn.setEnabled(true);
        userSearchBtn.setEnabled(true);
        
        // アクティブボタンを無効化（選択状態を示す）
        activeButton.setEnabled(false);
    }
    
    /**
     * タブにパネルを追加
     */
    public void addTab(String key, JPanel panel) {
        contentPanel.add(panel, key);
    }
    
    /**
     * 特定のタブを表示
     */
    public void showTab(String key) {
        cardLayout.show(contentPanel, key);
        
        // ボタン状態も更新
        switch (key) {
            case "ALL":
                updateTabButtonStates(allRankingBtn);
                break;
            case "MY":
                updateTabButtonStates(myRecordBtn);
                break;
            case "SEARCH":
                updateTabButtonStates(userSearchBtn);
                break;
        }
    }
    
    public JPanel getContentPanel() {
        return contentPanel;
    }
}
