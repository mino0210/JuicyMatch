package cardGame.game;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.File;

public class Sound {
	private Clip clip;

	public void play(String filePath, boolean loop, float volume) {
		new Thread(() -> {
			try {
				// 1. 전달받은 전체 경로(filePath)에서 파일명만 추출합니다.
				// 예: "D:/.../cardGame/sound/Card_Flip.wav" -> "Card_Flip.wav"
				File file = new File(filePath);
				String fileName = file.getName();

				// 2. 리소스 루트인 /cardGame/sound/ 와 추출한 파일명을 결합합니다.
				String resourcePath = "/cardGame/sound/" + fileName;

				InputStream is = Sound.class.getResourceAsStream(resourcePath);

				if (is == null) {
					System.err.println("[오류] 사운드 리소스를 찾을 수 없습니다: " + resourcePath);
					return;
				}

				AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
				clip = AudioSystem.getClip();
				clip.open(ais);

				if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
					FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
					gainControl.setValue(volume);
				}

				if (loop) clip.loop(Clip.LOOP_CONTINUOUSLY);
				else clip.start();

			} catch (Exception e) {
				System.err.println("사운드 재생 실패: " + e.getMessage());
			}
		}).start();
	}

	public void stop() {
		if (clip != null) {
			clip.stop();
			clip.close();
		}
	}

	public boolean isPlaying() {
		return (clip != null && clip.isRunning());
	}
}