package processing.sound;

import com.jsyn.data.Spectrum;
import com.jsyn.ports.UnitOutputPort;
import com.jsyn.unitgen.SpectralFFT;

import processing.core.PApplet;

/**
 * This is a Fast Fourier Transform (FFT) analyzer. It calculates the normalized
 * power spectrum of an audio stream the moment it is queried with the analyze()
 * method.
 * 
 * @webref sound
 * @param parent
 *            PApplet: typically use "this"
 * @param fftsize
 *            Size of FFT bandwidth in integers (default 512)
 **/
public class FFT extends Analyzer {

	public float[] spectrum;

	private SpectralFFT fft;

	public FFT(PApplet parent) {
		this(parent, 512);
	}

	// this is really the number of bins, NOT the fftSize
	public FFT(PApplet parent, int fftSize) {
		super(parent);
		if (fftSize < 0 || Integer.bitCount(fftSize) != 1) {
			// TODO throw RuntimeException
			Engine.printError("number of FFT bands needs to be a power of 2");
		} else {
			// add one to increase fftSize by one power to get desired number
			// of bins (see github issue #7)
			int log2 = 1 + Integer.numberOfTrailingZeros(fftSize);
			this.fft = new SpectralFFT(log2);
			// this.fft.setWindow(SpectralWindowFactory.getHannWindow(log2));
			this.spectrum = new float[fftSize];
		}
	}

	protected void setInput(UnitOutputPort input) {
		// superclass makes sure that input unit is actually playing, just connect it
		Engine.getEngine().add(this.fft);
		this.fft.input.connect(input);
		this.fft.start();
	}

	public void analyze() {
		this.analyze(this.spectrum);
	}

	/**
	 * Queries a value from the analyzer and returns a vector the size of the
	 * pre-defined number of bands.
	 * 
	 * @webref sound
	 **/
	public void analyze(float[] value) {
		Spectrum s = this.fft.output.getSpectrum();
		int bins = value.length;
		if (s.size() != 2 * bins) {
			Engine.printWarning("target array is not the same size as the number of frequency bins of the FFT");
			bins = Math.min(s.size() / 2, value.length);
		}
		for (int i = 0; i < bins; i++) {
			// index+1 to skip over DC bin
			// multiply by 2 to achieve same amplitude scale as original library
			value[i] = (float) (2 * Math.sqrt(Math.pow(s.getReal()[i + 1], 2) + Math.pow(s.getImaginary()[i + 1], 2)));
		}
	}
}
