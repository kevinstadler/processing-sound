package processing.sound;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.devices.AudioDeviceFactory;
import com.jsyn.devices.AudioDeviceManager;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.UnitGenerator;
import com.jsyn.unitgen.UnitSource;

import processing.core.PApplet;

class Engine {

	private static AudioDeviceManager audioManager;
	private static Engine singleton;

	protected Synthesizer synth;
	private LineOut lineOut;

	private int sampleRate = 44100;

	// set in constructor
	private int inputDevice;
	private int outputDevice;

	protected static Engine getEngine(PApplet parent) {
		if (Engine.singleton == null) {
			Engine.singleton = new Engine(parent);
		}
		return Engine.singleton;
	}

	protected static Engine getEngine() {
		return Engine.singleton;
	}

	private Engine(PApplet theParent) {
		// only call initalisation steps if not already initialised
		if (Engine.singleton != null) {
			return;
		}

		// create and start the synthesizer, and set this object as the singleton.
		this.synth = JSyn.createSynthesizer(Engine.getAudioManager());
		this.inputDevice = Engine.getAudioManager().getDefaultInputDeviceID();
		this.outputDevice = Engine.getAudioManager().getDefaultOutputDeviceID();

		this.lineOut = new LineOut(); // stereo lineout by default
		this.synth.add(lineOut);
		this.lineOut.start();

		this.startSynth();
		Engine.singleton = this;

		// register Processing library callback methods
		Object callback = new Callback();
		theParent.registerMethod("dispose", callback);
		// Android only
		theParent.registerMethod("pause", callback);
		theParent.registerMethod("resume", callback);
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

	protected static AudioDeviceManager getAudioManager() {
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

	protected void setSampleRate(int sampleRate) {
		Engine.singleton.sampleRate = sampleRate;
		Engine.singleton.startSynth();
	}

	protected void selectInputDevice(int deviceId) {
		Engine.singleton.inputDevice = deviceId;
		Engine.singleton.startSynth();
	}

	protected void selectOutputDevice(int deviceId) {
		Engine.singleton.outputDevice = deviceId;
		Engine.singleton.startSynth();
	}

	/**
	 * Set the overall output volume of the Processing sound library.
	 * @param volume
	 * @webref sound
	 */
	public void volume(float volume) {
		if (Engine.checkRange(volume, "volume")) {
			// TODO add master JSynCircuit before lineOut to control global volume/pan
		}
	}

	protected int getSampleRate() {
		return this.synth.getFrameRate();
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

	/**
	 * Internal helper class for Processing library callbacks
	 */
	public class Callback {
		public void dispose() {
			lineOut.stop();
			synth.stop();
		}

		public void pause() {
			// TODO
		}

		public void resume() {
			// TODO
		}
	}
}
