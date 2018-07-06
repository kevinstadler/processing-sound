package processing.sound;

import com.jsyn.unitgen.PulseOscillator;

import processing.core.PApplet;

/**
 * This is a simple Pulse oscillator.
 * 
 * @webref sound
 * @param parent
 *            PApplet: typically use "this"
 **/
public class Pulse extends Oscillator<PulseOscillator> {

	public Pulse(PApplet theParent) {
		super(theParent, new PulseOscillator());
	}

	/**
	 * Set the pulse width of the oscillator.
	 * 
	 * @webref sound
	 * @param width
	 *            The relative pulse width of the oscillator as a float value
	 *            between 0.0 and 1.0 (exclusive)
	 **/
	public void width(float width) {
		this.oscillator.width.set(width);
	}

	/**
	 * Set multiple parameters at once
	 * 
	 * @webref sound
	 * @param freq
	 *            The frequency value of the oscillator in Hz.
	 * @param width
	 *            The pulse width of the oscillator as a value between 0.0 and 1.0.
	 * @param amp
	 *            The amplitude of the oscillator as a value between 0.0 and 1.0.
	 * @param add
	 *            A value for modulating other audio signals.
	 * @param pos
	 *            The panoramic position of the oscillator as a float from -1.0 to
	 *            1.0.
	 **/
	public void set(float freq, float width, float amp, float add, float pos) {
		this.width(width);
		this.set(freq, amp, add, pos);
	}
}
