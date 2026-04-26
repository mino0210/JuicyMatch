package cardGame.entity;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Board {
    private final List<Card> cards = new ArrayList<>();
    private final JPanel boardPanel = new JPanel();
    private final JPanel boardContainer = new JPanel();
    private final JPanel boardWrapper = new JPanel();

    public Board(int rows, int cols) {
        boardPanel.setLayout(new GridLayout(rows, cols, 5, 5));
        boardPanel.setBackground(new Color(153,102,51));
        boardPanel.setPreferredSize(new Dimension(cols * 150 + (cols - 1) * 5, rows * 200 + (rows - 1) * 5));

        initCards(rows * cols / 2);

        boardWrapper.setLayout(new BorderLayout());
        boardWrapper.setBackground(new Color(153,102,51));
        boardWrapper.add(boardPanel, BorderLayout.CENTER);
        boardWrapper.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        boardContainer.setLayout(new BorderLayout());
        boardContainer.setBackground(new Color(245, 245, 235));
        boardContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 3),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));
        boardContainer.add(boardWrapper, BorderLayout.CENTER);
    }

    // --- 추가된 메서드: 인덱스로 카드 객체 가져오기 ---
    public Card getCard(int index) {
        if (index >= 0 && index < cards.size()) {
            return cards.get(index);
        }
        return null;
    }

    // --- 추가된 메서드: UI 강제 새로고침 ---
    public void refresh() {
        boardPanel.revalidate();
        boardPanel.repaint();
    }

    public JPanel getBoardContainer() {
        return boardContainer;
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

    public int getCardCnt(){
        int cnt=0;
        for (Card card : cards) {
            if(!card.isMatched()){
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
        boardPanel.remove(card);
        card.match(false);

        JLabel placeholder = new JLabel();
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
            if (!card.isMatched()) return false;
        }
        return true;
    }
}