package processing.sound;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.jsyn.data.FloatSample;
import com.jsyn.util.SampleLoader;

import fr.delthas.javamp3.Sound;
import processing.core.PApplet;

// calls to amp(), pan() etc affect both the LAST initiated and still running sample, AND all subsequently started ones
/**
 * This is a Soundfile player which allows to play back and manipulate sound files.
 * Supported formats are: WAV, AIF/AIFF, and MP3.
 * @webref sound
 * @param parent PApplet: typically use "this"
 * @param path Filename of the sound file to be loaded
 **/
public class SoundFile extends AudioSample {

	// array of UnitVoices each with a VariableRateStereoReader in
	//	private static VoiceAllocator PLAYERS = new VoiceAllocator(null);

	private static Map<String,FloatSample> SAMPLECACHE = new HashMap<String,FloatSample>();

	// the original library only printed an error if the file wasn't found,
	// but then later threw a NullPointerException when trying to play() the file.
	// this implementation will already throw an Exception upon failing to load.
	public SoundFile(PApplet parent, String path) {
		super(parent);

		this.sample = SoundFile.SAMPLECACHE.get(path);

		if (this.sample == null) {
			InputStream fin = parent.createInput(path);

			// if PApplet.createInput() can't find the file or URL, it prints
			// an error message and fin returns null. In this case we can just
			// return this dysfunctional SoundFile object without initialising further
			if (fin == null) {
				Engine.printError("unable to find file " + path);
				return;
			}

			try {
				// load WAV or AIF using JSyn
				this.sample = SampleLoader.loadFloatSample(fin);
			} catch (IOException e) {
				// try parsing as mp3
				try {
					Sound mp3 = new Sound(fin);
					try {
						ByteArrayOutputStream os = new ByteArrayOutputStream();
						// TODO make decoding asynchronous with a FutureTask<FloatSample>
						// this call is expensive
						mp3.decodeFullyInto(os);
						float data[] = new float[os.size() / 2];
						SampleLoader.decodeLittleI16ToF32(os.toByteArray(), 0, os.size(), data, 0);
						this.sample = new FloatSample(data, mp3.isStereo() ? 2 : 1);
					} catch (IOException ee) {
						throw ee;
					} finally {
						mp3.close();
					}
				} catch (IOException ee) {
					Engine.printError("unable to decode sound file " + path);
					// return dysfunctional SoundFile object
					return;
				}
			}
			SoundFile.SAMPLECACHE.put(path, this.sample);
		}
		this.initiatePlayer();
	}

	// Below are just duplicated methods from the AudioSample superclass which
	// are required for the reference to build the corresponding pages.

	/**
	 * Cues the playhead to a fixed position in the soundfile.
	 * @webref sound
	 * @param time Position to start from in seconds.
	 **/
	public void cue(float time) {
		super.cue(time);
	}

	/**
	 * Returns the duration of the soundfile in seconds.
	 * @webref sound
	 * @return The duration of the soundfile in seconds.
	 **/
	public float duration() {
		return super.duration();
	}

	/**
	 * Returns the number of frames of this soundfile.
	 * @webref sound
	 * @return The number of frames of this soundfile.
	 * @see duration()
	 **/
	public int frames() {
		return super.frames();
	}

	/**
	 * Starts playback which will loop at the end of the soundfile.
	 * @webref sound
	 **/	
	public void loop() {
		super.loop();
	}
	// TODO also need to duplicate other superclass signatures to be included in online reference?
	// e.g.:
//	public void loop(float rate, float pos, float amp, float add, float cue) {
//		super.loop(rate, pos, amp, add, cue);
//	}

	/**
	 * Jump to a specific position in the soundfile while continuing to play.
	 * @webref sound
	 * @param time Position to jump to as a float in seconds.
	 **/
	public void jump(float time) {
		super.jump(time);
	}

	/**
	 * Stop the playback of the file, but cue it to the current position so that
	 * the next call to play() will continue playing where it left off.
	 * @see stop
	 * @webref sound
	 */
	public void pause() {
		super.pause();
	}

	/**
	 * Starts the playback of the soundfile. Only plays the soundfile once.
	 * @webref sound
	 **/
	public void play() {
		super.play();
	}
	// TODO also need to duplicate other superclass signatures to be included in online reference?

	/**
	 * Set the playback rate of the soundfile.
	 * @webref sound
	 **/
	public void rate(float rate) {
		super.rate(rate);
	}

	/**
	 * Stops the playback.
	 * @see pause
	 * @webref sound
	 **/
	public void stop() {
		super.stop();
	}

}
