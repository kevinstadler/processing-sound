package processing.sound;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.devices.AudioDeviceFactory;
import com.jsyn.devices.AudioDeviceManager;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.UnitGenerator;
import com.jsyn.unitgen.UnitSource;

import processing.core.PApplet;

public class Engine {

	private static Engine singleton;

	private static AudioDeviceManager audioManager;
	protected Synthesizer synth;
	private LineOut lineOut;

	private int sampleRate = 44100;

	// set in constructor
	private int inputDevice;
	private int outputDevice;

	private static AudioDeviceManager getAudioManager() {
		if (Engine.audioManager == null) {
			try {
				Class.forName("javax.sound.sampled.AudioSystem");
				Engine.audioManager = AudioDeviceFactory.createAudioDeviceManager();
			} catch (ClassNotFoundException e) {
				Engine.audioManager = new JSynAndroidAudioDeviceManager();
			}
		}
		return Engine.audioManager;
	}

	public Engine(PApplet theParent) {
		// only call initalisation steps if not already initialised
		if (Engine.singleton != null) {
			return;
		}

		// create and start the synthesizer, and set this object as the singleton.
		theParent.registerMethod("dispose", this);
		// Android only
		theParent.registerMethod("pause", this);
		theParent.registerMethod("resume", this);

		this.synth = JSyn.createSynthesizer(Engine.getAudioManager());
		this.inputDevice = Engine.audioManager.getDefaultInputDeviceID();
		this.outputDevice = Engine.audioManager.getDefaultOutputDeviceID();

		this.lineOut = new LineOut(); // stereo lineout by default
		this.synth.add(lineOut);
		this.lineOut.start();

		this.startSynth();

		// store singleton
		Engine.singleton = this;
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
			System.out.println("#" + i + " : " + deviceName);
			System.out.println("  max inputs : " + maxInputs + (isDefaultInput ? "   (default)" : ""));
			System.out.println("  max outputs: " + maxOutputs + (isDefaultOutput ? "   (default)" : ""));
		}
		return devices;
	}

	protected void startSynth() {
		if (this.synth.isRunning()) {
			this.synth.stop();
		}

		// TODO do some more user-friendly checks based on getMaxInput/OutputChannels
		this.synth.start(this.sampleRate,
				this.inputDevice, Engine.getAudioManager().getMaxInputChannels(this.inputDevice),
				// TODO limit number of output channels to 2?
				this.outputDevice, Engine.getAudioManager().getMaxOutputChannels(this.outputDevice));
	}

	/**
	 * Set the internal sample rate of the synthesis engine.
	 * @param sampleRate the sample rate to be used by the synthesis engine (default 44100)
	 * @webref sound
	 */
	public void sampleRate(int sampleRate) {
		Engine.singleton.sampleRate = sampleRate;
		Engine.singleton.startSynth();
	}

	
	public void inputDevice(int deviceId) {
		Engine.singleton.inputDevice = deviceId;
		Engine.singleton.startSynth();
	}

	public void outputDevice(int deviceId) {
		Engine.singleton.outputDevice = deviceId;
		Engine.singleton.startSynth();
	}

	protected int getSampleRate() {
		return this.synth.getFrameRate();
	}

	protected static Engine getEngine() {
		return Engine.getEngine(null);
	}

	protected static Engine getEngine(PApplet parent) {
		if (Engine.singleton == null) {
			Engine.singleton = new Engine(parent);
		}
		return Engine.singleton;
	}

	protected void add(UnitGenerator generator) {
		if (generator.getSynthesisEngine() == null) {
			this.synth.add(generator);
		}
	}

	protected void remove(UnitGenerator generator) {
		this.synth.remove(generator);
	}

	protected void play(UnitSource source) {
		// TODO check if unit is already connected
		source.getOutput().connect(0, this.lineOut.input, 0);
		source.getOutput().connect(1, this.lineOut.input, 1);
	}

	protected void stop(UnitSource source) {
		source.getOutput().disconnect(0, this.lineOut.input, 0);
		source.getOutput().disconnect(1, this.lineOut.input, 1);
	}

	protected static boolean checkAmp(float amp) {
		if (amp < -1 || amp > 1) {
			Engine.printError("amplitude has to be in [-1,1]");
			return false;
		}
		return true;
	}

	protected static boolean checkPan(float pan) {
		if (pan < -1 || pan > 1) {
			Engine.printError("pan has to be in [-1,1]");
			return false;
		}
		return true;
	}

	protected static boolean checkRange(float value, String name) {
		if (value < 0 || value > 1) {
			Engine.printError(name + " parameter has to be between 0 and 1 (inclusive)");
			return false;
		}
		return true;
	}

	protected static void printWarning(String message) {
		PApplet.println("Sound library warning: " + message);
	}

	protected static void printError(String message) {
		PApplet.println("Sound library error: " + message);
	}

	public void dispose() {
		this.lineOut.stop();
		this.synth.stop();
	}

	public void pause() {
		// TODO android only
	}

	public void resume() {
		// TODO android only
	}
}
