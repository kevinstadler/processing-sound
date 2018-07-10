package processing.sound;

import com.jsyn.engine.SynthesisEngine;
import com.jsyn.unitgen.Circuit;
import com.jsyn.unitgen.InterpolatingDelay;
import com.jsyn.unitgen.MultiplyAdd;
import com.jsyn.unitgen.UnitFilter;

/**
 * A custom JSyn delay circuit with feedback.
 */
class JSynDelay extends UnitFilter {

	private Circuit reverbCircuit;

	private InterpolatingDelay delay = new InterpolatingDelay();
	private MultiplyAdd feedback = new MultiplyAdd();
	
	public JSynDelay() {
		super();
		this.reverbCircuit = new Circuit();
		this.reverbCircuit.add(this.delay);
		this.reverbCircuit.add(this.feedback);
		this.input = this.feedback.inputC;
		this.feedback.inputA.set(0.0);

		this.feedback.inputB.connect(this.delay.output);
		this.feedback.output.connect(this.delay.input);
		this.output = this.delay.output;
	}

	@Override
    public void setSynthesisEngine(SynthesisEngine synthesisEngine) {
    	super.setSynthesisEngine(synthesisEngine);
    	this.reverbCircuit.setSynthesisEngine(synthesisEngine);
    }

	public void generate(int start, int limit) {
		this.reverbCircuit.generate(start, limit);
	}

	protected void setDelayTime(float delayTime) {
		this.delay.delay.set(delayTime);
	}

	protected void setFeedback(float feedback) {
		// TODO check range
		this.feedback.inputA.set(feedback);
	}

	protected void setMaxDelayTime(float maxDelayTime) {
		int maxSamples = (int) (Engine.getEngine().getSampleRate() * maxDelayTime);
		this.delay.allocate(maxSamples);
	}
}
