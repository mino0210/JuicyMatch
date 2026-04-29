package cardGame.game;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * 사운드 재생 클래스 - 안전성 강화 + 동기화 버전
 * Audio playback class - Enhanced safety + synchronized version
 */
public class Sound {
	private Clip clip;
	private Clip itemClip;
	private FloatControl gainControl;
	private float previousVolume = -20.0f;
	
	// [중요] 클래스 레벨 락 - AudioSystem 동시 호출 방지
	// Class-level lock to prevent concurrent AudioSystem calls
	private static final Object AUDIO_LOCK = new Object();

	/**
	 * 사운드 재생 / Play sound
	 */
	public void play(String fileName, boolean loop, float volume) {
		synchronized (AUDIO_LOCK) {
			try {
				// [중요] 이전 Clip 완전 정리
				// Fully clean up previous clip
				cleanupClip();

				URL url = getClass().getResource("/cardGame/sound/" + fileName);
				if (url == null) {
					System.err.println("사운드 파일을 찾을 수 없음: " + fileName);
					return;
				}

				// BufferedInputStream으로 감싸기 (mark/reset 지원)
				// Wrap with BufferedInputStream (supports mark/reset)
				try (InputStream rawStream = url.openStream();
					 BufferedInputStream bufferedStream = new BufferedInputStream(rawStream);
					 AudioInputStream ais = AudioSystem.getAudioInputStream(bufferedStream)) {
					
					AudioFormat baseFormat = ais.getFormat();
					
					// 호환되지 않는 포맷이면 표준 포맷으로 변환
					// Convert to standard format if incompatible
					AudioFormat targetFormat;
					if (baseFormat.getSampleRate() <= 0 || baseFormat.isBigEndian()) {
						// 비표준 포맷 → 표준 포맷으로 변환
						// Non-standard format -> convert to standard format
						targetFormat = new AudioFormat(
							AudioFormat.Encoding.PCM_SIGNED,
							44100, 16, baseFormat.getChannels(),
							baseFormat.getChannels() * 2, 44100, false
						);
					} else {
						targetFormat = baseFormat;
					}
					
					AudioInputStream finalStream;
					if (!targetFormat.matches(baseFormat) && 
						AudioSystem.isConversionSupported(targetFormat, baseFormat)) {
						finalStream = AudioSystem.getAudioInputStream(targetFormat, ais);
					} else {
						finalStream = ais;
					}

					clip = AudioSystem.getClip();
					clip.open(finalStream);

					if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
						gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
						setVolume(volume);
					}

					if (loop) clip.loop(Clip.LOOP_CONTINUOUSLY);
					clip.start();
				}
			} catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
				System.err.println("사운드 재생 실패 [" + fileName + "]: " + e.getMessage());
			} catch (Exception e) {
				System.err.println("예기치 못한 사운드 오류 [" + fileName + "]: " + e.getMessage());
			}
		}
	}

	/**
	 * 아이템 사운드 재생 / Play item sound
	 */
	public void playItemSound(String fileName, float volume) {
		synchronized (AUDIO_LOCK) {
			try {
				stopItemSound();
				
				URL url = getClass().getResource("/cardGame/sound/" + fileName);
				if (url == null) return;

				try (InputStream rawStream = url.openStream();
					 BufferedInputStream bufferedStream = new BufferedInputStream(rawStream);
					 AudioInputStream ais = AudioSystem.getAudioInputStream(bufferedStream)) {
					
					itemClip = AudioSystem.getClip();
					itemClip.open(ais);

					if (itemClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
						FloatControl volumeControl = (FloatControl) itemClip.getControl(FloatControl.Type.MASTER_GAIN);
						volumeControl.setValue(volume);
					}
					itemClip.start();
				}
			} catch (Exception e) {
				System.err.println("아이템 사운드 재생 실패 [" + fileName + "]: " + e.getMessage());
			}
		}
	}

	/**
	 * Clip 안전하게 정리 / Safely cleanup clip
	 */
	private void cleanupClip() {
		if (clip != null) {
			try {
				if (clip.isRunning()) clip.stop();
				if (clip.isOpen()) clip.close();
			} catch (Exception ignored) {}
			clip = null;
		}
	}

	public void stopItemSound() {
		if (itemClip != null) {
			try {
				if (itemClip.isRunning()) itemClip.stop();
				if (itemClip.isOpen()) itemClip.close();
			} catch (Exception ignored) {}
			itemClip = null;
		}
	}

	public void setVolume(float volume) {
		if (gainControl != null) {
			try {
				float v = Math.max(-80.0f, Math.min(6.0f, volume));
				gainControl.setValue(v);
			} catch (Exception ignored) {}
		}
	}

	public void setMute(boolean isMute) {
		if (gainControl != null) {
			try {
				if (isMute) {
					previousVolume = gainControl.getValue();
					gainControl.setValue(-80.0f);
				} else {
					gainControl.setValue(previousVolume);
				}
			} catch (Exception ignored) {}
		}
	}

	public void stop() {
		synchronized (AUDIO_LOCK) {
			cleanupClip();
		}
	}

	public boolean isPlaying() {
		return (clip != null && clip.isRunning());
	}

	public boolean isItemPlaying() {
		return (itemClip != null && itemClip.isRunning());
	}
}
