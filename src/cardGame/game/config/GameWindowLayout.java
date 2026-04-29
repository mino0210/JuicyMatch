package cardGame.game.config;

/**
 * 인게임 화면 배치값을 한 곳에서 관리합니다.
 * Centralizes in-game screen layout constants.
 */
public final class GameWindowLayout {
    public static final int SCREEN_W = 1920;
    public static final int SCREEN_H = 1080;

    public static final int LEFT_MARGIN = 200;
    public static final int SIDE_PANEL_W = 200;
    public static final int SIDE_PANEL_H = 800;
    public static final int SIDE_PANEL_Y = 215;
    public static final int RIGHT_PANEL_X = SCREEN_W - SIDE_PANEL_W - LEFT_MARGIN;

    public static final int TITLE_W = 600;
    public static final int TITLE_H = 270;
    public static final int TITLE_X = (SCREEN_W - TITLE_W) / 2;
    public static final int TITLE_Y = 0;

    public static final int BOARD_BG_W = 900;
    public static final int BOARD_BG_H = 800;
    public static final int BOARD_BG_X = (SCREEN_W - BOARD_BG_W) / 2;
    public static final int BOARD_BG_Y = 215;

    private GameWindowLayout() {}
}
