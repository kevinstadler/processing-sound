package processing.sound;

import com.jsyn.devices.AudioDeviceManager;

import processing.core.PApplet;

/**
 * The Sound class allows for configuring global properties of the sound
 * library's audio synthesis and playback, such as the output device, sample
 * rate or global output volume.
 * @webref sound
 * @param parent
 *            PApplet: typically use "this"
 * @param sampleRate
 *            Sets the sample rate (default 44100).
 */
public class Sound {

	// could make this static as well, Engine class guarantees it's a singleton anyway
	private Engine engine;

	public Sound(PApplet parent) {
		this.engine = Engine.getEngine(parent);
	}

	/**
	 * Print and return information on available audio devices and their number of
	 * input/output channels.
	 * 
	 * @return an array giving the names of all audio devices available on this
	 *         computer
	 * @webref sound
	 */
	public static String[] list() {
		AudioDeviceManager audioManager = Engine.getAudioManager();
		int numDevices = audioManager.getDeviceCount();
		String[] devices = new String[numDevices];
		for (int i = 0; i < numDevices; i++) {
			String deviceName = audioManager.getDeviceName(i);
			devices[i] = audioManager.getDeviceName(i);
			int maxInputs = audioManager.getMaxInputChannels(i);
			int maxOutputs = audioManager.getMaxOutputChannels(i);
			boolean isDefaultInput = (i == audioManager.getDefaultInputDeviceID());
			boolean isDefaultOutput = (i == audioManager.getDefaultOutputDeviceID());
			System.out.println("deviceId" + i + ": " + deviceName);
			System.out.println("  max inputs : " + maxInputs + (isDefaultInput ? "   (default)" : ""));
			System.out.println("  max outputs: " + maxOutputs + (isDefaultOutput ? "   (default)" : ""));
		}
		return devices;
	}

	/**
	 * Get or set the internal sample rate of the synthesis engine.
	 * @param sampleRate the sample rate to be used by the synthesis engine (default 44100)
	 * @return the internal sample rate used by the synthesis engine
	 * @webref sound
	 */
	public int sampleRate() {
		return this.engine.getSampleRate();
	}

	public int sampleRate(int sampleRate) {
		this.engine.setSampleRate(sampleRate);
		return this.sampleRate();
	}

	/**
	 * Choose the device (sound card) which should be used for grabbing audio
	 * input using AudioIn.
	 * 
	 * Note that this setting affects the choice of sound card, which is not
	 * necessarily the same as the number of the input channel. If your sound
	 * card has more than one input channel, you can specify which channel to
	 * use in the constructor of the AudioIn class.
	 * @param deviceId the device id obtained from Sound.list()
	 * @seealso Sound.list()
	 * @webref sound
	 */
	public void inputDevice(int deviceId) {
		this.engine.selectInputDevice(deviceId);
	}

	/**
	 * Choose the device (sound card) which the Sound library's audio output
	 * should be sent to. The output device should support stereo output (2 channels).
	 * @param deviceId the device id obtained from list()
	 * @seealso list()
	 * @webref sound
	 */
	public void outputDevice(int deviceId) {
		this.engine.selectOutputDevice(deviceId);
	}

	/**
	 * Set the overall output volume of the Processing sound library.
	 * @param volume the desired output volume, normally between 0.0 and 1.0 (default is 1.0)
	 * @webref sound
	 */
	public void volume(float volume) {
		this.engine.setVolume(volume);
	}

}
