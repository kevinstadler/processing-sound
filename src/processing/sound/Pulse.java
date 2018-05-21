package processing.sound;

import com.jsyn.unitgen.PulseOscillator;

import processing.core.PApplet;

public class Pulse extends Oscillator<PulseOscillator> {
	public Pulse(PApplet theParent) {
		super(theParent);
	}

	protected PulseOscillator newOscillatorInstance() {
		return new PulseOscillator();
	}
	
	public void width(float width) {
		this.oscillator.width.set(width);
	}

	public void set(float freq, float width, float amp, float add, float pos) {
		this.width(width);
		this.set(freq, amp, add, pos);
	}
}
