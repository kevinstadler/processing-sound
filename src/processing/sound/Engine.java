package processing.sound;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.devices.AudioDeviceFactory;
import com.jsyn.devices.AudioDeviceManager;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.UnitGenerator;
import com.jsyn.unitgen.UnitSource;

import processing.core.PApplet;

// class needs to be public for registered callback methods to be callable
public class Engine {

	protected void startSynth(int sampleRate) {
		if (this.synth.isRunning()) {
			this.synth.stop();
		}
		this.synth.start(sampleRate, AudioDeviceManager.USE_DEFAULT_DEVICE, this.numInputs,
				AudioDeviceManager.USE_DEFAULT_DEVICE, this.numOutputs);
	}

	protected int getSampleRate() {
		return this.synth.getFrameRate();
	}

	public void dispose() {
		this.lineOut.stop();
		this.synth.stop();
	}

	public void pause() {
		// TODO android only
	}

	public void resume() {
		// TODO android only
	}

	protected static Engine getEngine() {
		return AudioDevice.getEngine(null);
	}

	protected void add(UnitGenerator generator) {
		if (generator.getSynthesisEngine() == null) {
			this.synth.add(generator);
		}
	}

	protected void remove(UnitGenerator generator) {
		this.synth.remove(generator);
	}

	protected void play(UnitSource source) {
		// TODO check if unit is already connected
		source.getOutput().connect(0, this.lineOut.input, 0);
		source.getOutput().connect(1, this.lineOut.input, 1);
	}

	protected void stop(UnitSource source) {
		source.getOutput().disconnect(0, this.lineOut.input, 0);
		source.getOutput().disconnect(1, this.lineOut.input, 1);
	}

	protected static boolean checkAmp(float amp) {
		if (amp < -1 || amp > 1) {
			Engine.printError("amplitude has to be in [-1,1]");
			return false;
		}
		return true;
	}

	protected static boolean checkPan(float pan) {
		if (pan < -1 || pan > 1) {
			Engine.printError("pan has to be in [-1,1]");
			return false;
		}
		return true;
	}

	protected static boolean checkRange(float value, String name) {
		if (value < 0 || value > 1) {
			Engine.printError(name + " parameter has to be between 0 and 1 (inclusive)");
			return false;
		}
		return true;
	}

	protected static void printWarning(String message) {
		PApplet.println("Sound library warning: " + message);
	}

	protected static void printError(String message) {
		PApplet.println("Sound library error: " + message);
	}
}
