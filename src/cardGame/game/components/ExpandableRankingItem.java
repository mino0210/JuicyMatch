package cardGame.game.components;

import cardGame.database.RecordDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * 확장 가능한 랭킹 아이템 - 클릭 시 상세 정보 표시
 * Expandable ranking item - Shows detail on click
 */
public class ExpandableRankingItem extends JPanel {
    
    // 이미지
    // Images
    private BufferedImage collapsedBg;
    private BufferedImage expandedBg;
    
    // 상태
    // State
    private boolean isExpanded = false;
    private boolean isLoading = false;
    private boolean dataLoaded = false;
    
    // 데이터
    // Data
    private int rank;
    private String username;       // user_id (DB)
    private String nickname;       // 표시 이름
    private int totalScore;
    
    // 레벨별 통계
    // Level stats
    private int lv1Total, lv1Best, lv1Count;
    private int lv2Total, lv2Best, lv2Count;
    private int lv3Total, lv3Best, lv3Count;
    
    // 크기
    // Sizes
    private static final int COLLAPSED_HEIGHT = 120;
    private static final int EXPANDED_HEIGHT = 400;
    private static final int WIDTH = 1200;
    
    // 색상
    // Colors
    private static final Color TEXT_COLOR = new Color(255, 250, 240);
    private static final Color GOLD_COLOR = new Color(255, 215, 0);
    private static final Color RANK_1_COLOR = new Color(255, 215, 0);
    private static final Color RANK_2_COLOR = new Color(192, 192, 192);
    private static final Color RANK_3_COLOR = new Color(205, 127, 50);
    
    /**
     * 생성자 / Constructor
     * @param rank 순위
     * @param username 사용자 ID
     * @param nickname 표시명
     * @param totalScore 총점
     */
    public ExpandableRankingItem(int rank, String username, String nickname, int totalScore) {
        this.rank = rank;
        this.username = username;
        this.nickname = nickname;
        this.totalScore = totalScore;
        
        loadImages();
        setupPanel();
    }
    
    private void loadImages() {
        try {
            collapsedBg = ImageIO.read(new File("src/cardGame/img/ranking_item_collapsed.png"));
            expandedBg = ImageIO.read(new File("src/cardGame/img/ranking_item_expanded.png"));
        } catch (IOException e) {
            System.err.println("랭킹 아이템 이미지 로드 실패: " + e.getMessage());
        }
    }
    
