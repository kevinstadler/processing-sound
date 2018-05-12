package processing.sound;

import com.jsyn.unitgen.SineOscillator;

import processing.core.PApplet;

public class SinOsc extends Oscillator {
	public SinOsc(PApplet parent) {
		super(parent, new SineOscillator());
	}
}
