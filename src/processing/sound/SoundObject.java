package processing.sound;

import processing.core.PApplet;

public abstract class SoundObject {
	protected SoundObject(PApplet parent) {
		Engine.getEngine(parent);
	}
}
