package processing.sound;

import processing.core.PApplet;

public class BrownNoise extends Noise<com.jsyn.unitgen.BrownNoise> {

	protected BrownNoise(PApplet theParent) {
		super(theParent);
		this.noise.output.connect(this.pan.input);
	}

	protected com.jsyn.unitgen.BrownNoise newNoiseInstance() {
		return new com.jsyn.unitgen.BrownNoise();
	}

}
