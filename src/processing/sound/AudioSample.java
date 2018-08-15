package processing.sound;

import com.jsyn.data.FloatSample;
import com.jsyn.unitgen.VariableRateDataReader;
import com.jsyn.unitgen.VariableRateMonoReader;
import com.jsyn.unitgen.VariableRateStereoReader;

import processing.core.PApplet;

/**
 * This class allows you low-level access to an audio buffer to create, access,
 * manipulate and play back sound samples.
 * 
 * If you want to pre-load your audio sample with an audio file from disk you
 * can do so using the SoundFile subclass.
 * 
 * @see SoundFile
 * @webref sound
 */
public class AudioSample extends SoundObject {

	protected FloatSample sample;
	protected VariableRateDataReader player;

	protected int startFrame = 0;

	public AudioSample(PApplet parent, int frames) {
		this(parent, frames, false);
	}

	public AudioSample(PApplet parent, int frames, boolean stereo) {
		this(parent, frames, stereo, 44100); // read current framerate from Engine instead?
	}

	/**
	 * Allocate a new audiosample buffer with the given number of frames.
	 *
	 * @param parent
	 *            typically use "this"
	 * @param frames
	 *            the desired number of frames for this audiosample
	 * @param frameRate
	 *            the underlying frame rate of the sample (default: 44100)
	 * @param stereo
	 *            whether to treat the audiosample as 2-channel (stereo) or not
	 *            (default: false)
	 * @webref sound
	 */
	public AudioSample(PApplet parent, int frames, boolean stereo, int frameRate) {
		super(parent);
		this.sample = new FloatSample(frames, stereo ? 2 : 1);
		this.sample.setFrameRate(frameRate);
		this.initiatePlayer();
	}

	// TODO add another set of constructors: AudioSample(PApplet parent, float
	// duration)?
	// risk of accidental overloading through int/float, but could be interesting..

	/**
	 * @param data
	 *            an array of float values to be used as this audiosample's sound
	 *            data. The audiosample will consequently have as many frames as the
	 *            length of the given array.
	 * @webref sound
	 */
	public AudioSample(PApplet parent, float[] data) {
		this(parent, data, false);
	}

