package processing.sound;

import processing.core.PApplet;

public class WhiteNoise extends Noise<com.jsyn.unitgen.WhiteNoise> {

	public WhiteNoise(PApplet theParent) {
		super(theParent);
		this.noise.output.connect(this.pan.input);
	}

	protected com.jsyn.unitgen.WhiteNoise newNoiseInstance() {
		return new com.jsyn.unitgen.WhiteNoise();
	}

	protected void setNoiseAmp(float amp) {
		this.noise.amplitude.set(amp);
	}
}
