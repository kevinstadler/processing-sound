package processing.sound;

import processing.core.PApplet;

/**
* This is a White Noise Generator. White Noise has a flat spectrum. 
* @webref sound
* @param parent PApplet: typically use "this"	
**/
public class WhiteNoise extends Noise<com.jsyn.unitgen.WhiteNoise> {

	public WhiteNoise(PApplet theParent) {
		super(theParent);
		this.noise.output.connect(this.add.inputA);
	}

	protected com.jsyn.unitgen.WhiteNoise newNoiseInstance() {
		return new com.jsyn.unitgen.WhiteNoise();
	}

	protected void setNoiseAmp(float amp) {
		this.noise.amplitude.set(amp);
	}
}
