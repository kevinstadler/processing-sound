package processing.sound;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.Pan;
import com.jsyn.unitgen.UnitGenerator;

import processing.core.PApplet;

public class Engine {

	private static Engine singleton;

	private Synthesizer synth;
	private LineOut lineOut;
	
	private Engine() {
		this.synth = JSyn.createSynthesizer();
		this.synth.start();

		this.lineOut = new LineOut(); // stereo lineout by default
		this.synth.add(lineOut);
		this.lineOut.start();
	}
	
	protected static Engine getEngine() {
		return Engine.getEngine(null);
	}

	protected static Engine getEngine(PApplet parent) {
		if (Engine.singleton == null) {
			Engine.singleton = new Engine(); // TODO actually store parent?
		}
		return Engine.singleton;
	}

	protected void add(UnitGenerator generator) {
		this.synth.add(generator);
	}

	protected void add(Pan generator) {
		this.synth.add(generator);
		generator.output.connect(0, lineOut.input, 0);
		generator.output.connect(1, lineOut.input, 1);
	}
	
	protected void remove(UnitGenerator generator) {
		this.synth.remove(generator);
	}
}
