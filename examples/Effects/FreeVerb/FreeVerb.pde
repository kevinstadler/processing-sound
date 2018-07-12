/*
Play a sound sample and apply a reverb filter to it, changing the effect
parameters based on the mouse position.

With the mouse pointer at the top of the sketch you'll only hear the "dry"
(unprocessed) signal, move the mouse downwards to add more of the "wet"
reverb signal to the mix. The left-right position of the mouse controls the
"room size" and damping of the effect, with a smaller room (and more refraction)
at the left, and a bigger (but more dampened) room towards the right.
*/

import processing.sound.*;

SoundFile soundfile;
Reverb reverb;

void setup() {
    size(640,360);
    background(255);

    //Load a soundfile
    soundfile = new SoundFile(this, "vibraphon.aiff");

    // create a Delay Effect
    reverb = new Reverb(this);

    // Play the file in a loop
    soundfile.loop();

    // Set soundfile as input to the reverb 
    reverb.process(soundfile);
}      


void draw() {
    // change the roomsize of the reverb
    reverb.room(map(mouseX, 0, width, 0, 1.0));

    // change the high frequency dampening parameter
    reverb.damp(map(mouseX, 0, width, 0, 1.0));

    // change the wet/dry relation of the effect
    reverb.wet(map(mouseY, 0, height, 0, 1.0));
}