	public AudioSample(PApplet parent, float[] data, boolean stereo) {
		this(parent, data, stereo, 44100); // read current framerate from Engine instead?
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

	/**
	 * Change the amplitude/volume of this audiosample.
	 *
	 * @param amp
	 *            A float value between 0.0 (complete silence) and 1.0 (full volume)
	 *            controlling the amplitude/volume of this sound.
	 * @webref sound
	 **/
	public void amp(float amp) {
		if (Engine.checkAmp(amp)) {
			this.player.amplitude.set(amp);
		}
	}

	/**
	 * Returns the number of channels in the audiosample.
	 * 
	 * @return the number of channels in the audiosample (1 for mono, 2 for stereo)
	 * @webref sound
	 **/
	public int channels() {
		return this.sample.getChannelsPerFrame();
	}

	/**
	 * Cues the playhead to a fixed position in the audiosample.
	 * 
	 * @param time
	 *            position in the audiosample that the next playback should start
	 *            from, in seconds.
	 * @webref sound
	 **/
	public void cue(float time) {
		this.setStartTime(time);
	}

	/**
	 * Cues the playhead to a fixed position in the audiosample.
	 * 
	 * @webref sound
	 * @param frameNumber
	 *            frame number to start playback from.
	 **/
	public void cueFrame(int frameNumber) {
		this.setStartFrame(frameNumber);
	}

	/**
	 * Returns the duration of the audiosample in seconds.
	 * 
	 * @webref sound
	 * @return The duration of the audiosample in seconds.
	 **/
	public float duration() {
		return (float) (this.frames() / this.sample.getFrameRate());
	}

	/**
	 * Returns the number of frames of the audiosample.
	 * 
	 * @webref sound
	 * @return The number of frames of the audiosample.
	 * @see duration()
	 **/
	public int frames() {
		return this.sample.getNumFrames();
	}

	public void resize(int frames) {
		this.resize(frames, false);
	}

	private void setStartFrame(int frameNumber) {
		if (this.checkStartFrame(frameNumber)) {
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
	 * 
	 * @param time
	 *            position to jump to, in seconds.
	 * @see cue
	 * @see play
	 * @webref sound
	 **/
	public void jump(float time) {
		if (this.setStartTime(time)) {
			this.stop();
			this.play(); // if the file wasn't playing when jump() was called, just start playing it
		}
	}

	// helper function: when called on a soundfile already running, the original
	// library triggered a second (concurrent) playback. with JSyn, every data
	// reader can only do one playback at a time, so if the present player
	// is busy we need to create a new one with the exact same settings and
	// trigger it instead (see JSyn's VoiceAllocator class)
	protected AudioSample getUnusedPlayer() {
		// TODO could implement a more intelligent player allocation pool method here to
		// limit the total number of playback voices
		if (this.isPlaying()) {
			// use private constructor which copies the sample as well as all playback
			// settings over
			return new AudioSample(this);
		} else {
			return this;
		}
	}

	public void loop() {
		AudioSample source = this.getUnusedPlayer();
		source.player.dataQueue.queueLoop(source.sample, 0, source.frames() - source.startFrame);
		// for improved handling by the user, could return a reference to whichever
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

	/**
	 * Starts playback which will loop at the end of the sample.
	 * 
	 * @param rate
	 *            relative playback rate to use. 1 is the original speed. 0.5 is
	 *            half speed and one octave down. 2 is double the speed and one
	 *            octave up.
	 * @param pos
	 *            the panoramic position of this sound unit from -1.0 (left) to 1.0
	 *            (right). Only works for mono audiosamples!
	 * @param amp
	 *            the desired playback amplitude of the audiosample as a value from
	 *            0.0 (complete silence) to 1.0 (full volume)
	 * @param add
	 *            offset the output of the generator by the given value
	 * @webref sound
	 */
	public void loop(float rate, float pos, float amp, float add) {
		this.add(add);
		this.loop(rate, pos, amp);
	}

	/*
	 * FIXME cueing a position for loops has to be handled differently than for
	 * simple playback, because passing a startFrame to dataQueue.queueLoop() causes
	 * repetitions of the loop to only ever be played from that position, instead of
	 * jumping back to the very beginning of the sample after reaching the end
	 * 
	 * @param cue position in the audiosample that the loop should start from, in
	 * seconds. public void loop(float rate, float pos, float amp, float add, float
	 * cue) { this.cue(cue); this.loop(rate, pos, amp, add); }
	 */

	public void play() {
		AudioSample source = this.getUnusedPlayer();
		source.player.dataQueue.queue(source.sample, source.startFrame, source.frames() - source.startFrame);
		// for improved handling by the user, could return a reference to
		// whichever audiosample object is the actual source (i.e. JSyn
		// container) of the newly triggered playback
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

	/**
	 * Starts the playback of the audiosample. Only plays to the end of the
	 * audiosample once.
	 * 
	 * @param rate
	 *            relative playback rate to use. 1 is the original speed. 0.5 is
	 *            half speed and one octave down. 2 is double the speed and one
	 *            octave up.
	 * @param amp
	 *            the desired playback amplitude of the audiosample as a value from
	 *            0.0 (complete silence) to 1.0 (full volume)
	 * @param pos
	 *            the panoramic position of this sound unit from -1.0 (left) to 1.0
	 *            (right). Only works for mono audiosamples!
	 * @param cue
	 *            position in the audiosample that playback should start from, in
	 *            seconds.
	 * @param add
	 *            offset the output of the generator by the given value
	 * @webref sound
	 **/
	public void play(float rate, float pos, float amp, float add, float cue) {
		this.cue(cue);
		this.play(rate, pos, amp, add);
	}

	/**
	 * Set the playback rate of the audiosample.
	 * 
	 * @param rate
	 *            Relative playback rate to use. 1 is the original speed. 0.5 is
	 *            half speed and one octave down. 2 is double the speed and one
	 *            octave up.
	 * @webref sound
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
	 * Resizes the underlying buffer of the audiosample to the given number of
	 * frames.
	 * 
	 * @param frames
	 *            the desired number of frames for this audiosample
	 * @param stereo
	 *            whether to treat the audiosample as 2-channel (stereo) or not
	 *            (default: false)
	 * @webref sound
	 */
	public void resize(int frames, boolean stereo) {
		this.stop();
		this.sample.allocate(frames, stereo ? 2 : 1);
		this.initiatePlayer(); // TODO re-initiation might be redundant
	}

	/**
	 * Returns the underlying sample rate of the audiosample.
	 * 
	 * @webref sound
	 * @return Returns the underlying sample rate of the audiosample as an int.
	 **/
	public int sampleRate() {
		return (int) Math.round(this.sample.getFrameRate());
	}

	/**
	 * Move the sound in a stereo panorama. Only works for mono audiosamples!
	 *
	 * @webref sound
	 * @param pos
	 *            the panoramic position of this sound unit from -1.0 (left) to 1.0
	 *            (right).
	 **/
	public void pan(float pos) {
		super.pan(pos);
	}

	/**
	 * Set multiple parameters at once
	 * 
	 * @webref sound
	 * @param rate
	 *            Relative playback rate to use. 1 is the original speed. 0.5 is
	 *            half speed and one octave down. 2 is double the speed and one
	 *            octave up.
	 * @param pos
	 *            the panoramic position of this sound unit from -1.0 (left) to 1.0
	 *            (right).
	 * @param amp
	 *            the desired playback amplitude of the audiosample as a value from
	 *            0.0 (complete silence) to 1.0 (full volume)
	 * @param add
	 *            offset the output of the generator by the given value
	 **/
	public void set(float rate, float pos, float amp, float add) {
		this.rate(rate);
		this.pan(pos);
		this.amp(amp);
		this.add(add);
	}

	/**
	 * Stops the playback.
	 * 
	 * @webref sound
	 **/
	public void stop() {
		this.player.dataQueue.clear();
	}

	// new methods go here

	/**
	 * Get current sound file playback position in seconds.
	 * 
	 * @return The current position of the sound file playback in seconds
	 * @webref sound
	 */
	public float position() {
		// TODO progress in sample seconds or current-rate-playback seconds??
		// TODO might have to offset getFrameCount by this.startFrame?
		return (this.player.dataQueue.getFrameCount() % this.frames()) / (float) this.sampleRate();
	}

	/**
	 * Get current sound file playback position in percent.
	 * 
	 * @return The current position of the sound file playback in percent (a value
	 *         between 0 and 100).
	 * @webref sound
	 */
	public float percent() {
		// TODO might have to offset getFrameCount by this.startFrame?
		return 100f * (this.player.dataQueue.getFrameCount() % this.frames()) / (float) this.frames();
	}

	/**
	 * Check whether this audiosample is currently playing.
	 * 
	 * @return `true` if the audiosample is currently playing, `false` if it is not.
	 * @webref sound
	 */
	public boolean isPlaying() {
		// overrides the SoundObject's default implementation
		return this.player.dataQueue.hasMore();
	}

	/**
	 * Stop the playback of the sample, but cue it to the current position.
	 * 
	 * @see cue
	 * @webref sound
	 */
	public void pause() {
		if (this.isPlaying()) {
			this.startFrame = (int) this.player.dataQueue.getFrameCount() % this.frames();
			this.stop();
		} else {
			Engine.printWarning("audio sample is not currently playing");
		}
	}

	protected boolean checkStartFrame(int startFrame) {
		return this.checkStartFrame(startFrame, true);
	}

	protected boolean checkStartFrame(int startFrame, boolean verbose) {
		if (startFrame < 0 || startFrame >= this.frames()) {
			if (verbose) {
				Engine.printError(Integer.toString(startFrame) + " is not a valid start frame number (has to be in [0,"
						+ Integer.toString(this.frames() - 1) + "]");
			}
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Get the current sample data and write it into the given array.
	 *
	 * Get the current sample data and write it into the given array. The array has
	 * to be able to store as many floats as there are frames in this sample.
	 *
	 * @param data
	 *            the target array that the read data is written to
	 */
	public void read(float[] data) {
		if (data.length != this.frames()) {
			Engine.printWarning(
					"the length of the given array does not match the number of frames of this audio sample");
		}
		// TODO catch exception and print understandable error message
		this.sample.read(data);
	}

	/**
	 * Read some frames of this audio sample into an array.
	 *
	 * @param startFrame
	 *            the index of the first frame of the audiosample that should be
	 *            read
	 * @param startIndex
	 *            the position in the array where the first read frame should be
	 *            written to (typically 0)
	 * @param numFrames
	 *            the number of frames that should be read (can't be greater than
	 *            data.length - startIndex)
	 * @webref sound
	 */
	public void read(int startFrame, float[] data, int startIndex, int numFrames) {
		if (this.checkStartFrame(startFrame)) {
			if (startFrame + numFrames < this.frames()) {
				this.sample.read(startFrame, data, startIndex, numFrames);
			} else {
				// overflow at end of sample, need to do two partial copies
				int firstReadLength = this.frames() - startFrame;
				this.sample.read(startFrame, data, startIndex, firstReadLength);
				this.sample.read(0, data, startIndex + firstReadLength, numFrames - firstReadLength);
			}
		}
	}

	/**
	 * @param index
	 *            the index of the single frame of the audiosample that should be
	 *            read and returned as a float
	 * @return the value of the audio sample at the given frame
	 */
	public float read(int index) {
		// TODO catch exception and print understandable error message
		return (float) this.sample.readDouble(index);
	}

	/**
	 * Overwrite the sample with the data from the given array. The array can
	 * contain up to as many floats as there are frames in this sample.
	 * 
	 * @param data
	 *            the array from which the sample data should be taken
	 */
	public void write(float[] data) {
		if (data.length != this.frames()) {
			Engine.printWarning(
					"the length of the given array does not match the number of frames of this audio sample");
		}
		this.sample.write(data);
	}

	/**
	 * Write some frames of this audio sample.
	 *
	 * @param startFrame
	 *            the index of the first frame of the audiosample that should be
	 *            written to
	 * @param startIndex
	 *            the position in the array that the first value to write should be
	 *            taken from (typically 0)
	 * @param numFrames
	 *            the number of frames that should be written (can't be greater than
	 *            data.length - startIndex)
	 * @webref sound
	 */
	public void write(int startFrame, float[] data, int startIndex, int numFrames) {
		if (this.checkStartFrame(startFrame)) {
			if (startFrame + numFrames < this.frames()) {
				this.sample.write(startFrame, data, startIndex, numFrames);
			} else {
				// overflow at end of sample, need to do two partial copies
				int firstReadLength = this.frames() - startFrame;
				this.sample.write(startFrame, data, startIndex, firstReadLength);
				this.sample.write(0, data, startIndex + firstReadLength, numFrames - firstReadLength);
			}

		}
	}

	/**
	 * @param index
	 *            the index of the single frame of the audiosample that should be
	 *            set to the given value
	 * @param value
	 *            the float value that the given audio frame should be set to
	 */
	public void write(int index, float value) {
		if (this.checkStartFrame(startFrame)) {
			this.sample.writeDouble(index, value);
		}
	}
}
