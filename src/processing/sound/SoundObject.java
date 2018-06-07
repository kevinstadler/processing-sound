package processing.sound;

import com.jsyn.unitgen.UnitFilter;

import processing.core.PApplet;

/**
 * Convenience class with a Pan and an Add
 */
abstract class SoundObject {

	// subclasses need to initialise this circuit
	protected JSynCircuit circuit;

	private boolean isPlaying = false;

	protected SoundObject(PApplet parent) {
		Engine.getEngine(parent);
	}

	/**
	 * Offset the output of this generator by given value
	 * @webref sound
	 * @param add A value for offsetting the audio signal.
	 **/
	public final void add(float add) {
		// TODO check if circuit supports add
		this.circuit.processor.add(add);
	}

	/**
	 * Changes the amplitude/volume of this sound.
	 * @webref sound
	 * @param amp A float value between 0.0 and 1.0 controlling the amplitude/volume of this sound.
	 **/
	public abstract void amp(float amp);

	/**
	 * TODO
	 * @webref sound
	 * @return
	 */
	public boolean isPlaying() {
		return this.isPlaying;
	}

	/**
	 * Move the sound in a stereo panorama.
	 * @webref sound
	 * @param pos The panoramic position of this sound unit as a float from -1.0 (left) to 1.0 (right).
	 **/
	public final void pan(float pos) {
		// TODO check if circuit supports pan
		if (Engine.checkPan(pos)) {
			this.circuit.processor.pan(pos);
		}
	}

	/**
	 * Start the generator
	 * @webref sound
	 **/
	public void play() {
		Engine.getEngine().add(this.circuit);
		Engine.getEngine().play(this.circuit);
		this.isPlaying = true;
	}

	/**
	 * Stop the generator
	 * @webref sound
	 **/
	public void stop() {
		this.isPlaying = false;
		Engine.getEngine().stop(this.circuit);
		Engine.getEngine().remove(this.circuit);
	}

	protected void setEffect(Effect<? extends UnitFilter> effect) {
		// TODO check if same effect is set a second time
		if (this.circuit.effect != null) {
			this.removeEffect(this.circuit.effect);
		}

		Engine.getEngine().add(effect.left);
		Engine.getEngine().add(effect.right);
		this.circuit.setEffect(effect);
	}

	protected void removeEffect (Effect<? extends UnitFilter> effect) {
		if (this.circuit.effect != effect) {
			// possibly a previous effect that's being stopped here, ignore call
			PApplet.println("Error: this effect is not currently processing any signals.");
			return;
		}

		this.circuit.removeEffect();
		Engine.getEngine().remove(effect.left);
		Engine.getEngine().remove(effect.right);
	}
}
