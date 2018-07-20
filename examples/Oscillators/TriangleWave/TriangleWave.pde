/**
 * This is a triangle (or "saw") wave oscillator. The method .play() starts the
 * oscillator. There are several setter functions for configuring the oscillator, like
 * .amp(), .freq(), .pan() and .add(). If you want to set all of them at the same time
 * you can use .set(float freq, float amp, float add, float pan)
 */

import processing.sound.*;

TriOsc tri;

void setup() {
  size(640, 360);
  background(255);

  // create and start the triangle wave oscillator.
  tri = new TriOsc(this);
  tri.play();
}

void draw() {
  // map mouseY from 0.0 to 1.0 for amplitude
  tri.amp(map(mouseY, 0, height, 1.0, 0.0));

  // map mouseX from 20Hz to 1000Hz for frequency  
  tri.freq(map(mouseX, 0, width, 80.0, 1000.0));

  // map mouseX from -1.0 to 1.0 for left to right 
  tri.pan(map(mouseX, 0, width, -1.0, 1.0));
}
