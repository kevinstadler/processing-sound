package processing.sound;

import processing.core.PApplet;

/**
* This is a pink noise generator. Pink Noise has a decrease of 3dB per octave.
* @webref sound
* @param parent PApplet: typically use "this"	
**/
public class PinkNoise extends Noise<com.jsyn.unitgen.PinkNoise> {

	protected PinkNoise(PApplet theParent) {
		super(theParent);
		this.noise.output.connect(this.input);
	}

	protected com.jsyn.unitgen.PinkNoise newNoiseInstance() {
		return new com.jsyn.unitgen.PinkNoise();
	}

	protected void setNoiseAmp(float amp) {
		this.noise.amplitude.set(amp);
	}

}
