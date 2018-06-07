package processing.sound;

import processing.core.PApplet;

/**
 * This is a brown noise generator. Brown noise has a decrease of 6db per octave.
 * @webref sound
 * @param parent PApplet: typically use "this"	
 **/
public class BrownNoise extends Noise<com.jsyn.unitgen.BrownNoise> {

	protected BrownNoise(PApplet theParent) {
		super(theParent, new com.jsyn.unitgen.BrownNoise());
	}

	protected void setNoiseAmp(float amp) {
		this.noise.amplitude.set(amp);
	}
}
