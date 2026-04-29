package cardGame.game.panels;


import cardGame.entity.User;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

class BottomPane extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	private JTextField gameIdField;
	private JButton searchButton;
	private JButton resetButton;
	private JButton myScoreButton;


	public BottomPane(){
	}

	private static final Color BUTTON_BORDER_COLOR = new Color(255, 115, 0);  // 테두리 색상 (오렌지색)

	void init() {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(600, 80));

		setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
		setBackground(new Color(255, 232, 204));

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setBackground(new Color(255, 232, 204));

		// 검색창
		// Search field
		gameIdField = new JTextField("", 20);
		gameIdField.setMaximumSize(new Dimension(1500, 40));
		gameIdField.setFont(gameIdField.getFont().deriveFont(17f));
		gameIdField.setBackground(Color.white);
		gameIdField.setBorder(BorderFactory.createLineBorder(Color.white));

		// 플레이스홀더 초기화
		// Initialize placeholder
		initPlaceholder();
		panel.add(gameIdField);

		// 검색 버튼
		// Search button
		searchButton = new JButton("검색");
		customizeButton(searchButton);
		panel.add(Box.createRigidArea(new Dimension(10, 0)));
		panel.add(searchButton);

		// 초기화 버튼
		// Reset button
		resetButton = new JButton("초기화");
		customizeButton(resetButton);
		panel.add(Box.createRigidArea(new Dimension(10, 0)));
		panel.add(resetButton);

		// 내점수 버튼
		// My score button
		myScoreButton = new JButton("내점수");
		customizeButton(myScoreButton);
		panel.add(Box.createRigidArea(new Dimension(10, 0)));
		panel.add(myScoreButton);



		addButtonMouseListener(searchButton);
		addButtonMouseListener(resetButton);
		addButtonMouseListener(myScoreButton);

		// Enter 키로 검색 버튼 클릭
		// Trigger search button on Enter key
		gameIdField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					actionPerformed(new ActionEvent(searchButton, ActionEvent.ACTION_PERFORMED, "검색"));
				}
			}
		});

		add(panel, BorderLayout.CENTER); // 중앙에 입력 패널 추가
	}

	void customizeButton(JButton button) {
		button.setPreferredSize(new Dimension(100, 40));
		button.setMaximumSize(new Dimension(100, 40));
		button.setFont(button.getFont().deriveFont(16f));
		button.setBackground(Color.white);
		button.setFocusPainted(false); // 버튼에 포커스 효과 제거
		button.setOpaque(true);
		button.setBorder(BorderFactory.createLineBorder(BUTTON_BORDER_COLOR, 2)); // 테두리 색상 설정
		button.setUI(new javax.swing.plaf.basic.BasicButtonUI());  // 기본 스타일 제거
		button.addActionListener(this);
	}

	void addButtonMouseListener(JButton button) {
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				button.setBackground(BUTTON_BORDER_COLOR); // 버튼 클릭
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				button.setBackground(Color.white); // 버튼 클릭 해제
			}
		});
	}

	void initPlaceholder() {
		// 플레이스홀더
		// Placeholder
		gameIdField.setText("아이디를 입력하세요!");
		gameIdField.setForeground(BUTTON_BORDER_COLOR); // 플레이스홀더 텍스트 색상 설정

		// 검색 창 클릭 - 플레이스홀더 제거
		// Search field clicked - remove placeholder
		gameIdField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (gameIdField.getText().equals("아이디를 입력하세요!")) {
					gameIdField.setText("");
					gameIdField.setForeground(Color.black); // 텍스트 색상 변경
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (gameIdField.getText().isEmpty()) {
					gameIdField.setText("아이디를 입력하세요!");
					gameIdField.setForeground(BUTTON_BORDER_COLOR); // 색상 복원
				}
			}
		});
	}

	void clearEdits() {
		gameIdField.setText("아이디를 입력하세요!");
		gameIdField.setForeground(BUTTON_BORDER_COLOR); // 색상 복원
	}

	String getGameIdText() {
		String text = gameIdField.getText();
		if (text.equals("아이디를 입력하세요!")) {
			return ""; // 빈 문자열 반환
		}
		return text;
	}

	void moveSelectedToEdits(String[] rowTexts) {
		// 선택된 데이터를 텍스트 필드에 반영하는 로직
		// Logic to reflect selected data into text field
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

		if ("검색".equals(command)) {
			String gameId = getGameIdText();
			clearEdits();
			TablePanel tablePanel = TablePanel.GetInstance();

			tablePanel.search(gameId);  // 검색
			tablePanel.clearTableSelection();
		}
		else if ("초기화".equals(command)) {
			TablePanel tablePanel = TablePanel.GetInstance();

			tablePanel.resetTable();  // 테이블 초기화
			clearEdits();
		}
		else if("내점수".equals(command)){

			User user = GameMenuPanel.getLoginedUser();
			System.out.println("user = " + user);
			if(user == null){
				JOptionPane.showMessageDialog(null, "로그인이 안 되었습니다.");
				return;
			}
			String gameId = user.getUsername();
			clearEdits();
			TablePanel tablePanel = TablePanel.GetInstance();

			tablePanel.searchMyRecord(gameId);
			tablePanel.clearTableSelection();
		}
	}
}

