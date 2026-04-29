package cardGame.game.panels;

import cardGame.database.RecordDAO;
import cardGame.entity.User;
import cardGame.game.GameController;
import cardGame.game.Sound;
import cardGame.game.components.ImagePanel;
import cardGame.game.components.TitleBoard;
import cardGame.game.components.WoodButton;
import cardGame.game.config.RankingLayout;
import cardGame.game.ui.RankingBackgroundPanel;
import cardGame.game.ui.RankingRowPanel;
import cardGame.game.ui.RankingScrollBarUI;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * DB 기록을 읽어 랭킹 목록과 개인 기록을 표시하는 화면입니다.
 * Screen that displays ranking and personal records from database records.
 */
public class RankingPanel extends JPanel {
    private final GameController gameController;
    private final User loginedUser;
    private final Sound sound = new Sound();

    private boolean showMyRecordOnly = false;
    private JPanel rankingListPanelRef;

    public RankingPanel(GameController gameController, User loginedUser) {
        this.gameController = gameController;
        this.loginedUser = loginedUser;
    }

    /**
     * 랭킹 화면 UI를 생성하고 탭/뒤로가기 버튼 이벤트를 연결합니다.
     * Builds the ranking UI and connects tab/back button events.
     */
    public JPanel showRanking() {
        ImagePanel panel = new ImagePanel("src/cardGame/img/background.png");
        panel.setPreferredSize(new Dimension(RankingLayout.SCREEN_W, RankingLayout.SCREEN_H));
        panel.setLayout(null);

        TitleBoard titleBoard = new TitleBoard("きろく");
        titleBoard.setBounds(RankingLayout.TITLE_X, RankingLayout.TITLE_Y, RankingLayout.TITLE_W, RankingLayout.TITLE_H);
        panel.add(titleBoard);

        RankingBackgroundPanel rankingBgPanel = new RankingBackgroundPanel();
        rankingBgPanel.setBounds(RankingLayout.PANEL_X, RankingLayout.PANEL_Y, RankingLayout.PANEL_W, RankingLayout.PANEL_H);
        panel.add(rankingBgPanel);

        JPanel rankingListPanel = createRankingListPanel();
        rankingListPanelRef = rankingListPanel;
        loadRankingItems(rankingListPanel);

        JScrollPane scrollPane = createRankingScrollPane(rankingListPanel);
        rankingBgPanel.add(scrollPane);

        WoodButton myRecordBtn = new WoodButton("マイレコード", 200, 60);
        myRecordBtn.setBounds(RankingLayout.PANEL_W - 230, 130, 200, 60);
        rankingBgPanel.add(myRecordBtn);
        rankingBgPanel.setComponentZOrder(myRecordBtn, 0);
        myRecordBtn.addActionListener(e -> toggleMyRecordFilter(myRecordBtn));

        WoodButton backBtn = new WoodButton("戻る");
        backBtn.setBounds(RankingLayout.BACK_BTN_X, RankingLayout.BACK_BTN_Y, RankingLayout.BACK_BTN_W, RankingLayout.BACK_BTN_H);
        panel.add(backBtn);
        backBtn.addActionListener(e -> {
            sound.play("BtnClick.wav", false, -10.0f);
            gameController.switchToPanel("gameMenu", loginedUser);
        });

        return panel;
    }

    private JPanel createRankingListPanel() {
        JPanel rankingListPanel = new JPanel();
        rankingListPanel.setLayout(new BoxLayout(rankingListPanel, BoxLayout.Y_AXIS));
        rankingListPanel.setOpaque(false);
        rankingListPanel.add(Box.createVerticalStrut(RankingLayout.TOP_GAP_IN_LIST));
        return rankingListPanel;
    }

