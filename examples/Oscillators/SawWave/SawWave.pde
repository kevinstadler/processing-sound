/**
 * This is a saw wave oscillator. The method .play() starts the oscillator. There
 * are several setter functions for configuring the oscillator, such as .amp(),
 * .freq(), .pan() and .add(). If you want to set all of them at the same time you can
 * use .set(float freq, float amp, float add, float pan)
 */

import processing.sound.*;

SawOsc saw;

void setup() {
  size(640, 360);
  background(255);

  // Create and start the saw oscillator.
  saw = new SawOsc(this);
  saw.play();
}

void draw() {
  // Map mouseY from 0.0 to 1.0 for amplitude
  saw.amp(map(mouseY, 0, height, 1.0, 0.0));

  // Map mouseX from 20Hz to 1000Hz for frequency  
  saw.freq(map(mouseX, 0, width, 20.0, 1000.0));

  // Map mouseX from -1.0 to 1.0 for left to right 
  saw.pan(map(mouseX, 0, width, -1.0, 1.0));
}
