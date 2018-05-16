package processing.sound;

import com.jsyn.unitgen.SawtoothOscillator;

import processing.core.PApplet;

public class SawOsc extends Oscillator<SawtoothOscillator> {
	public SawOsc(PApplet theParent) {
		super(theParent);
	}

	protected SawtoothOscillator newOscillatorInstance() {
		return new SawtoothOscillator();
	}
}
