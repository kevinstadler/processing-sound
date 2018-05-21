package processing.sound;

import com.jsyn.unitgen.Add;
import com.jsyn.unitgen.Pan;

import processing.core.PApplet;

/**
 * Convenience class with a Pan and an Add
 */
abstract class SoundObject {

	protected Pan pan = new Pan();
	protected Add add = new Add();

	protected SoundObject(PApplet parent) {
		Engine.getEngine();

		this.add.output.connect(this.pan.input);
		this.add.inputB.set(0.0);
		// subclasses should call this constructor, then connect their
		// generator units to this.add.inputA
	}

	public final void add(float add) {
		this.add.inputB.set(add);
	}

	public abstract void amp(float amp);

	public final void pan(float pan) {
		if (Engine.checkPan(pan)) {
			this.pan.pan.set(pan);
		}
	}

	public void play() {
		Engine.getEngine().add(this.add);
		Engine.getEngine().add(this.pan);
	}

	public void stop() {
		Engine.getEngine().remove(this.pan);
		Engine.getEngine().remove(this.add);
	}
}
