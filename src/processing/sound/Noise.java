package processing.sound;

import com.jsyn.unitgen.UnitGenerator;
import com.jsyn.unitgen.UnitSource;

import processing.core.PApplet;

abstract class Noise<JSynNoise extends UnitGenerator> extends SoundObject {

	protected JSynNoise noise;

	protected Noise(PApplet theParent, JSynNoise noise) {
		super(theParent);
		this.noise = noise;
		this.circuit = new JSynCircuit(((UnitSource) this.noise).getOutput());
	}

	/**
	 * Set multiple parameters at once.
	 * @webref sound
	 * @param amp Amplitude value between 0.0 and 1.0
	 * @param add Offset the generator by a given value
	 * @param pos Pan the generator in stereo panorama. Allowed values are between -1.0 and 1.0.
	 **/
	public void set(float amp, float add, float pos) {
		this.amp(amp);
		this.add(add);
		this.pan(pos);
	}
}
