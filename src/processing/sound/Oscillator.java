package processing.sound;

import com.jsyn.unitgen.Pan;
import com.jsyn.unitgen.UnitOscillator;

import processing.core.PApplet;

public abstract class Oscillator<JSynOscillator extends UnitOscillator> extends SoundObject {
	
	protected JSynOscillator oscillator;
	private Pan pan = new Pan();

	protected abstract JSynOscillator newOscillatorInstance();
	
	protected Oscillator(PApplet theParent) {
		super(theParent);
		this.oscillator = this.newOscillatorInstance();
		this.oscillator.output.connect(this.pan.input);
	}
	
	public void add(float add) {
		// TODO
	}
	
	public void amp(float amp) {
		// TODO check argument in [0,1]?
		this.oscillator.amplitude.set(amp);
	}
	
	public void freq(float freq) {
		this.oscillator.frequency.set(freq);
	}
	
	public void pan(float pan) {
		// TODO check argument in [-1,1]?
		this.pan.pan.set(pan);
	}
	
	public void play() {
		Engine.getEngine().add(this.oscillator);
		Engine.getEngine().add(this.pan);
	}

	public void play(float freq, float amp) {
		this.freq(freq);
		this.amp(amp);
		this.play();
	}

	public void play(float freq, float amp, float add) {
		this.add(add);
		this.play(freq, amp);
	}

	public void set(float freq, float amp, float add, float pos) {
		this.freq(freq);
		this.amp(amp);
		this.add(add);
		this.pan(pos);
	}
	
	public void stop() {
		Engine.getEngine().remove(this.pan);
		Engine.getEngine().remove(this.oscillator);
	}
}
