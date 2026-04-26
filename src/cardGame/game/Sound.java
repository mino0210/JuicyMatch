package cardGame.game;

import javax.sound.sampled.*;
import java.io.File;

public class Sound {
	private Clip clip; // 재생 상태 확인 및 정지를 위한 필드 변수

	public void Sound(String file, boolean loop, float volume) {
		new Thread(() -> {
			try {
				File audioFile = new File(file);
				if (!audioFile.exists()) {
					System.err.println("파일을 찾을 수 없습니다: " + file);
					return;
				}

				AudioInputStream ais = AudioSystem.getAudioInputStream(audioFile);
				this.clip = AudioSystem.getClip();
				this.clip.open(ais);

				if (this.clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
					FloatControl gainControl = (FloatControl) this.clip.getControl(FloatControl.Type.MASTER_GAIN);
					gainControl.setValue(volume);
				}

				if (loop) {
					this.clip.loop(Clip.LOOP_CONTINUOUSLY);
				} else {
					this.clip.start();
				}
			} catch (Exception e) {
				System.err.println("사운드 재생 오류: " + e.getMessage());
			}
		}).start();
	}

	// GameMenu 등에서 사용하는 'isPlaying' 메서드
	public boolean isPlaying() {
		return (clip != null && clip.isRunning());
	}

	// 에러가 났던 'Stop_Sound' 메서드 이름 수정
	public void Stop_Sound() {
		if (clip != null) {
			clip.stop();
			clip.close();
		}
	}
}