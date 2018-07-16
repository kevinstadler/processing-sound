package processing.sound;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.devices.AudioDeviceFactory;
import com.jsyn.devices.AudioDeviceManager;
import com.jsyn.unitgen.LineOut;

import processing.core.PApplet;

/**
 * AudioDevice allows for configuring the audio server. If you need a low
 * latency server you can reduce the buffer size. Allowed values are power of 2.
 * For changing the sample rate pass the appropriate value in the constructor.
 * 
 * @webref sound
 * @param parent
 *            PApplet: typically use "this"
 * @param sampleRate
 *            Sets the sample rate (default 44100).
 * @param bufferSize
 *            Sets the buffer size (not used).
 **/
public class AudioDevice {

	private static AudioDevice singleton;

	private AudioDeviceManager audioManager;
	protected Synthesizer synth;
	private LineOut lineOut;
	private int sampleRate = 44100;
	private int numInputs;
	private int numOutputs;

	// internal dummy constructor that initialises and sets the singleton
	private AudioDevice(PApplet parent, AudioDevice device) {
		if (AudioDevice.singleton == null) {
			AudioDevice.singleton = this;

			try {
				Class.forName("javax.sound.sampled.AudioSystem");
				this.audioManager = AudioDeviceFactory.createAudioDeviceManager();
			} catch (ClassNotFoundException e) {
				this.audioManager = new JSynAndroidAudioDeviceManager();
			}
			this.synth = JSyn.createSynthesizer(this.audioManager);

			int numDevices = audioManager.getDeviceCount();
			for (int i = 0; i < numDevices; i++) {
				String deviceName = audioManager.getDeviceName(i);
				int maxInputs = audioManager.getMaxInputChannels(i);
				int maxOutputs = audioManager.getMaxOutputChannels(i);
				boolean isDefaultInput = (i == audioManager.getDefaultInputDeviceID());
				boolean isDefaultOutput = (i == audioManager.getDefaultOutputDeviceID());
				if (isDefaultInput) {
					this.numInputs = maxInputs;
				}
				if (isDefaultOutput) {
					// could try to grab more output channels if we wanted to?
					this.numOutputs = Math.min(maxOutputs, 2);
				}
				System.out.println("#" + i + " : " + deviceName);
				System.out.println("  max inputs : " + maxInputs + (isDefaultInput ? "   (default)" : ""));
				System.out.println("  max outputs: " + maxOutputs + (isDefaultOutput ? "   (default)" : ""));
			}

			this.lineOut = new LineOut(); // stereo lineout by default
			this.synth.add(lineOut);
			this.lineOut.start();

//			if (parent != null) {
//				parent.registerMethod("dispose", this);
//				// Android only
//				parent.registerMethod("pause", this);
//				parent.registerMethod("resume", this);
//			}

		}
		// TODO adopt parameters
		this.startSynth(44100);

	}

	public AudioDevice(PApplet theParent, int sampleRate) {
		this(theParent, new AudioDevice(sampleRate));
	}

	public AudioDevice(PApplet theParent, int sampleRate, int bufferSize) {
		// bufferSize was necessary for original library's FFT to
		// work, but currently ignored
		this(theParent, sampleRate);
	}

	protected static AudioDevice getEngine(PApplet parent) {
		if (AudioDevice.singleton == null) {
			AudioDevice.singleton = new AudioDevice(parent);
		}
		return AudioDevice.singleton;
	}

	// TODO original library had other public methods which were however not
	// documented

	// TODO move Engine class infrastructure over to AudioDevice altogether?
}
