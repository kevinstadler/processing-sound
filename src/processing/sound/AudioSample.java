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
	 * 
	 * @param parent
	 * @param data
	 * @param frames
	 * @param stereo
	 * @webref sound
	 */
	public AudioSample(PApplet parent, int frames, boolean stereo) {
		super(parent);
		this.sample = new FloatSample(frames, stereo ? 2 : 1);
		this.initiatePlayer();
	}

	public AudioSample(PApplet parent, int frames) {
		this(parent, frames, false);
	}

	public AudioSample(PApplet parent, float[] data, boolean stereo) {
		super(parent);
		this.sample = new FloatSample(data, stereo ? 2 : 1);
	}

	public AudioSample(PApplet parent, float[] data) {
		this(parent, data, false);
	}

	// called by subclasses who initialise their own sample object
	protected AudioSample(PApplet parent) {
		super(parent);
	}

	// private constructor for cloning
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
	 * Returns the number of channels in the sound file.
	 * @webref sound
	 * @return Returns the number of channels in the sound file (1 for mono, 2 for stereo)
	 **/
	public int channels() {
		return this.sample.getChannelsPerFrame();
	}

	/**
	 * Cues the playhead to a fixed position in the soundfile.
	 * @webref sound
	 * @param time Position to start from in seconds.
	 **/
	// methCla-based library only supported int here!
	public void cue(float time) {
		this.setStartFrame(time);
	}

	/**
	 * Returns the duration of the the soundfile.
	 * @webref sound
	 * @return Returns the duration of the file in seconds.
	 **/
	public float duration() {
		return (float) (this.frames() / this.sample.getFrameRate());
	}

	/**
	 * Returns the number of frames/samples of the sound file.
	 * @webref sound
	 * @return Returns the number of samples of the sound file
	 **/
	public int frames() {
		return this.sample.getNumFrames();
	}

	private boolean setStartFrame(float time) {
		if (time < 0) {
			Engine.printError("absolute position can't be < 0");
			return false;
		}
		int startFrame = Math.round(this.sampleRate() * time);
		if (startFrame >= this.frames()) {
			Engine.printError("can't cue past of end of sample (total duration of soundfile is " + this.duration() + "s)");
			return false;
		}
		this.startFrame = startFrame;
		return true;
	}

	/**
	 * Jump to a specific position in the file while continuing to play.
	 * @webref sound
	 * @param time Position to jump to as a float in seconds.
	 **/
	public void jump(float time) {
		if (this.setStartFrame(time)) {
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
	 * Starts the playback of a soundfile to loop.
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
	 * Starts the playback of a soundfile. Only plays the soundfile once.
	 * @webref sound
	 **/
	public void play() {
		AudioSample source = this.getUnusedPlayer();
		source.player.dataQueue.queue(source.sample,
				source.startFrame,
				source.frames() - source.startFrame);
		// for improved handling by the user, return a reference to whichever
		// sound file is the source of the newly triggered playback
		// return source;
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
	 * Change the playback rate of the soundfile.
	 * @webref sound
	 * @param rate This method changes the playback rate of the soundfile. 1 is the original speed. 0.5 is half speed and one octave down. 2 is double the speed and one octave up. 
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
	 * 
	 * @param frames
	 * @param stereo
	 * @webref sound
	 */
	public void resize(int frames, boolean stereo) {
		this.sample.allocate(frames, stereo ? 2 : 1);
	}

	/**
	 * Returns the sample rate of the soundfile.
	 * @webref sound
	 * @return Returns the sample rate of the soundfile as an int.
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
	 * Stops the player
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

}
