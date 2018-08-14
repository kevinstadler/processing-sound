## GSoC 2018 work product report

This is the dedicated repository for the Google Summer of Code 2018 project *Complete overhaul of the [processing-sound](https://summerofcode.withgoogle.com/projects/#5133277640261632) library* for [The Processing Foundation](https://summerofcode.withgoogle.com/organizations/4915113891463168/). As the library is a complete rewrite of the [original Processing sound library](https://github.com/processing/processing-sound-archive), all of the code in this repository was contributed by myself as part of Google Summer of Code. The JavaDoc reference for all public classes and methods of the library can be found [here](https://kevinstadler.github.io/processing-sound/docs/). On top of the code, I've also committed several updates to the `processing-docs` online reference ([see commit log](https://github.com/processing/processing-docs/commits?author=kevinstadler)).

Pending minor revisions, the library will be adopted as Processing's default Sound library by the end of August 2018, which will also see the code transferred over to The Processing Foundation's [official stable repository location](https://github.com/processing/processing-sound). The present repository with full commit logs will remain here as a record of the work done by me over the summer.

## Project summary

The goal of the project was to create a simple sound synthesis library for beginners with the [exact same API as the original Processing Sound library](https://www.processing.org/reference/libraries/sound/index.html), but based on a full Java synthesis stack to improve support for [Android](http://android.processing.org/) and [Raspberry Pi](https://pi.processing.org/).

### How to build

1. `git clone git@github.com:kevinstadler/processing-sound.git`
2. into the `library/` folder copy (or soft-link) your Processsing's `core.jar` (and, optionally, also your Android SDK's `android.jar`, API level 26 or higher)
3. `ant dist` (or, alternatively, run build.xml from within Eclipse)

The resulting `processing-sound.zip` can be extracted into your Processing installation's `libraries/` folder.

Javadoc documentation of all currently implemented classes and methods is available [here](https://kevinstadler.github.io/processing-sound/docs/).

### Dependencies (downloaded automatically by Ant)

* [JSyn](http://www.softsynth.com/jsyn/) (Apache v2)
* [JavaMP3](https://github.com/kevinstadler/JavaMP3) (MIT)

### Links
* [GSoC project description](https://summerofcode.withgoogle.com/projects/#5133277640261632)
* [Processing Sound library API](https://www.processing.org/reference/libraries/sound/index.html)
* [Current up-to-date JavaDoc of this library rewrite](https://kevinstadler.github.io/processing-sound/)
* [Processing discourse thread](https://discourse.processing.org/t/early-builds-of-the-new-processing-sound-library-available-for-testing/1796)
* [Sound tutorial](https://www.processing.org/tutorials/sound/) from Extension 3 of [*Processing* (2nd edition)](https://www.processing.org/handbook)
* [JSyn javadocs](http://www.softsynth.com/jsyn/docs/javadocs/)
* [Sonia](http://sonia.pitaru.com) (JSyn-based sound library for Processing V90, discontinued)
* [JSyn on Raspberry Pi](https://stackoverflow.com/questions/34333777/no-sound-output-when-running-jsyn-on-raspberry-pi-raspbian-jessie)

## License

LGPL v2.1
