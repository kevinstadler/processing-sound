package processing.sound;

import com.jsyn.ports.UnitOutputPort;

import processing.core.PApplet;

/**
 * This is a Fast Fourier Transform (FFT) analyzer. It calculates the normalized
 * power spectrum of an audio stream the moment it is queried with the analyze()
 * method.
 * 
 * @webref sound
 * @param parent
 *            PApplet: typically use "this"
 * @param bands
 *            Number of frequency bands for the FFT as an integer, default: 512.
 *            This parameter needs to be a power of 2 (e.g. 16, 32, 64, 128,
 *            ...).
 **/
public class FFT extends Analyzer {

	public float[] spectrum;

	private JSynFFT fft;

	public FFT(PApplet parent) {
		this(parent, 512);
	}

	// this is really the number of bins, NOT the fftSize
	public FFT(PApplet parent, int bands) {
		super(parent);
		if (bands < 0 || Integer.bitCount(bands) != 1) {
			// TODO throw RuntimeException?
			Engine.printError("number of FFT bands needs to be a power of 2");
		} else {
			// FFT buffer size is twice the number of frequency bands
			this.fft = new JSynFFT(2 * bands);
			this.spectrum = new float[bands];
		}
	}

	protected void removeInput() {
		this.fft.input.disconnectAll();
		this.input = null;
	}

	protected void setInput(UnitOutputPort input) {
		// superclass makes sure that input unit is actually playing, just connect it
		Engine.getEngine().add(this.fft);
		this.fft.input.connect(input);
		this.fft.start();
	}

	/**
	 * Calculates the current frequency spectrum from the input source, writes it
	 * into this FFT's `spectrum` array, and returns it.
	 *
	 * @return the current frequency spectrum of the input source. The array has as
	 *         many elements as this FFT analyzer's number of frequency bands
	 */
	public float[] analyze() {
		return this.analyze(this.spectrum);
	}

	/**
	 * Calculates the current frequency spectrum from the input source, writes it
	 * into the given array, and returns it.
	 *
	 * @param value
	 *            an array with as many elements as this FFT analyzer's number of
	 *            frequency bands
	 * @webref sound
	 **/
	public float[] analyze(float[] value) {
		if (this.input == null) {
			Engine.printWarning("this FFT has no sound source connected to it, nothing to analyze");
		}
		this.fft.calculateMagnitudes(value);
		return value;
	}
}
