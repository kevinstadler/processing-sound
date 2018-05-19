package processing.sound;

import processing.core.PApplet;

public class PinkNoise extends Noise<com.jsyn.unitgen.PinkNoise> {

	protected PinkNoise(PApplet theParent) {
		super(theParent);
		this.noise.output.connect(this.pan.input);
	}

	protected com.jsyn.unitgen.PinkNoise newNoiseInstance() {
		return new com.jsyn.unitgen.PinkNoise();
	}

}
