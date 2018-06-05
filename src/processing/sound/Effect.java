package processing.sound;

import com.jsyn.unitgen.UnitFilter;

import processing.core.PApplet;

abstract class Effect<EffectType extends UnitFilter> {

	protected EffectType effect;

	protected SoundObject input;

	Effect(PApplet parent, EffectType effect) {
		Engine.getEngine(parent);
		this.effect = effect;
	}

	protected void process(SoundObject input) {
		this.input = input;
		// attach effect to circuit until removed with effect.stop()
		this.input.setEffect(this.effect);
	}

	/**
	 * 	Stops the Filter.
	 */
	public void stop() {
		if (this.input == null) {
			// TODO print error msg
		} else {
			this.input.removeEffect(this.effect);
			this.input = null;
		}
	}
}
