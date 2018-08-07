package processing.sound;

import com.jsyn.data.FloatSample;
import com.jsyn.unitgen.VariableRateDataReader;
import com.jsyn.unitgen.VariableRateMonoReader;
import com.jsyn.unitgen.VariableRateStereoReader;

import processing.core.PApplet;

/**
 * 
 * @webref sound
 */
public class AudioSample extends SoundObject {

	protected FloatSample sample;
	protected VariableRateDataReader player;

	protected int startFrame = 0;

	/**
	 * Allocate a new audiosample buffer with the given number of frames.
	 * @param parent PApplet: typically use "this"
	 * @param data 
	 * @param frames The desired number of frames for this audiosample
	 * @param frameRate The underlying frame rate of the sample (default: 44100)
	 * @param stereo boolean: whether to treat the audiosample as 2-channel (stereo) or not.
	 * @webref sound
	 */
	public AudioSample(PApplet parent, int frames) {
		this(parent, frames, false);
	}

	public AudioSample(PApplet parent, int frames, boolean stereo) {
		this(parent, frames, false, 44100); // TODO read current framerate from Engine
	}

	public AudioSample(PApplet parent, int frames, boolean stereo, int frameRate) {
		super(parent);
		this.sample = new FloatSample(frames, stereo ? 2 : 1);
		this.sample.setFrameRate(frameRate);
		this.initiatePlayer();
	}

	// TODO add another set of constructors: AudioSample(PApplet parent, float duration)?
	// risk of accidental overloading through int/float, but could be interesting..

	public AudioSample(PApplet parent, float[] data) {
		this(parent, data, false);
	}

	public AudioSample(PApplet parent, float[] data, boolean stereo) {
		this(parent, data, stereo, 44100); // TODO read current framerate from Engine
	}

	public AudioSample(PApplet parent, float[] data, int frameRate) {
		this(parent, data, false, frameRate);
	}

	public AudioSample(PApplet parent, float[] data, boolean stereo, int frameRate) {
		super(parent);
		this.sample = new FloatSample(data, stereo ? 2 : 1);
		this.sample.setFrameRate(frameRate);
		this.initiatePlayer();
	}

	// called by subclasses who initialise their own sample object
	protected AudioSample(PApplet parent) {
		super(parent);
	}

	// private constructor for cloning (see getUnusedPlayer() method below)
	protected AudioSample(AudioSample original) {
		super(null);
		this.sample = original.sample;
		this.initiatePlayer();
		this.player.amplitude.set(original.player.amplitude.get());
		this.player.rate.set(original.player.rate.get());
		this.startFrame = original.startFrame;
	}

	// should be called by the constructor after the sample object has been set
	protected void initiatePlayer() {
		if (this.channels() == 2) {
			this.player = new VariableRateStereoReader();
		} else {
			this.player = new VariableRateMonoReader();
		}

		// needs to be set explicitly
		this.player.rate.set(this.sampleRate());
		this.circuit = new JSynCircuit(this.player.output);
		this.amplitude = this.player.amplitude;

		// unlike the Oscillator and Noise classes, the sample player units can
		// always stay connected to the JSyn synths, since they make no noise
		// as long as their dataQueue is empty
		super.play(); // doesn't actually start playback, just adds the (silent) units
	}

	public void amp(float amp) {
		if (Engine.checkAmp(amp)) {
			this.player.amplitude.set(amp);
		}
	}

	/**
	 * Returns the number of channels in the audiosample.
	 * @webref sound
	 * @return Returns the number of channels in the audiosample (1 for mono, 2 for stereo)
	 **/
	public int channels() {
		return this.sample.getChannelsPerFrame();
	}

	/**
	 * Cues the playhead to a fixed position in the audiosample.
	 * @webref sound
	 * @param time Position to start from in seconds.
	 **/
	public void cue(float time) {
		this.setStartTime(time);
	}

	/**
	 * Cues the playhead to a fixed position in the audiosample.
	 * @webref sound
	 * @param frameNumber Frame number to start playback from.
	 **/
	public void cueFrame(int frameNumber) {
		this.setStartFrame(frameNumber);
	}
	
	/**
	 * Returns the duration of the audiosample in seconds.
	 * @webref sound
	 * @return The duration of the audiosample in seconds.
	 **/
	public float duration() {
		return (float) (this.frames() / this.sample.getFrameRate());
	}

	/**
	 * Returns the number of frames of the audiosample.
	 * @webref sound
	 * @return The number of frames of the audiosample.
	 * @see duration()
	 **/
	public int frames() {
		return this.sample.getNumFrames();
	}

	private void setStartFrame(int frameNumber) {
		if (frameNumber < 0 || frameNumber >= this.frames()) {
			Engine.printError("invalid start frame number (has to be in [0," + Integer.toString(this.frames()-1) + "]");
		} else {
			this.startFrame = frameNumber;
		}
	}

	private boolean setStartTime(float time) {
		if (time < 0) {
			Engine.printError("absolute position can't be < 0");
			return false;
		}
		int startFrame = Math.round(this.sampleRate() * time);
		if (startFrame >= this.frames()) {
			Engine.printError("can't cue past of end of sample (total duration is " + this.duration() + "s)");
			return false;
		}
		this.startFrame = startFrame;
		return true;
	}

	/**
	 * Jump to a specific position in the audiosample while continuing to play.
	 * @webref sound
	 * @param time Position to jump to as a float in seconds.
	 **/
	public void jump(float time) {
		if (this.setStartTime(time)) {
			this.stop();
			this.play(); // TODO what if the file wasn't playing when jump() was called?
		}
	}

