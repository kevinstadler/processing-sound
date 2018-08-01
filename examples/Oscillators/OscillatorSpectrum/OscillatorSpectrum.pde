/**
 * Inspect the frequency spectrum of different simple oscillators.
 */

import processing.sound.*;

// All oscillators are instances of the Oscillator superclass.
Oscillator oscs[] = new Oscillator[5];

// Store information on which of the oscillators is currently playing.
int current = 0;

FFT fft;
int fftBands = 512;

void setup() {
  size(640, 360);
  background(255);

  // create the oscillators
  oscs[0] = new SinOsc(this);
  oscs[1] = new TriOsc(this);
  oscs[2] = new SawOsc(this);
  oscs[3] = new SqrOsc(this);

  Pulse pulse = new Pulse(this);
  pulse.width(0.1);
  oscs[4] = pulse;

  fft = new FFT(this, 512);

  oscs[current].play();
  fft.input(oscs[current]);
}

void draw() {
  // Map mouseX from 20Hz to 22000Hz for frequency  
  float frequency = map(mouseX, 0, width, 20.0, 22000.0);

  // Only play one of the four oscillators, based on mouseY
  int nextOscillator = floor(map(mouseY, 0, height, 0, oscs.length));

  if (nextOscillator != current) {
    oscs[current].stop();
    current = nextOscillator;

    // Switch FFT analysis over to the newly selected oscillator.
    fft.input(oscs[current]);
    // Play (but don't make it too loud)
    oscs[current].play(frequency, 0.3);

  } else {
    // Still on the same oscillator, update frequency.
    oscs[current].freq(frequency);
  }

  // Draw frequency spectrum.
  background(125, 255, 125);
  fill(255, 0, 150);
  noStroke();

  fft.analyze();

  float r_width = width/float(fftBands);

  for (int i = 0; i < fftBands; i++) {
    rect( i*r_width, height, r_width, -fft.spectrum[i]*height);
  }

  // Display the name of the oscillator class.
  textSize(32);
  fill(0);
  float verticalPosition = map(current, -1, oscs.length, 0, height);
  text(oscs[current].getClass().getSimpleName(), 0, verticalPosition);
}
