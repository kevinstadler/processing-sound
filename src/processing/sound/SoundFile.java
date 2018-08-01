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
					// TODO do what?
				}
			}
			SoundFile.SAMPLECACHE.put(path, this.sample);
		}
		this.initiatePlayer();
	}

}
