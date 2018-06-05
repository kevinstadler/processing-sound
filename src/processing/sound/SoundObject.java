package processing.sound;

import com.jsyn.ports.UnitOutputPort;
import com.jsyn.unitgen.UnitFilter;

import processing.core.PApplet;

/**
 * Convenience class with a Pan and an Add
 */
abstract class SoundObject {

	protected JSynCircuit circuit = new JSynCircuit();

	// pointer to this object's stereo out -- can point to a processor or its plugged-in effect
	protected UnitOutputPort output;

	private boolean isPlaying = false;

	protected SoundObject(PApplet parent) {
		Engine.getEngine();
	}

	/**
	* Offset the output of this generator by given value
	* @webref sound
	* @param add A value for offsetting the audio signal.
	**/
	public final void add(float add) {
		this.circuit.processor.add(add);
	}

	/**
	* Changes the amplitude/volume of the player.
	* @webref sound
	* @param amp A float value between 0.0 and 1.0 controlling the amplitude/volume of the player.
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
		Engine.getEngine().play(this.circuit.processor.output);
		this.isPlaying = true;
	}

	/**
	* Stop the generator
	* @webref sound
	**/
	public void stop() {
		this.isPlaying = false;
		Engine.getEngine().stop(this.circuit.processor.output);
		Engine.getEngine().remove(this.circuit);
	}

	protected void setEffect(UnitFilter effect) {
		if (this.circuit.effect != null) {
			this.removeEffect(this.circuit.effect);
		}
		this.circuit.effect = effect;
		this.circuit.processor.output.connect(this.circuit.effect.input);
		if (this.isPlaying()) {
			Engine.getEngine().stop(this.circuit.processor.output);
			Engine.getEngine().play(this.circuit.effect.output);
		}
	}

	protected void removeEffect (UnitFilter effect) {
		if (effect != this.circuit.effect) {
			 // possibly a previous effect that's being stopped here, ignore call
			return;
		}
		// TODO
	}
}
