package processing.sound;

import com.jsyn.unitgen.TriangleOscillator;

import processing.core.PApplet;

public class TriOsc extends Oscillator<TriangleOscillator> {
	public TriOsc(PApplet theParent) {
		super(theParent);
	}

	protected TriangleOscillator newOscillatorInstance() {
		return new TriangleOscillator();
	}
}
