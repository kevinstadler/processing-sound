package processing.sound;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.Pan;
import com.jsyn.unitgen.UnitGenerator;

import processing.core.PApplet;

class Engine {

	private static Engine singleton;

	protected Synthesizer synth;
	private LineOut lineOut;
	
	private Engine() {
		this.synth = JSyn.createSynthesizer();
		this.synth.start();

		this.lineOut = new LineOut(); // stereo lineout by default
		this.synth.add(lineOut);
		this.lineOut.start();
	}
	
	protected static Engine getEngine() {
		if (Engine.singleton == null) {
			Engine.singleton = new Engine();
		}
		return Engine.singleton;
	}

	protected void add(UnitGenerator generator) {
		this.synth.add(generator);
	}

	protected void remove(UnitGenerator generator) {
		this.synth.remove(generator);
	}

	protected void add(Pan generator) {
		this.synth.add(generator);
		generator.output.connect(0, lineOut.input, 0);
		generator.output.connect(1, lineOut.input, 1);
	}

	protected void remove(Pan generator) {
		this.synth.remove(generator);
		generator.output.disconnect(0, lineOut.input, 0);
		generator.output.disconnect(1, lineOut.input, 1);
	}

	protected static boolean checkAmp(float amp) {
		if (amp < 0) {
			PApplet.println("Error: amplitude can't be negative");
			return false;
		}
		return true;
	}

	protected static boolean checkPan(float pan) {
		if (pan < -1 || pan > 1) {
			PApplet.println("Error: pan has to be in [-1,1]");
			return false;
		}
		return true;
	}
}
