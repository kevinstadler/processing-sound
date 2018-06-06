package processing.sound;

import com.jsyn.ports.UnitOutputPort;
import com.jsyn.unitgen.TwoInDualOut;
import com.jsyn.unitgen.UnitFilter;

import processing.core.PApplet;

/**
 * A helper class for applying the same effect (with the same parameters) on two channels.
 * @param <EffectType>
 */
abstract class Effect<EffectType extends UnitFilter> {

	protected SoundObject input;

	// FIXME what do we do if the same effect is applied to several different
	// input sources -- do we consider them all to feed into the same effect
	// unit(s), or should we instantiate new units every time process() is called?
	protected EffectType left;
	protected EffectType right;
	protected UnitOutputPort output;

	Effect(PApplet parent) {
		Engine.getEngine(parent);
		this.left = this.newInstance();
		this.right = this.newInstance();
		TwoInDualOut merge = new TwoInDualOut();
		merge.inputA.connect(this.left.output);
		merge.inputB.connect(this.right.output);
		this.output = merge.output;
	}

	protected abstract EffectType newInstance();

	protected void process(SoundObject input) {
		this.input = input;
		// attach effect to circuit until removed with effect.stop()
		this.input.setEffect(this);
	}

	/**
	 * 	Stops the Filter.
	 */
	public void stop() {
		if (this.input == null) {
			PApplet.println("Error: this effect is not currently processing any signals.");
		} else {
			this.input.removeEffect(this);
			this.input = null;
		}
	}
}
