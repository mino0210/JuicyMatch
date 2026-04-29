package cardGame.game.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * 折りたたみ可能なランキングアイテムパネル
 * クリックで詳細表示をトグル
 */
public class ExpandableRankPanel extends JPanel {
    
    private boolean isExpanded = false;
    private JPanel headerPanel;
    private JPanel detailPanel;
    private JLabel expandIcon;
    
    private static final Color MAPLE_ORANGE = new Color(255, 143, 0);
    private static final Color CREAM_BG = new Color(255, 248, 230);
    private static final Color HEADER_BG = new Color(255, 235, 205);
    
    public ExpandableRankPanel(String rankText, String userIdText, String scoreText) {
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);
        setBorder(new EmptyBorder(5, 10, 5, 10));
        
        // ヘッダーパネル（常に表示）
        headerPanel = createHeaderPanel(rankText, userIdText, scoreText);
        
        // 詳細パネル（折りたたみ可能）
        detailPanel = createDetailPanel();
        detailPanel.setVisible(false);
        
        add(headerPanel, BorderLayout.NORTH);
        add(detailPanel, BorderLayout.CENTER);
        
        // クリックイベント
        headerPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleExpand();
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                headerPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
        });
    }
    
    private JPanel createHeaderPanel(String rank, String userId, String score) {
        JPanel panel = new JPanel(new BorderLayout(10, 5)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // 背景
                g2.setColor(HEADER_BG);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                
                // 枠線
                g2.setColor(MAPLE_ORANGE);
                g2.setStroke(new BasicStroke(2f));
                g2.draw(new RoundRectangle2D.Double(1, 1, getWidth() - 2, getHeight() - 2, 10, 10));
                
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));
        panel.setPreferredSize(new Dimension(0, 60));
        
        // 順位ラベル
        JLabel rankLabel = new JLabel(rank);
        rankLabel.setFont(new Font("Meiryo", Font.BOLD, 18));
        rankLabel.setForeground(MAPLE_ORANGE);
        
        // ユーザーIDラベル
        JLabel userLabel = new JLabel(userId);
        userLabel.setFont(new Font("Meiryo", Font.BOLD, 16));
        
        // スコアラベル
        JLabel scoreLabel = new JLabel(score + " pt");
        scoreLabel.setFont(new Font("Meiryo", Font.BOLD, 15));
        scoreLabel.setForeground(new Color(100, 100, 100));
        
        // 展開アイコン
        expandIcon = new JLabel("▼");
        expandIcon.setFont(new Font("SansSerif", Font.BOLD, 12));
        expandIcon.setForeground(MAPLE_ORANGE);
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(rankLabel);
        leftPanel.add(userLabel);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(scoreLabel);
        rightPanel.add(expandIcon);
        
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createDetailPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 30, 10, 30));
        
        // 詳細情報を追加（例）
        JLabel detailLabel = new JLabel("詳細情報がここに表示されます");
        detailLabel.setFont(new Font("Meiryo", Font.BOLD, 13));
        detailLabel.setForeground(new Color(80, 80, 80));
        
        panel.add(detailLabel);
        panel.add(Box.createVerticalStrut(5));
        
        return panel;
    }
    
    /**
     * 詳細パネルにコンポーネントを追加
     */
    public void addDetailComponent(Component comp) {
        detailPanel.add(comp);
    }
    
    /**
     * 展開/折りたたみトグル
     */
    public void toggleExpand() {
        isExpanded = !isExpanded;
        detailPanel.setVisible(isExpanded);
        expandIcon.setText(isExpanded ? "▲" : "▼");
        
        // 親コンテナの再描画
        revalidate();
        repaint();
        
        Container parent = getParent();
        if (parent != null) {
            parent.revalidate();
            parent.repaint();
        }
    }
    
    public boolean isExpanded() {
        return isExpanded;
    }
}
