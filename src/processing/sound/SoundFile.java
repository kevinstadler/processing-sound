package processing.sound;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.jsyn.data.FloatSample;
import com.jsyn.unitgen.VariableRateStereoReader;
import com.jsyn.util.SampleLoader;

import fr.delthas.javamp3.Sound;
import processing.core.PApplet;

public class SoundFile extends SoundObject {

	private FloatSample sample;
	private VariableRateStereoReader player = new VariableRateStereoReader();

	private int startFrame = 0;

	// TODO determine original behaviour when file not found - exception or println?
	public SoundFile(PApplet parent, String path) throws IOException {
		super(parent);
		File f = new File(path);

		// TODO share samples in memory if multiple SoundFiles created from same resource
		try {
			this.sample = SampleLoader.loadFloatSample(f);
		} catch (IOException e) {
			// try parsing as mp3
			Sound mp3 = new Sound(new FileInputStream(f));
			try {
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				mp3.decodeFullyInto(os);
				float data[] = new float[os.size() / 2];
				SampleLoader.decodeLittleI16ToF32(os.toByteArray(), 0, os.size(), data, 0);
				this.sample = new FloatSample(data, mp3.isStereo() ? 2 : 1);
				// alternatively: convert to Little Endian signed short array like so:
//				short s = (short) (b1<<8 | b2 & 0xFF);
//				this.sample = new ShortSample(s.array(), mp3.isStereo() ? 2 : 1);
			} finally {
				mp3.close();
			}
		}

		// unlike the Oscillator and Noise classes, the sample player units can
		// always stay connected to the JSyn synths, since they make no noise
		// as long as their dataQueue is empty
		this.player.rate.set(this.sampleRate());

		this.player.output.connect(this.add.inputA);
		Engine.getEngine().add(this.player);
		super.play(); // doesn't actually start playback, just adds the (silent) units
	}

	public void amp(float amp) {
		// TODO check in [0, 1]
		this.player.amplitude.set(amp);
	}

	public int channels() {
		return this.sample.getChannelsPerFrame();
	}

	// methCla-based library only supported int here!
	public void cue(float time) {
		this.setStartFrame(time);
	}

	public float duration() {
		// in seconds
		return (float) (this.frames() / this.sample.getFrameRate());
	}

	public int frames() {
		return this.sample.getNumFrames();
	}
	
	private boolean setStartFrame(float time) {
		if (time < 0) {
			PApplet.println("Gotta be positive");
			return false;
		}
		int startFrame = Math.round(this.sampleRate() * time);
		if (startFrame >= this.frames()) {
			PApplet.println("Can't cue past of end of sample (total duration is " + this.duration() + "s)");
			return false;
		}
		this.startFrame = startFrame;
		return true;
	}

	public void jump(float time) {
		if (this.setStartFrame(time)) {
			this.stop();
			this.play(); // TODO what if the file wasn't playing when jump() was called?
		}
	}
	
	public void loop() {
		this.player.dataQueue.queueLoop(this.sample,
				this.startFrame,
				this.frames() - this.startFrame);
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
		this.pan(pos);
		this.loop(rate, amp);
	}

	public void loop(float rate, float pos, float amp, float add) {
		this.add(add);
		this.loop(rate, pos, amp);
	}

	public void loop(float rate, float pos, float amp, float add, float cue) {
		this.cue(cue);
		this.loop(rate, pos, amp, add);
	}

	public void play() {
		// TODO behaviour when it is already playing? (see this.player.dataQueue.hasMore())
		this.player.dataQueue.queue(this.sample,
				this.startFrame,
				this.frames() - this.startFrame);
	}

	public void rate(float rate) {
		// TODO check rate > 0
		// 1.0 = original
		this.player.rate.set(this.sampleRate() * rate);
	}

	public int sampleRate() {
		return (int) Math.round(this.sample.getFrameRate());
	}

	public void set(float rate, float pos, float amp, float add) {
		this.rate(rate);
		this.pan(pos);
		this.amp(amp);
		this.add(add);
	}

	public void stop() {
		this.player.dataQueue.clear();
	}

	// new methods go here
	public boolean isPlaying() {
		return this.player.dataQueue.hasMore();
	}
}
