package processing.sound;

import com.jsyn.unitgen.UnitOscillator;

import processing.core.PApplet;

abstract class Oscillator<JSynOscillator extends UnitOscillator> extends SoundObject {
	
	protected JSynOscillator oscillator;

	protected abstract JSynOscillator newOscillatorInstance();
	
	protected Oscillator(PApplet theParent) {
		super(theParent);
		this.oscillator = this.newOscillatorInstance();
		this.oscillator.output.connect(this.add.inputA);
	}
	
	/**
	* Set the amplitude/volume of the oscillator
	* @webref sound
	* @param amp The amplitude value of the oscillator as a float fom 0.0 to 1.0
	**/
	public void amp(float amp) {
		// TODO check argument in [0,1]?
		this.oscillator.amplitude.set(amp);
	}
	
	/**
	* Set the freuquency of the oscillator in Hz.
	* @webref sound
	* @param freq A floating point value of the oscillator in Hz.
	**/
	public void freq(float freq) {
		this.oscillator.frequency.set(freq);
	}
	
	/**
	* Starts the oscillator
	* @webref sound
	**/
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

	/**
	* Set multiple parameters at once
	* @webref sound
	* @param freq The frequency value of the oscillator in Hz.
	* @param amp The amplitude of the oscillator as a value between 0.0 and 1.0.
	* @param add A value for modulating other audio signals.
	* @param pos The panoramic position of the oscillator as a float from -1.0 to 1.0.
	**/
	public void set(float freq, float amp, float add, float pos) {
		this.freq(freq);
		this.amp(amp);
		this.add(add);
		this.pan(pos);
	}
	
	/**
	* Stop the oscillator.
	* @webref sound
	**/
	public void stop() {
		super.stop();
		Engine.getEngine().remove(this.oscillator);
	}
}
