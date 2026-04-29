package cardGame.game.config;

/**
 * 랭킹 화면 배치값을 한 곳에서 관리합니다.
 * Centralizes layout constants for the ranking screen.
 */
public final class RankingLayout {
    public static final int SCREEN_W = 1920;
    public static final int SCREEN_H = 1080;

    public static final int TITLE_W = 500;
    public static final int TITLE_H = 150;
    public static final int TITLE_X = (SCREEN_W - TITLE_W) / 2;
    public static final int TITLE_Y = 0;

    public static final int PANEL_W = 1250;
    public static final int PANEL_H = 950;
    public static final int PANEL_X = (SCREEN_W - PANEL_W) / 2;
    public static final int PANEL_Y = -10;

    public static final int LIST_W = 1140;
    public static final int LIST_X = (PANEL_W - LIST_W) / 2;
    public static final int LIST_Y = 225;
    public static final int LIST_H = 595;
    public static final int TOP_GAP_IN_LIST = 6;

    public static final int ROW_W = 1085;
    public static final int ROW_H = 92;
    public static final int ROW_GAP = 14;
    public static final int EXPANDED_EXTRA_H = 210;

    public static final int BACK_BTN_W = 300;
    public static final int BACK_BTN_H = 70;
    public static final int BACK_BTN_X = (SCREEN_W - BACK_BTN_W) / 2;
    public static final int BACK_BTN_Y = 880;

    private RankingLayout() {}
}
