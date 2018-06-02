package processing.sound;

import com.jsyn.ports.UnitInputPort;
import com.jsyn.ports.UnitOutputPort;
import com.jsyn.unitgen.Add;
import com.jsyn.unitgen.Circuit;
import com.jsyn.unitgen.Pan;

import processing.core.PApplet;

/**
 * Convenience class with a Pan and an Add
 */
abstract class SoundObject {

	protected Circuit circuit = new Circuit();
	private Pan pan = new Pan();
	private Add add = new Add();

	protected UnitInputPort input;
//	protected UnitOutputPort output; // TODO connect pan or effect

	// subclasses should call this constructor, then connect their generator
	// units to this.input
	protected SoundObject(PApplet parent) {
		Engine.getEngine();
		this.circuit.add(pan);
		this.circuit.add(add);

		this.add.output.connect(this.pan.input);
		this.add.inputB.set(0.0);
		this.input = this.add.inputA;
	}

	/**
	* Offset the output of this generator by given value
	* @webref sound
	* @param add A value for offsetting the audio signal.
	**/
	public final void add(float add) {
		this.add.inputB.set(add);
	}

	/**
	* Changes the amplitude/volume of the player.
	* @webref sound
	* @param amp A float value between 0.0 and 1.0 controlling the amplitude/volume of the player.
	**/
	public abstract void amp(float amp);

	/**
	* Move the sound in a stereo panorama.
	* @webref sound
	* @param pos The panoramic position of this sound unit as a float from -1.0 (left) to 1.0 (right).
	**/
	public final void pan(float pos) {
		if (Engine.checkPan(pos)) {
			this.pan.pan.set(pos);
		}
	}

	UnitOutputPort getOutput() {
		return this.pan.output;
	}
	
	/**
	* Start the generator
	* @webref sound
	**/
	public void play() {
		Engine.getEngine().add(this.circuit);
		Engine.getEngine().play(this.pan);
	}

	/**
	* Stop the generator
	* @webref sound
	**/
	public void stop() {
		Engine.getEngine().stop(this.pan);
		Engine.getEngine().remove(this.circuit);
	}
}
