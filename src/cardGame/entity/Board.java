package cardGame.entity;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Board {
    // 최종 인게임 UI에 맞춰 카드 크기/간격 재조정
    // Card size/gap adjusted for final in-game UI
    private static final int CARD_W = 136;
    private static final int CARD_H = 170;
    private static final int CARD_GAP_X = 24;
    private static final int CARD_GAP_Y = 15;

    private static final Color BOARD_BG = new Color(95, 50, 22);

    private final List<Card> cards = new ArrayList<>();
    private final JPanel boardPanel = new JPanel();
    private final JPanel boardContainer = new JPanel();
    private final JPanel boardWrapper = new JPanel();

    public Board(int rows, int cols) {
        boardPanel.setLayout(new GridLayout(rows, cols, CARD_GAP_X, CARD_GAP_Y));
        boardPanel.setOpaque(false);

        int boardW = cols * CARD_W + (cols - 1) * CARD_GAP_X;
        int boardH = rows * CARD_H + (rows - 1) * CARD_GAP_Y;

        boardPanel.setPreferredSize(new Dimension(boardW, boardH));
        boardPanel.setMinimumSize(new Dimension(boardW, boardH));
        boardPanel.setMaximumSize(new Dimension(boardW, boardH));

        initCards(rows * cols / 2);

        boardWrapper.setLayout(new GridBagLayout());
        boardWrapper.setOpaque(false);
        boardWrapper.add(boardPanel);

        boardContainer.setLayout(new GridBagLayout());
        boardContainer.setBackground(BOARD_BG);
        boardContainer.setOpaque(false);
        boardContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        boardContainer.add(boardWrapper);
    }

    public Card getCard(int index) {
        if (index >= 0 && index < cards.size()) {
            return cards.get(index);
        }
        return null;
    }

    public void refresh() {
        boardPanel.revalidate();
        boardPanel.repaint();
    }

    public JPanel getBoardContainer() {
        return boardContainer;
    }

    public Dimension getBoardSize() {
        return boardPanel.getPreferredSize();
    }

    private void initCards(int totalCards) {
        ArrayList<Integer> values = new ArrayList<>();

        for (int i = 1; i <= totalCards; i++) {
            values.add(i);
            values.add(i);
        }

        Collections.shuffle(values);

        for (int value : values) {
            Card card = new Card(value);
            cards.add(card);
            boardPanel.add(card);
        }
    }

    public int getCardCnt() {
        int cnt = 0;

        for (Card card : cards) {
            if (!card.isMatched()) {
                cnt++;
            }
        }

        return cnt;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void showAllCard() {
        for (Card card : cards) {
            card.reveal();
        }
    }

    public void hideAllCard() {
        for (Card card : cards) {
            card.hide();
        }
    }

    public void removeCard(Card card) {
        int index = boardPanel.getComponentZOrder(card);

        if (index < 0) {
            return;
        }

        boardPanel.remove(card);
        card.match(false);

        JLabel placeholder = new JLabel();
        placeholder.setPreferredSize(new Dimension(CARD_W, CARD_H));
        placeholder.setOpaque(false);

        boardPanel.add(placeholder, index);

        boardPanel.revalidate();
        boardPanel.repaint();
    }

    public void resetBoard() {
        for (Card card : cards) {
            card.reset();
        }

        Collections.shuffle(cards);

        boardPanel.removeAll();

        for (Card card : cards) {
            boardPanel.add(card);
        }

        boardPanel.revalidate();
        boardPanel.repaint();
    }

    public boolean isAllMatched() {
        for (Card card : cards) {
            if (!card.isMatched()) {
                return false;
            }
        }

        return true;
    }
}
