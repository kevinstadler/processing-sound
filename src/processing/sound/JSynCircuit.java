package processing.sound;

import com.jsyn.ports.UnitOutputPort;
import com.jsyn.unitgen.Circuit;
import com.jsyn.unitgen.UnitFilter;
import com.jsyn.unitgen.UnitGenerator;

/**
 * Helper class wrapping a source unit generator, add/pan processor and effect into one circuit.
 */
class JSynCircuit extends Circuit {

	private UnitGenerator source;
	protected JSynProcessor processor = new JSynProcessor();
	protected Effect<? extends UnitFilter> effect;

	protected void setSource(UnitOutputPort input) {
		this.source = input.getUnitGenerator();
		this.add(this.source);

		if (input.getNumParts() == 2) {
			// stereo source - no need for pan, so bypass processor
			this.processor.output = input;
		} else {
			this.add(this.processor);
			this.processor.input.connect(input);
		}
	}

	protected UnitOutputPort getOutput() {
		if (this.effect == null) {
			return this.processor.output;
		} else {
			return this.effect.output;
		}
	}
}
