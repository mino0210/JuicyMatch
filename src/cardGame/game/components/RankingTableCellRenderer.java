package cardGame.game.components;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * ランキングテーブル用カスタムセルレンダラー
 * 1~3位にバッジ色を適用、ログインユーザー行をハイライト
 */
public class RankingTableCellRenderer extends DefaultTableCellRenderer {
    
    private static final Color GOLD = new Color(255, 215, 0);
    private static final Color SILVER = new Color(192, 192, 192);
    private static final Color BRONZE = new Color(205, 127, 50);
    private static final Color LOGGED_IN_BG = new Color(255, 243, 224); // オレンジ系薄い背景
    private static final Color LOGGED_IN_BORDER = new Color(255, 143, 0); // メイプルオレンジ
    private static final Color DEFAULT_BG = new Color(255, 255, 255);
    private static final Color ALTERNATE_BG = new Color(250, 250, 245);
    
    private String loggedInUserId;
    private int rankColumnIndex = 0;  // 順位列のインデックス
    private int userIdColumnIndex = 1; // ユーザーID列のインデックス
    
    public RankingTableCellRenderer(String loggedInUserId) {
        this.loggedInUserId = loggedInUserId;
        setHorizontalAlignment(SwingConstants.CENTER);
    }
    
    public void setRankColumnIndex(int index) {
        this.rankColumnIndex = index;
    }
    
    public void setUserIdColumnIndex(int index) {
        this.userIdColumnIndex = index;
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        // デフォルトフォント設定
        Font defaultFont = new Font("Meiryo", Font.BOLD, 14);
        c.setFont(defaultFont);
        
        // テーブルからユーザーIDと順位を取得
        String userId = (String) table.getValueAt(row, userIdColumnIndex);
        int rank = row + 1; // 行番号 + 1 = 順位
        
        // ログインユーザーの行かチェック
        boolean isLoggedInUser = loggedInUserId != null && userId != null && userId.equals(loggedInUserId);
        
        if (isSelected) {
            // 選択時の色
            c.setBackground(new Color(255, 200, 150));
            c.setForeground(Color.BLACK);
        } else if (isLoggedInUser) {
            // ログインユーザーの行をハイライト
            c.setBackground(LOGGED_IN_BG);
            c.setForeground(Color.BLACK);
            setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, LOGGED_IN_BORDER));
            Font boldFont = new Font("Meiryo", Font.BOLD, 14);
            c.setFont(boldFont);
        } else {
            // 通常行（交互色）
            c.setBackground(row % 2 == 0 ? DEFAULT_BG : ALTERNATE_BG);
            c.setForeground(Color.BLACK);
            setBorder(noFocusBorder);
        }
        
        // 順位列に色を適用
        if (column == rankColumnIndex) {
            Font rankFont = new Font("Meiryo", Font.BOLD, 16);
            c.setFont(rankFont);
            
            switch (rank) {
                case 1:
                    c.setForeground(GOLD.darker());
                    setText("🥇 " + rank);
                    break;
                case 2:
                    c.setForeground(SILVER.darker());
                    setText("🥈 " + rank);
                    break;
                case 3:
                    c.setForeground(BRONZE.darker());
                    setText("🥉 " + rank);
                    break;
                default:
                    c.setForeground(Color.BLACK);
                    break;
            }
        }
        
        return c;
    }
}
