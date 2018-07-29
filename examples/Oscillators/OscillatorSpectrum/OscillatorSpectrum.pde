/**
 * Inspect the frequency spectrum of different simple oscillators.
 */

import processing.sound.*;

Oscillator oscs[] = new Oscillator[4];
int current = 0;

FFT fft;
int fftBands = 512;

void setup() {
  size(640, 360);
  background(255);

  // create the oscillators
  oscs[0] = new SinOsc(this);
  oscs[1] = new TriOsc(this);
  oscs[2] = new SqrOsc(this);

  Pulse pulse = new Pulse(this);
  pulse.width(0.5);
  oscs[3] = pulse;

  fft = new FFT(this, 512);

  oscs[current].play();
  fft.input(oscs[current]);
}

void draw() {
  // Map mouseX from 20Hz to 2000Hz for frequency  
  float frequency = map(mouseX, 0, width, 20.0, 2000.0);

  // Only play one of the four oscillators, based on mouseY
  int nextOscillator = floor(map(mouseY, 0, height, 0, 4));

  if (nextOscillator != current) {
    oscs[current].stop();

    fft.input(oscs[nextOscillator]);
    oscs[nextOscillator].play();
    current = nextOscillator;
  }

  oscs[current].freq(frequency);

  // Draw frequency spectrum.
  background(125, 255, 125);
  fill(255, 0, 150);
  noStroke();

  fft.analyze();

  float r_width = width/float(fftBands);

  for (int i = 0; i < fftBands; i++) {
    rect( i*r_width, height, r_width, -fft.spectrum[i]*height);
  }
}
