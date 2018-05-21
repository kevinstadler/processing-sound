package processing.sound;

import com.jsyn.unitgen.Pan;
import com.jsyn.unitgen.UnitGenerator;

import processing.core.PApplet;

public abstract class Noise<JSynNoise extends UnitGenerator> extends SoundObject {

	protected JSynNoise noise;
	protected Pan pan = new Pan();

	protected abstract JSynNoise newNoiseInstance();

	protected Noise(PApplet theParent) {
		super(theParent);
		this.noise = this.newNoiseInstance();
		// subclasses are responsible for connecting the noise unit to the pan!
	}

	public void add(float add) {
		// TODO
	}

	public void amp(float amp) {
		// TODO check argument in [0,1]?
		this.setNoiseAmp(amp);
	}

	// no uniform interface for JSyn's noise classes, implementation needs to be in subclasses
	protected abstract void setNoiseAmp(float amp);

	public void pan(float pan) {
		// TODO check argument in [-1,1]?
		this.pan.pan.set(pan);
	}

	public void play() {
		Engine.getEngine().add(this.noise);
		Engine.getEngine().add(this.pan);
	}

	public void set(float amp, float add, float pos) {
		this.amp(amp);
		this.add(add);
		this.pan(pos);
	}

	public void stop() {
		Engine.getEngine().remove(this.pan);
		Engine.getEngine().remove(this.noise);
	}
}
