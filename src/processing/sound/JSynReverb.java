package processing.sound;

import com.jsyn.engine.SynthesisEngine;
import com.jsyn.unitgen.Circuit;
import com.jsyn.unitgen.FilterAllPass;
import com.jsyn.unitgen.MixerMono;
import com.jsyn.unitgen.PassThrough;
import com.jsyn.unitgen.UnitFilter;

/**
 * A JSyn implementation of the classic Freeverb design.
 */
public class JSynReverb extends UnitFilter {

	private Circuit reverbCircuit;

	private int[] Ns = new int[] {1557, 1617, 1491, 1422, 1277, 1356, 1188, 1116};
	private JSynLBCF[] lbcfs = new JSynLBCF[Ns.length];
	private MixerMono mixer;

	public JSynReverb() {
		this.reverbCircuit = new Circuit();
		PassThrough in = new PassThrough();
		this.reverbCircuit.add(in);
		this.input = in.input;

		FilterAllPass ap = new FilterAllPass();
		this.reverbCircuit.add(ap);
		ap.gain.set(1.0 / Ns.length);
		
		for (int i = 0; i < Ns.length; i++) {
			this.lbcfs[i] = new JSynLBCF(0.84, 0.2, Ns[i]);
			this.reverbCircuit.add(this.lbcfs[i]);
			this.lbcfs[i].input.connect(in.output);

			// inputs get added automatically
			this.lbcfs[i].output.connect(ap.input);
		}

		this.mixer = new MixerMono(2);
		this.mixer.amplitude.set(1.0);
		this.setWet(0.5);

		in.output.connect(0, this.mixer.input, 0);
		ap.output.connect(0, this.mixer.input, 1);
		this.output = this.mixer.output;
	}

	@Override
    public void setSynthesisEngine(SynthesisEngine synthesisEngine) {
    	this.reverbCircuit.setSynthesisEngine(synthesisEngine);
    }

	@Override
	public void generate(int start, int limit) {
		// not called
	}

	protected void setDamp(double damp) {
		// TODO check parameter
		for (JSynLBCF lbcf : this.lbcfs) {
			lbcf.setD(damp);
		}
	}

	protected void setRoom(double room) {
		// TODO check parameter
		for (JSynLBCF lbcf : this.lbcfs) {
			lbcf.setF(room);
		}
	}

	protected void setWet(double wet) {
		// TODO check parameter
		this.mixer.gain.set(0, 1 - wet);
		this.mixer.gain.set(1, wet);
	}
}
