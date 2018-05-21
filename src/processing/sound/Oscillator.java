package processing.sound;

import com.jsyn.unitgen.UnitOscillator;

import processing.core.PApplet;

public abstract class Oscillator<JSynOscillator extends UnitOscillator> extends SoundObject {
	
	protected JSynOscillator oscillator;

	protected abstract JSynOscillator newOscillatorInstance();
	
	protected Oscillator(PApplet theParent) {
		super(theParent);
		this.oscillator = this.newOscillatorInstance();
		this.oscillator.output.connect(this.add.inputA);
	}
	
	public void amp(float amp) {
		// TODO check argument in [0,1]?
		this.oscillator.amplitude.set(amp);
	}
	
	public void freq(float freq) {
		this.oscillator.frequency.set(freq);
	}
	
	public void play() {
		Engine.getEngine().add(this.oscillator);
		super.play();
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
		super.stop();
		Engine.getEngine().remove(this.oscillator);
	}
}
