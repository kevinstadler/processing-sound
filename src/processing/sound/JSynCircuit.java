package processing.sound;

import com.jsyn.unitgen.Circuit;
import com.jsyn.unitgen.UnitFilter;
import com.jsyn.unitgen.UnitGenerator;
import com.jsyn.unitgen.UnitSource;
import com.jsyn.unitgen.VariableRateStereoReader;

/**
 * Helper class wrapping a source unit generator, add/pan processor and effect into one circuit.
 */
class JSynCircuit extends Circuit {

	private UnitGenerator source;
	protected JSynProcessor processor = new JSynProcessor();
	protected UnitFilter effect;

	public void setSource(UnitGenerator source) {
		this.source = source;
		this.add(source);

		if (source instanceof VariableRateStereoReader) {
			// bypass processor
			this.processor.output = ((VariableRateStereoReader) this.source).output;
		} else {
			this.add(this.processor);
			this.processor.input.connect(((UnitSource) this.source).getOutput());
		}
	}

}
