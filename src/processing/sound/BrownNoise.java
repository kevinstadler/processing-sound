package processing.sound;

import processing.core.PApplet;

public class BrownNoise extends Noise<com.jsyn.unitgen.BrownNoise> {

	protected BrownNoise(PApplet theParent) {
		super(theParent);
		this.noise.output.connect(this.add.inputA);
	}

	protected com.jsyn.unitgen.BrownNoise newNoiseInstance() {
		return new com.jsyn.unitgen.BrownNoise();
	}

	protected void setNoiseAmp(float amp) {
		this.noise.amplitude.set(amp);
	}
}