    private JScrollPane createRankingScrollPane(JPanel rankingListPanel) {
        JScrollPane scrollPane = new JScrollPane(rankingListPanel);
        scrollPane.setBounds(RankingLayout.LIST_X, RankingLayout.LIST_Y, RankingLayout.LIST_W, RankingLayout.LIST_H);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getViewport().setScrollMode(JViewport.BLIT_SCROLL_MODE);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(28);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(18, 0));
        scrollPane.getVerticalScrollBar().setUI(new RankingScrollBarUI());
        return scrollPane;
    }

    /**
     * 내 기록 필터를 전환하고 랭킹 목록을 새로 그립니다.
     * Toggles the personal-record filter and redraws the ranking list.
     */
    private void toggleMyRecordFilter(WoodButton myRecordBtn) {
        sound.play("BtnClick.wav", false, -10.0f);
        showMyRecordOnly = !showMyRecordOnly;
        myRecordBtn.setButtonText(showMyRecordOnly ? "ぜんたい" : "マイレコード");
        refreshRankingList();
    }

    private void refreshRankingList() {
        if (rankingListPanelRef == null) {
            return;
        }
        rankingListPanelRef.removeAll();
        rankingListPanelRef.add(Box.createVerticalStrut(RankingLayout.TOP_GAP_IN_LIST));
        loadRankingItems(rankingListPanelRef);
        rankingListPanelRef.revalidate();
        rankingListPanelRef.repaint();
    }

    /**
     * 현재 필터 상태에 맞춰 랭킹 행 데이터를 다시 불러옵니다.
     * Reloads ranking row data based on the current filter state.
     */
    private void loadRankingItems(JPanel rankingListPanel) {
        try {
            RecordDAO recordDAO = new RecordDAO();
            List<Map<String, Object>> rankings = recordDAO.getGlobalRankings();

            if (rankings == null || rankings.isEmpty()) {
                addCenteredMessage(rankingListPanel, "きろくがありません。", 30);
                return;
            }

            String loginedUsername = (loginedUser != null) ? loginedUser.getUsername() : null;
            int rank = 1;
            RankingRowPanel myRow = null;

            for (Map<String, Object> rankData : rankings) {
                String username = safeString(rankData.get("username"));
                String nickname = safeString(rankData.get("nickname"));
                int totalSum = safeInt(rankData.get("totalSum"));
                String displayName = !nickname.isBlank() ? nickname : username;

                if (showMyRecordOnly && loginedUsername != null && !username.equals(loginedUsername)) {
                    rank++;
                    continue;
                }

                RankingRowPanel row = new RankingRowPanel(rank, username, displayName, totalSum, sound);
                row.setAlignmentX(Component.CENTER_ALIGNMENT);
                if (loginedUsername != null && username.equals(loginedUsername)) {
                    myRow = row;
                }
                rankingListPanel.add(row);
                rankingListPanel.add(Box.createVerticalStrut(RankingLayout.ROW_GAP));

                rank++;
                if (rank > 50) {
                    break;
                }
            }

            if (showMyRecordOnly && myRow != null) {
                RankingRowPanel finalMyRow = myRow;
                SwingUtilities.invokeLater(() -> finalMyRow.setExpandedState(true));
            }
        } catch (Exception e) {
            System.err.println("랭킹 데이터 로드 실패: " + e.getMessage());
            e.printStackTrace();
            addCenteredMessage(rankingListPanel, "データを読み込めませんでした。", 28);
        }
    }

    private void addCenteredMessage(JPanel rankingListPanel, String message, int fontSize) {
        JLabel label = new JLabel(message, SwingConstants.CENTER);
        label.setFont(createFont(fontSize, Font.BOLD));
        label.setForeground(new Color(255, 250, 240));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        rankingListPanel.add(Box.createVerticalStrut(220));
        rankingListPanel.add(label);
    }

    private Font createFont(int size, int style) {
        String[] fontNames = {"Yu Gothic UI", "Yu Gothic", "Meiryo", "MS Gothic", "Hiragino Sans"};
        for (String fontName : fontNames) {
            Font font = new Font(fontName, style | Font.BOLD, size);
            if (font.getFamily().equals(fontName)) {
                return font;
            }
        }
        return new Font("Dialog", style | Font.BOLD, size);
    }

    private String safeString(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private int safeInt(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
