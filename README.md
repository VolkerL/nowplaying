# NowPlaying

NowPlaying is a simple application which tries to find the song you are currently playing and write that to a text file.

## Features

Currently we offer the following features:

- Query [Last.fm](http://www.last.fm) for your current playing track
- Define a template layout in which the track information will be inserted
- Define a message which should be written to the text file if no song is being played

## Usage

Download one of the releases from [GitHub](https://github.com/volkerl/nowplaying).
Unzip it and double-click the jar-file.

Note that the jar-file stores its current configuration to a file called `nowplaying.properties` right next to it.

Actually, it stores the properties file where the JVM running the jar file was started.
When opening the application by double-clicking this will be the directory where the jar file is located.

## Building

To build it from source, download the source and execute `mvn package`.
The jar-file will be located in `target/NowPlaying.jar`.

### Updating Licenses
To update the `LICENSE` and `THIRD-PARTY` files in both the base directory
and the `META-INF` folder, run

```
mvn license:update-project-license license:add-third-party
```

To add license headers on all java files run `mvn license:update-file-header`.

## Contact, Bug Tracker and Contributions

If you have any questions, remarks or bugs feel free to leave [an issue here on GitHub](https://github.com/volkerl/issues).

Contributions are very welcome. Use the standard git fork/pull-request mechanism.

## License

NowPlaying is licensed under the GNU General Public License, Version 3.0.
See [LICENSE](LICENSE) for the license.

We use several other libraries which should all be compatible with the GPLv3.
The licenses of the third party dependencies can be found in [THIRD-PARTY](THIRD-PARTY).
Please let me know if there are any licensing issues by creating an [issue here on GitHub](https://github.com/volkerl/nowplaying/issues).
