/*
In this example, a WhiteNoise generator (equal amount of noise at all frequencies) is
passed through a BandPass filter. You can control both the central frequency (left/right)
as well as the bandwidth of the filter (up/down) with the mouse. The position and size
of the circle indicates how much of the noise's spectrum passes through the filter, and
at what frequency range.
*/

import processing.sound.*;

WhiteNoise noise;
BandPass bandPass;

void setup() {
    size(640,360);
    
    // Create the noise generator + Filter
    noise = new WhiteNoise(this);
    bandPass = new BandPass(this);
    
    noise.play(0.5);
    bandPass.process(noise);
}      

void draw() {
    float freq = map(mouseX, 0, width, 20, 10000);
    float bandwidth = map(mouseY, 0, height, 1000, 100);

    bandPass.freq(freq);
    bandPass.bw(bandwidth);

    background(255);
    noStroke();
    fill(255, 0, 150);
    ellipse(mouseX, height, 2*(height - mouseY), 2*(height - mouseY));
}