    private void setupPanel() {
        setPreferredSize(new Dimension(WIDTH, COLLAPSED_HEIGHT));
        setMaximumSize(new Dimension(WIDTH, COLLAPSED_HEIGHT));
        setMinimumSize(new Dimension(WIDTH, COLLAPSED_HEIGHT));
        
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isLoading) return;
                toggleExpand();
            }
        });
    }
    
    /**
     * 확장/축소 토글 - 처음 확장 시 데이터 로드
     * Toggle expand/collapse - load data on first expand
     */
    private void toggleExpand() {
        if (!isExpanded && !dataLoaded) {
            // 처음 펼치는 경우 - 데이터 로드
            // Load data on first expand
            isLoading = true;
            loadLevelStats();
        }
        
        isExpanded = !isExpanded;
        animateResize();
    }
    
    /**
     * 레벨별 통계 로드 (RecordDAO.getUserLevelStats 사용)
     * Load level stats using actual RecordDAO method
     */
    private void loadLevelStats() {
        try {
            RecordDAO recordDAO = new RecordDAO();
            // Map<level, [totalScore, bestScore, playCount]>
            Map<Integer, int[]> stats = recordDAO.getUserLevelStats(username);
            
            int[] lv1 = stats.get(1);
            int[] lv2 = stats.get(2);
            int[] lv3 = stats.get(3);
            
            if (lv1 != null) {
                lv1Total = lv1[0];
                lv1Best = lv1[1];
                lv1Count = lv1[2];
            }
            if (lv2 != null) {
                lv2Total = lv2[0];
                lv2Best = lv2[1];
                lv2Count = lv2[2];
            }
            if (lv3 != null) {
                lv3Total = lv3[0];
                lv3Best = lv3[1];
                lv3Count = lv3[2];
            }
            
            dataLoaded = true;
        } catch (Exception e) {
            System.err.println("레벨 통계 로드 실패: " + e.getMessage());
        } finally {
            isLoading = false;
        }
    }
    
    private void animateResize() {
        int targetHeight = isExpanded ? EXPANDED_HEIGHT : COLLAPSED_HEIGHT;
        int currentHeight = getHeight();
        
        Timer timer = new Timer(20, null);
        timer.addActionListener(new ActionListener() {
            int frame = 0;
            int frames = 10;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                frame++;
                
                float progress = (float) frame / frames;
                progress = (float) (1 - Math.pow(1 - progress, 3));
                
                int newHeight = (int) (currentHeight + (targetHeight - currentHeight) * progress);
                
                setPreferredSize(new Dimension(WIDTH, newHeight));
                setMaximumSize(new Dimension(WIDTH, newHeight));
                setMinimumSize(new Dimension(WIDTH, newHeight));
                
                revalidate();
                repaint();
                
                Container parent = getParent();
                if (parent != null) {
                    parent.revalidate();
                    parent.repaint();
                }
                
                if (frame >= frames) {
                    timer.stop();
                }
            }
        });
        timer.start();
    }
    
    private Font createFont(int size, int style) {
        int resolvedStyle = style | Font.BOLD;
        String[] fontNames = {"Yu Gothic UI", "Meiryo", "MS Gothic", "Hiragino Sans"};
        for (String fontName : fontNames) {
            Font font = new Font(fontName, resolvedStyle, size);
            if (font.getFamily().equals(fontName)) {
                return font;
            }
        }
        return new Font("Dialog", resolvedStyle, size);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        
        // 배경 이미지
        // Background image
        if (isExpanded && expandedBg != null) {
            g2.drawImage(expandedBg, 0, 0, width, height, null);
        } else if (collapsedBg != null) {
            g2.drawImage(collapsedBg, 0, 0, width, COLLAPSED_HEIGHT, null);
        }
        
        // 기본 정보
        // Basic info
        drawBasicInfo(g2);
        
        // 상세 정보
        // Detail info
        if (isExpanded && dataLoaded) {
            drawDetailInfo(g2);
        }
        
        g2.dispose();
    }
    
    private void drawBasicInfo(Graphics2D g2) {
        // 순위 배지
        // Rank badge
        drawRankBadge(g2, 30, 40);
        
        // 순위 번호
        // Rank number
        g2.setFont(createFont(24, Font.BOLD));
        g2.setColor(TEXT_COLOR);
        g2.drawString(rank + "", 70, 70);
        
        // 사용자명
        // Username
        g2.setFont(createFont(28, Font.BOLD));
        String displayName = (nickname != null && !nickname.isEmpty()) ? nickname : username;
        g2.drawString(displayName, 150, 73);
        
        // 총점
        // Total score
        g2.setFont(createFont(32, Font.BOLD));
        g2.setColor(GOLD_COLOR);
        String scoreText = totalScore + " pt";
        FontMetrics fm = g2.getFontMetrics();
        int scoreWidth = fm.stringWidth(scoreText);
        g2.drawString(scoreText, getWidth() - scoreWidth - 100, 73);
        
        // 화살표
        // Arrow
        g2.setFont(createFont(24, Font.BOLD));
        g2.setColor(GOLD_COLOR);
        String arrow = isExpanded ? "▲" : "▼";
        g2.drawString(arrow, getWidth() - 50, 73);
    }
    
    private void drawDetailInfo(Graphics2D g2) {
        int startY = 160;
        int lineHeight = 80;
        
        drawLevelDetail(g2, startY, 1, lv1Total, lv1Best, lv1Count, GOLD_COLOR);
        drawLevelDetail(g2, startY + lineHeight, 2, lv2Total, lv2Best, lv2Count, new Color(192, 192, 192));
        drawLevelDetail(g2, startY + lineHeight * 2, 3, lv3Total, lv3Best, lv3Count, new Color(255, 140, 0));
    }
    
    private void drawLevelDetail(Graphics2D g2, int y, int level, int total, int best, int count, Color badgeColor) {
        drawLevelBadge(g2, 320, y - 25, level, badgeColor);
        
        g2.setFont(createFont(20, Font.BOLD));
        g2.setColor(TEXT_COLOR);
        
        g2.drawString("そうてん: " + total + " pt", 430, y);
        
        g2.setColor(GOLD_COLOR);
        g2.drawString("さいこうてん: " + best + " pt", 750, y);
        
        g2.setColor(TEXT_COLOR);
        g2.setFont(createFont(24, Font.BOLD));
        g2.drawString(count + "かい", 1080, y);
    }
    
    private void drawRankBadge(Graphics2D g2, int x, int y) {
        int size = 40;
        
        Color badgeColor;
        if (rank == 1) badgeColor = RANK_1_COLOR;
        else if (rank == 2) badgeColor = RANK_2_COLOR;
        else if (rank == 3) badgeColor = RANK_3_COLOR;
        else badgeColor = new Color(139, 111, 71);
        
        g2.setColor(badgeColor);
        g2.fillOval(x, y, size, size);
        
        g2.setColor(new Color(101, 67, 33));
        g2.setStroke(new BasicStroke(2));
        g2.drawOval(x, y, size, size);
    }
    
    private void drawLevelBadge(Graphics2D g2, int x, int y, int level, Color color) {
        int width = 80;
        int height = 40;
        
        g2.setColor(color);
        g2.fillRoundRect(x, y, width, height, 10, 10);
        
        g2.setColor(new Color(101, 67, 33));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(x, y, width, height, 10, 10);
        
        g2.setFont(createFont(18, Font.BOLD));
        g2.setColor(new Color(101, 67, 33));
        String text = "Lv." + level;
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textX = x + (width - textWidth) / 2;
        int textY = y + (height + fm.getAscent()) / 2 - 2;
        g2.drawString(text, textX, textY);
    }
}
