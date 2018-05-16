package processing.sound;

import com.jsyn.unitgen.SquareOscillator;

import processing.core.PApplet;

public class SqrOsc extends Oscillator<SquareOscillator> {
	public SqrOsc(PApplet theParent) {
		super(theParent);
	}

	protected SquareOscillator newOscillatorInstance() {
		return new SquareOscillator();
	}
}
