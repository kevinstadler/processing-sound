package processing.sound;

import processing.core.PApplet;

/**
 * The ProcessingSound.* functions allow you to set global configuration
 * properties of the sound engine, such as the input and output device,
 * synthesis sample rate or overall volume.
 * 
 * @webref sound
 */
public class ProcessingSound {

	private static boolean registered = false;

	// apart from statically storing configuration values, the (undocumented)
	// public methods of this class are also used as PApplet callbacks
	private ProcessingSound(PApplet parent) {
		if (ProcessingSound.registered) {
			ProcessingSound.registered = true;
			parent.registerMethod("dispose", this);
			// Android only
			parent.registerMethod("pause", this);
			parent.registerMethod("resume", this);
		}
	}

	public void dispose() {
		// TODO
		// this.lineOut.stop();
		// this.synth.stop();
	}

	public void pause() {
		// TODO android only
	}

	public void resume() {
		// TODO android only
	}

	public static void sampleRate(float sampleRate) {
		// TODO
	}

	public static void volume(float volume) {
		// TODO
	}
}
