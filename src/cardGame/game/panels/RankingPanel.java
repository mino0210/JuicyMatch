package cardGame.game.panels;

import cardGame.database.RecordDAO;
import cardGame.entity.User;
import cardGame.game.GameController;
import cardGame.game.Sound;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;     // 추가됨
import java.util.Map;      // 추가됨
import java.util.HashMap;  // 추가됨
import java.util.ArrayList; // 추가됨

public class RankingPanel extends JPanel {
    private GameController gameController;
    private User loginedUser;
    private Sound sound = new Sound();
    private RecordDAO recordDAO = new RecordDAO();

    public RankingPanel(GameController gameController, User loginedUser) {
        this.gameController = gameController;
        this.loginedUser = loginedUser;
        setLayout(new BorderLayout());
    }

    public JPanel showRanking() {
        gameController.setTitle("기록 대시보드");

        // 메인 패널 생성 및 배경색 설정
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // 1. 상단 요약 패널
        mainPanel.add(createSummaryPanel(), BorderLayout.NORTH);

        // 2. 중앙 리스트 영역
        JPanel listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setBackground(Color.WHITE);

        List<Map<String, Object>> allRankings = recordDAO.getGlobalRankings();
        int rank = 1;
        for (Map<String, Object> data : allRankings) {
            listContainer.add(new RankItemPanel(rank++, data));
            listContainer.add(Box.createVerticalStrut(5));
        }

        JScrollPane scrollPane = new JScrollPane(listContainer);
        scrollPane.setBorder(null);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // 3. 하단 버튼
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        backButton.addActionListener(e -> {
            sound.play("BtnClick.wav", false, -10.0f);
            gameController.switchToPanel("gameMenu", loginedUser);
        });
        mainPanel.add(backButton, BorderLayout.SOUTH);

        return mainPanel;
    }

    private JPanel createSummaryPanel() {
        Map<String, Object> summary = recordDAO.getDashboardSummary(loginedUser.getUsername());
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
        panel.setBackground(new Color(248, 248, 248));
        panel.setBorder(new EmptyBorder(25, 20, 25, 20));

        // 데이터가 null일 경우를 대비한 안전 처리
        int total = (summary.get("totalUsers") != null) ? (int)summary.get("totalUsers") : 0;
        int myTotal = (summary.get("myTotal") != null) ? (int)summary.get("myTotal") : 0;
        int myBest = (summary.get("myBest") != null) ? (int)summary.get("myBest") : 0;

        panel.add(createSummaryItem("전체 유저", total + "명", Color.BLACK));
        panel.add(createSummaryItem("내 순위", "RANKING", new Color(0, 102, 204)));
        panel.add(createSummaryItem("내 총점", myTotal + "점", Color.DARK_GRAY));
        panel.add(createSummaryItem("내 최고점", myBest + "점", new Color(184, 134, 11)));

        return panel;
    }

    private JPanel createSummaryItem(String title, String value, Color color) {
        JPanel p = new JPanel(new GridLayout(2, 1));
        p.setOpaque(false);
        JLabel t = new JLabel(title, SwingConstants.CENTER);
        JLabel v = new JLabel(value, SwingConstants.CENTER);
        v.setFont(new Font("맑은 고딕", Font.BOLD, 22));
        v.setForeground(color);
        p.add(t); p.add(v);
        return p;
    }

    // RankItemPanel 및 내부 메서드(createDetailRow 등)는 이전 코드와 동일하게 유지
    // RankingPanel.java 내부의 RankItemPanel 클래스 수정
    private class RankItemPanel extends JPanel {
        private JPanel detailPanel;
        private boolean isExpanded = false;
        private String targetUser;

        // 생성자 파라미터 2개(int rank, Map data)를 정확히 받도록 수정
        public RankItemPanel(int rank, Map<String, Object> data) {
            this.targetUser = (String) data.get("username");
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);

            // 헤더 구성 (순위, 닉네임, 총점 등)
            JPanel header = new JPanel(new GridLayout(1, 5));
            header.add(new JLabel(" " + rank));
            header.add(new JLabel((String) data.get("nickname")));
            header.add(new JLabel(data.get("totalSum").toString()));
            header.add(new JLabel(data.get("bestScore").toString()));
            header.add(new JLabel("▼"));

            // 상세 패널 숨김 설정
            detailPanel = new JPanel();
            detailPanel.setVisible(false);

            header.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mousePressed(java.awt.event.MouseEvent e) {
                    toggleDetail();
                }
            });

            add(header, BorderLayout.NORTH);
            add(detailPanel, BorderLayout.CENTER);
        }

        private void toggleDetail() {
            if (!isExpanded) {
                // 기존 내용 삭제 후 새로 고침
                detailPanel.removeAll();
                detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.Y_AXIS)); // 세로 배치
                detailPanel.setBorder(new EmptyBorder(10, 50, 10, 10)); // 왼쪽 여백을 주어 들여쓰기 효과

                // DB에서 해당 유저의 레벨별 상세 통계 로드
                List<Map<String, Object>> stats = recordDAO.getUserStatistics(targetUser);

                if (stats.isEmpty()) {
                    detailPanel.add(new JLabel("상세 기록이 없습니다."));
                } else {
                    for (Map<String, Object> s : stats) {
                        // 각 레벨별 한 줄(Row) 생성
                        detailPanel.add(createDetailRow(s));
                        detailPanel.add(Box.createVerticalStrut(5)); // 줄 간격
                    }
                }
                detailPanel.revalidate();
            }
            isExpanded = !isExpanded;
            detailPanel.setVisible(isExpanded);
            revalidate();
            repaint();
        }
        /**
         * 레벨별 총점과 최고점을 한 줄에 예쁘게 보여주는 메서드
         */
        private JPanel createDetailRow(Map<String, Object> s) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
            row.setOpaque(false);

            int lv = (int) s.get("level");
            // 총점과 최고점 데이터 추출 (RecordDAO의 Map 키값 기준)
            String total = s.get("totalScore").toString();
            String best = s.get("maxScore").toString();
            String lastPlay = (String) s.get("lastPlay");

            // UI 레이블 생성
            JLabel lvLbl = new JLabel("Lv." + lv);
            lvLbl.setFont(new Font("맑은 고딕", Font.BOLD, 13));
            lvLbl.setForeground(new Color(0, 102, 204)); // 레벨 강조 색상

            JLabel scoreInfo = new JLabel(String.format("총점: %s 점 | 최고점: %s 점 (%s)", total, best, lastPlay));
            scoreInfo.setFont(new Font("맑은 고딕", Font.PLAIN, 13));

            row.add(lvLbl);
            row.add(scoreInfo);

            return row;
        }
    }
}