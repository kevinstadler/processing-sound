package processing.sound;

import com.jsyn.unitgen.SineOscillator;

import processing.core.PApplet;

public class SinOsc extends Oscillator<SineOscillator> {
	public SinOsc(PApplet theParent) {
		super(theParent);
	}

	protected SineOscillator newOscillatorInstance() {
		return new SineOscillator();
	}
}
