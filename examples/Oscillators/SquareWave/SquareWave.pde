/**
 * This is a square wave oscillator. The method .play() starts the oscillator. There
 * are several setter functions for configuring the oscillator, such as .amp(),
 * .freq(), .pan() and .add(). If you want to set all of them at the same time you can
 * use .set(float freq, float amp, float add, float pan)
 */

import processing.sound.*;

SqrOsc sqr;

void setup() {
  size(640, 360);
  background(255);

  // create and start the oscillator.
  sqr = new SqrOsc(this);
  sqr.play();
}

void draw() {
  // map mouseY from 0.0 to 1.0 for amplitude
  sqr.amp(map(mouseY, 0, height, 1.0, 0.0));

  // map mouseX from 20Hz to 1000Hz for frequency  
  sqr.freq(map(mouseX, 0, width, 80.0, 200.0));

  // map mouseX from -1.0 to 1.0 for left to right 
  sqr.pan(map(mouseX, 0, width, -1.0, 1.0));
}
