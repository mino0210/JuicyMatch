package cardGame.game.panels;

import cardGame.entity.Record;
import cardGame.entity.User;
import cardGame.game.GameController;
import cardGame.mgr.Manageable;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import static cardGame.game.GameController.*;

public class TablePanel extends JPanel implements ListSelectionListener {

	private static final long serialVersionUID = 1L;
	private static TablePanel tablePanel = null;
	private JTable table;
	private DefaultTableModel tableModel; // 테이블 데이터 관리
	private int selectedIndex = -1;
	private BottomPane bottom;
	private User loginedUser;
	private GameController gameController;

	private String[] columnNames = { "등수", "아이디", "총점", "최고 점수" };

	private TablePanel() {
		super(new BorderLayout());
	}

	public static TablePanel GetInstance() {
		if (tablePanel == null) tablePanel = new TablePanel();
		return tablePanel;
	}

	public void initUI() {
		// 수정: RECORD_FILE_PATH 매개변수 제거
		// Modified: removed RECORD_FILE_PATH parameter
		initTablePane();

		JScrollPane center = new JScrollPane(table);
		center.getViewport().setBackground(Color.decode("#FFF8E8")); // 빈 영역 배경색 설정

		add(center, BorderLayout.CENTER);

		bottom = new BottomPane();
		bottom.init();
		add(bottom, BorderLayout.PAGE_END);
	}

	// 수정: 매개변수 제거
	// Modified: removed parameter
	void initTablePane() {
		tableModel = new DefaultTableModel(columnNames, 0);
		table = new JTable(tableModel);

		// 테이블 설정
		// Setup table
		table.getSelectionModel().addListSelectionListener(this);
		table.setFont(new Font("굴림", Font.BOLD, 18));
		table.setRowHeight(40); // 행 높이
		table.setPreferredScrollableViewportSize(new Dimension(800, 300)); // 테이블 크기
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// 테이블 헤더
		// Table header
		table.getTableHeader().setFont(new Font("굴림", Font.BOLD, 16));
		table.getTableHeader().setPreferredSize(new Dimension(800, 40));
		table.getTableHeader().setBackground(Color.decode("#FFE8CC")); // 헤더 배경색 변경

		// 빈 영역 배경색 설정
		// Set background color for empty area
		table.setBackground(Color.decode("#FFF8E8"));

		// 수정: loadData 호출 시 매개변수 제거
		// Modified: removed parameter from loadData call
		loadData();
	}

	// 수정: 매개변수 String filename 제거 및 메모리(recordMgr) 데이터 사용
	// Modified: removed String filename parameter, use memory (recordMgr) data
    public void loadData() {
		Map<String, List<Integer>> gameRecords = new HashMap<>();

		// DB에서 이미 불러온 recordMgr.mList를 사용합니다.
		// Use recordMgr.mList already loaded from DB.
		for (Manageable m : recordMgr.mList) {
			Record record = (Record) m;
			int score = record.getScore();
			String id = record.getUser().getUsername();
			gameRecords.computeIfAbsent(id, k -> new ArrayList<>()).add(score);
		}

		// 총점, 최고점수 계산
		// Calculate total score and best score
		List<Map.Entry<String, Integer>> totalScores = new ArrayList<>();
		for (Map.Entry<String, List<Integer>> entry : gameRecords.entrySet()) {
			String id = entry.getKey();
			List<Integer> scores = entry.getValue();
			int totalScore = scores.stream().mapToInt(Integer::intValue).sum();
			totalScores.add(new AbstractMap.SimpleEntry<>(id, totalScore));
		}

		// 총점에 따른 내림차순 정렬
		// Sort descending by total score
		totalScores.sort((entry1, entry2) -> Integer.compare(entry2.getValue(), entry1.getValue()));

		// 테이블 데이터 초기화
		// Reset table data
		tableModel.setRowCount(0);

		int rank = 1;
		for (Map.Entry<String, Integer> entry : totalScores) {
			String id = entry.getKey();
			int totalScore = entry.getValue();

			// 해당 아이디의 최고 점수 계산
			// Calculate best score for the user ID
			int maxScore = gameRecords.get(id).stream().mapToInt(Integer::intValue).max().orElse(0);

			// 테이블에 행 추가 (순위, 아이디, 총점, 최고 점수)
			// Add row to table (rank, ID, total score, best score)
			tableModel.addRow(new Object[]{rank++, id, totalScore, maxScore});
		}
	}

	void searchMyRecord(String gameId){
		List<String[]> searchResults = new ArrayList<>();

		for (Manageable m : recordMgr.mList) {
			Record record = (Record) m;
			int score = record.getScore();
			String id = record.getUser().getUsername();

			if (id.equals(gameId)) {
				searchResults.add(new String[]{id, String.valueOf(score)});
			}
		}

		tableModel.setRowCount(0);
		searchResults.sort((record1, record2) -> Integer.compare(Integer.parseInt(record2[1]), Integer.parseInt(record1[1])));

		int rank = 1;
		for (String[] record : searchResults) {
			tableModel.addRow(new Object[]{rank++, record[0], record[1]});
		}

		String[] searchColumnNames = {"등수", "아이디", "점수"};
		tableModel.setColumnIdentifiers(searchColumnNames);
	}

	void search(String gameId) {
		if (gameId.isEmpty()) {
			return;
		}

		List<String[]> searchResults = new ArrayList<>();

		for (Manageable m : recordMgr.mList) {
			Record record = (Record) m;
			int score = record.getScore();
			String id = record.getUser().getUsername();

			if (id.contains(gameId)) {
				searchResults.add(new String[]{id, String.valueOf(score)});
			}
		}

		tableModel.setRowCount(0);
		searchResults.sort((record1, record2) -> Integer.compare(Integer.parseInt(record2[1]), Integer.parseInt(record1[1])));

		int rank = 1;
		for (String[] record : searchResults) {
			tableModel.addRow(new Object[]{rank++, record[0], record[1]});
		}

		String[] searchColumnNames = {"등수", "아이디", "점수"};
		tableModel.setColumnIdentifiers(searchColumnNames);
	}

	// 테이블 초기화 시 파일 경로 참조 제거
	// Remove file path reference when resetting table
	public void resetTable() {
		tableModel.setColumnIdentifiers(new String[]{"등수", "아이디", "총점", "최고 점수"});
		tableModel.setRowCount(0);
		loadData(); // 매개변수 없이 호출
		clearTableSelection();
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		ListSelectionModel lsm = (ListSelectionModel) e.getSource();
		if (lsm.isSelectionEmpty()) return;

		selectedIndex = lsm.getMinSelectionIndex();
		if (selectedIndex >= 0 && selectedIndex < tableModel.getRowCount()) {
			Object[] rowTexts = new Object[tableModel.getColumnCount()];
			for (int i = 0; i < rowTexts.length; i++)
				rowTexts[i] = tableModel.getValueAt(selectedIndex, i);

			// BottomPane으로 선택된 데이터 전달 (문자열 배열로 변환 필요시)
			// Pass selected data to BottomPane (convert to string array if needed)
			String[] stringRowTexts = Arrays.stream(rowTexts).map(String::valueOf).toArray(String[]::new);
			bottom.moveSelectedToEdits(stringRowTexts);
		}
	}

	public JTable getTable() {
		return table;
	}

	public void clearTableSelection() {
		table.clearSelection();
	}
}