	// helper function: when called on a soundfile already running, the original
	// library triggered a second (concurrent) playback. with JSyn, every data
	// reader can only do one playback at a time, so if the present player
	// is busy we need to create a new one with the exact same settings and
	// trigger it instead
	protected AudioSample getUnusedPlayer() {
		// TODO TODO TODO
		if (this.isPlaying()) {
			return new AudioSample(this);
		} else {
			return this;
		}
	}

	/**
	 * Starts playback which will loop at the end of the sample.
	 * @webref sound
	 **/	
	public void loop() {
		AudioSample source = this.getUnusedPlayer();
		source.player.dataQueue.queueLoop(source.sample,
				source.startFrame,
				source.frames() - source.startFrame);
		// for improved handling by the user, return a reference to whichever
		// sound file is the source of the newly triggered playback
		// return source;
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

	/**
	 * Starts the playback of the audiosample. Only plays the audiosample once.
	 * @webref sound
	 **/
	public void play() {
		AudioSample source = this.getUnusedPlayer();
		source.player.dataQueue.queue(source.sample,
				source.startFrame,
				source.frames() - source.startFrame);
		// for improved handling by the user, could return a reference to
		// whichever audiosample object is the actual source (i.e. JSyn
		// container) of the newly triggered playback
		//return source;
	}

	public void play(float rate) {
		this.rate(rate);
		this.play();
	}

	public void play(float rate, float amp) {
		this.amp(amp);
		this.play(rate);
	}

	public void play(float rate, float pos, float amp) {
		this.pan(pos);
		this.play(rate, amp);
	}

	public void play(float rate, float pos, float amp, float add) {
		this.add(add);
		this.play(rate, pos, amp);
	}

	public void play(float rate, float pos, float amp, float add, float cue) {
		this.cue(cue);
		this.play(rate, pos, amp, add);
	}

	/**
	 * Set the playback rate of the audiosample.
	 * @webref sound
	 * @param rate Relative playback rate to use. 1 is the original speed. 0.5 is half speed and one octave down. 2 is double the speed and one octave up.
	 **/
	public void rate(float rate) {
		if (rate <= 0) {
			Engine.printError("rate needs to be positive");
		} else {
			// 1.0 = original
			this.player.rate.set(this.sampleRate() * rate);
		}
	}

	/**
	 * Resizes the underlying buffer of the audiosample to the given number of frames.
	 * @param frames
	 * @param stereo
	 * @webref sound
	 */
	public void resize(int frames, boolean stereo) {
		this.sample.allocate(frames, stereo ? 2 : 1);
	}

	/**
	 * Returns the underlying sample rate of the audiosample.
	 * @webref sound
	 * @return Returns the underlying sample rate of the audiosample as an int.
	 **/
	public int sampleRate() {
		return (int) Math.round(this.sample.getFrameRate());
	}

	/**
	 * Set multiple parameters at once
	 * @webref sound
	 * @param rate The playback rate of the original file. 
	 * @param pos The panoramic position of the player as a float from -1.0 to 1.0.
	 * @param amp The amplitude of the player as a value between 0.0 and 1.0.
	 * @param add A value for modulating other audio signals.
	 **/
	public void set(float rate, float pos, float amp, float add) {
		this.rate(rate);
		this.pan(pos);
		this.amp(amp);
		this.add(add);
	}

	/**
	 * Stops the playback.
	 * @webref sound
	 **/
	public void stop() {
		this.player.dataQueue.clear();
	}

	// new methods go here

	/**
	 * Get current sound file playback position in seconds.
	 * @return The current position of the sound file playback in seconds (TODO seconds at which sample rate?)
	 * @webref sound
	 */
	public float position() {
		// progress in sample seconds or current-rate-playback seconds??
		return this.player.dataQueue.getFrameCount() / (float) this.sampleRate();
	}

	/**
	 * Get current sound file playback position in percent.
	 * @return The current position of the sound file playback in percent (a value between 0 and 100).
	 * @webref sound
	 */
	public float percent() {
		return 100f * this.player.dataQueue.getFrameCount() / (float) this.frames();
	}

	/**
	 * Check whether this soundfile is currently playing.
	 * @return `true` if the soundfile is currently playing, `false` if it is not.
	 * @webref sound
	 */
	public boolean isPlaying() {
		// overrides the SoundObject's default implementation
		return this.player.dataQueue.hasMore();
	}

	/**
	 * Stop the playback of the file, but cue it to the current position so that
	 * the next call to play() will continue playing where it left off.
	 * @webref sound
	 */
	public void pause() {
		if (this.isPlaying()) {
			this.startFrame = (int) this.player.dataQueue.getFrameCount();
			this.stop();
		} else {
			Engine.printWarning("sound file is not currently playing");
		}
	}

	/**
	 * Get the current sample data and write it into the given array. The array
	 * has to be able to store as many floats as there are frames in this sample
	 * (or twice as many if this is a stereo sample).
	 * @param data the target array to write the sample data to
	 * @webref sound
	 */
	public void read(float[] data) {
		this.sample.read(data);
	}

	public void read(int startFrame, float[] data, int startIndex, int numFrames) {
		this.sample.read(startFrame, data, startIndex, numFrames);
	}

	public float readFrame(int index) {
		return (float) this.sample.readDouble(index);
	}

	/**
	 * Overwrite the sample with the data from the given array. The array can
	 * contain up to as many floats as there are frames in this sample
	 * (or twice as many if this is a stereo sample).
	 * @param data the array from which the sample data should be taken
	 * @webref sound
	 */
	public void write(float[] data) {
		this.sample.write(data);
	}

	public void write(int startFrame, float[] data, int startIndex, int numFrames) {
		this.sample.write(startFrame, data, startIndex, numFrames);
	}

	public void writeFrame(int index, float value) {
		this.sample.writeDouble(index, value);
	}
}
