package cardGame.game;

import javax.sound.sampled.*;
import java.net.URL;

public class Sound {
	private Clip clip;
	private Clip itemClip;
	private FloatControl gainControl;
	private float previousVolume = -20.0f;

	// [Sound.java 수정본]
	public void play(String fileName, boolean loop, float volume) {
		try {
			// [추가] 이미 재생 중인 Clip이 있다면 확실히 정지하고 닫기
			if (clip != null) {
				if (clip.isRunning()) clip.stop();
				clip.close();
			}

			URL url = getClass().getResource("/cardGame/sound/" + fileName);
			if (url == null) return;

			AudioInputStream ais = AudioSystem.getAudioInputStream(url);
			clip = AudioSystem.getClip();
			clip.open(ais);

			if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
				gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
				setVolume(volume);
			}

			if (loop) clip.loop(Clip.LOOP_CONTINUOUSLY);
			clip.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void playItemSound(String fileName, float volume) {
		try {
			stopItemSound();
			URL url = getClass().getResource("/cardGame/sound/" + fileName);
			if (url == null) return;

			AudioInputStream ais = AudioSystem.getAudioInputStream(url);
			itemClip = AudioSystem.getClip();
			itemClip.open(ais);

			if (itemClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
				FloatControl volumeControl = (FloatControl) itemClip.getControl(FloatControl.Type.MASTER_GAIN);
				volumeControl.setValue(volume);
			}
			itemClip.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stopItemSound() {
		if (itemClip != null) {
			if (itemClip.isRunning()) itemClip.stop();
			itemClip.close();
			itemClip = null;
		}
	}

	public void setVolume(float volume) {
		if (gainControl != null) {
			float v = Math.max(-80.0f, Math.min(6.0f, volume));
			gainControl.setValue(v);
		}
	}

	public void setMute(boolean isMute) {
		if (gainControl != null) {
			if (isMute) {
				previousVolume = gainControl.getValue();
				gainControl.setValue(-80.0f);
			} else {
				gainControl.setValue(previousVolume);
			}
		}
	}

	public void stop() {
		if (clip != null) {
			if (clip.isRunning()) clip.stop();
			clip.close();
		}
	}

	// --- [복구 및 추가] 상태 확인 메서드 ---

	// 일반 배경음/효과음 재생 여부 확인 (복구)
	public boolean isPlaying() {
		return (clip != null && clip.isRunning());
	}

	// 아이템 시계 소리 재생 여부 확인 (추가)
	public boolean isItemPlaying() {
		return (itemClip != null && itemClip.isRunning());
	}
}