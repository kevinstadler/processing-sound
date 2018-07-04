package processing.sound;

import com.jsyn.ports.UnitOutputPort;

import processing.core.PApplet;

abstract class Analyzer {

	protected SoundObject input;

	protected Analyzer(PApplet parent) {
		Engine.getEngine(parent);
	}

	/**
	 * Define the audio input for the analyzer.
	 * 
	 * @webref sound
	 * @param input The input sound source
	 **/
	public void input(SoundObject input) {
		if (this.input == input) {
			Engine.printWarning("This input was already connected to the analyzer");
		} else {
			if (this.input != null) {
				// TODO disconnect unit from analyzer

				if (!this.input.isPlaying()) {
					// unit was only analyzed but not playing out loud - remove from synth
					Engine.getEngine().remove(this.input.circuit);
				}
			}
			this.input = input;
			if (!this.input.isPlaying()) {
				Engine.getEngine().add(input.circuit);
			}

			this.setInput(input.circuit.output.output);
		}
	}

	// connect sound source in subclass AND add analyser unit to Engine
	protected abstract void setInput(UnitOutputPort input);
}
