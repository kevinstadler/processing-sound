## processing-sound overhaul

This is the work-in-progress repository of Google Summer of Code 2018 project *Complete overhaul of the [processing-sound](https://github.com/processing/processing-sound) library*.

The goal of the project is to create a simple sound synthesis library for beginners with the [exact same API as the original Processing Sound library](https://www.processing.org/reference/libraries/sound/index.html), but based on a full Java synthesis stack to improve support for [Android](http://android.processing.org/) and [Raspberry Pi](https://pi.processing.org/).

## how to build

1. `git clone git@github.com:kevinstadler/processing-sound.git`
2. into the `library/` folder copy (or soft-link) your Processsing's `core.jar` (and, optionally, also your Android SDK's `android.jar`)
3. `ant dist` (or, alternatively, run build.xml from within Eclipse)

The resulting `processing-sound.zip` can be extracted into your Processing installation's `libraries/` folder.

Javadoc documentation of all currently implemented classes and methods is available [here](https://kevinstadler.github.io/processing-sound/).

### dependencies (downloaded automatically by Ant)

* [JSyn](http://www.softsynth.com/jsyn/) (Apache v2)
* [JavaMP3](https://github.com/kevinstadler/JavaMP3) (MIT)

### links
* [GSoC project description](https://summerofcode.withgoogle.com/projects/#5133277640261632)
* [Processing Sound library API](https://www.processing.org/reference/libraries/sound/index.html)
* [Sound tutorial](https://www.processing.org/tutorials/sound/) from Extension 3 of [*Processing* (2nd edition)](https://www.processing.org/handbook)
* [JSyn javadocs](http://www.softsynth.com/jsyn/docs/javadocs/)
* [Sonia](http://sonia.pitaru.com) (JSyn-based sound library for Processing V90, discontinued)
* [JSyn on Raspberry Pi](https://stackoverflow.com/questions/34333777/no-sound-output-when-running-jsyn-on-raspberry-pi-raspbian-jessie)

## license

LGPL v2.1