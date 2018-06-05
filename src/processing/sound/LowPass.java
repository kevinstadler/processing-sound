package processing.sound;

import com.jsyn.unitgen.FilterLowPass;

import processing.core.PApplet;

public class LowPass extends Effect<FilterLowPass> {

	public LowPass(PApplet parent) {
		super(parent, new com.jsyn.unitgen.FilterLowPass());
	}

	/**
	 * Set the cut off frequency for the filter
	 * @param freq
	 */
	public void freq(float freq) {
		this.effect.frequency.set(freq);
	}
}
