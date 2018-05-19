package processing.sound;

import java.io.File;
import java.io.IOException;

import com.jsyn.data.FloatSample;
import com.jsyn.unitgen.Pan;
import com.jsyn.unitgen.VariableRateStereoReader;
import com.jsyn.util.SampleLoader;

import processing.core.PApplet;

public class SoundFile extends SoundObject {

	private FloatSample sample;
	private VariableRateStereoReader player = new VariableRateStereoReader();
	private Pan pan = new Pan();
	
	public SoundFile(PApplet parent, String path) throws IOException {
		super(parent);
		File f = new File(path);
		this.sample = SampleLoader.loadFloatSample(f);
		this.player.rate.set(this.sampleRate());
		this.player.output.connect(this.pan.input);
		Engine.getEngine().add(this.player);
		Engine.getEngine().add(this.pan);
	}

	public void amp(float amp) {
		this.player.amplitude.set(amp);
	}
	
	public int frames() {
		return this.sample.getNumFrames();
	}
	
	public int sampleRate() {
		return (int) Math.round(this.sample.getFrameRate());
	}
	
	public int channels() {
		return this.sample.getChannelsPerFrame();
	}
	
	public float duration() {
		// in seconds
		return (float) (this.sample.getNumFrames() / this.sample.getFrameRate());
	}
	
	public void jump(float time) {
		// TODO
		this.player.dataQueue.beginFrame(0);
	}
	
	public void loop() {
		this.player.dataQueue.queueLoop(this.sample);
	}

	public void loop(float rate) {
		this.rate(rate);
		this.loop();
	}

	public void loop(float rate, float amp) {
		this.rate(rate);
		this.amp(amp);
		this.loop();
	}

	public void loop(float rate, float pos, float amp) {
		// TODO convert pos to frame int
//		this.player.dataQueue.queueLoop(this.sample, , );
	}
	
	public void play() {
		this.player.dataQueue.queueOn(this.sample);
	}

	public void rate(float rate) {
		// 1.0 = original TODO test
		this.player.rate.set(rate);
	}
}
