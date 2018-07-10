package processing.sound;

import com.jsyn.unitgen.ChannelIn;
import com.jsyn.unitgen.Multiply;

import processing.core.PApplet;

/**
 * AudioIn lets you grab the audio input from your sound card.
 * 
 * @webref sound
 * @param parent
 *            PApplet: typically use "this"
 * @param in
 *            input channel number (optional, default 0)
 **/
public class AudioIn extends SoundObject {

	// ChannelIn for mono, LineIn for stereo
	private ChannelIn input;

	// unlike the oscillator classes, ChannelIn does not have an amplitude
	// port, so we need to control the amplitude via an extra multiplier unit
	private Multiply multiplier;

	public AudioIn(PApplet parent) {
		this(parent, 0);
	}

	public AudioIn(PApplet parent, int in) {
		super(parent);
		this.input = new ChannelIn(in);
		this.multiplier = new Multiply();
		this.multiplier.inputA.connect(this.input.output);
		this.amplitude = this.multiplier.inputB;

		this.circuit = new JSynCircuit(this.multiplier.output);
		this.circuit.add(this.input);
	}

	/**
	 * Start capturing the input stream and route it to the audio output
	 * 
	 * @webref sound
	 **/
	public void play() {
		super.play();
	}

	/**
	 * Start the input stream without routing it to the audio output. This is useful
	 * if you only want to perform audio analysis based on the microphone input.
	 * 
	 * @webref sound
	 */
	public void start() {
		Engine.getEngine().add(this.circuit);
	}

	public void start(float amp) {
		this.amp(amp);
		this.start();
	}

	public void start(float amp, float add) {
		this.add(add);
		this.start(amp);
	}

	public void start(float amp, float add, float pos) {
		this.set(amp, add, pos);
		this.start();
	}

	/**
	 * Sets amplitude, add and pan position with one method.
	 * 
	 * @webref sound
	 * @param amp
	 *            Amplitude value between 0.0 and 1.0
	 * @param add
	 *            Offset the generator by a given value
	 * @param pos
	 *            Pan the generator in stereo panorama. Allowed values are between
	 *            -1.0 and 1.0.
	 **/
	public void set(float amp, float add, float pos) {
		this.amp(amp);
		this.add(add);
		this.pan(pos);
	}
}
