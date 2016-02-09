# NowPlaying

NowPlaying is a simple application which tries to find the song you are currently playing and write that to a text file.

## Features

Currently we offer the following features:

- Query [Last.fm](http://www.last.fm) for your current playing track
- Define a template layout in which the track information will be inserted
- Define a message which should be written to the text file if no song is being played

## Usage

Download one of the [releases from GitHub](https://github.com/volkerl/nowplaying/releases).
Make sure to download the jar file unless you want to build from source.

Note: please make sure you don't use the old 1.0 version. It has a nasty bug where your template isn't actually changeable. That makes it quite useless, so please download a newer version if you have the old 1.0!

Now just double-click the jar file and the application will start.

#### Configure Last.fm

The first thing you have to configure is the Last.fm information.
Just fill in your Last.fm username (not case sensitive).

The next field determines how often we will ask Last.fm to provide us with the current track.
By default we query the Last.fm API every 5 seconds.
Please make sure you do not lower it too much, we wouldn't want to spam them.

For now just ignore the "advanced configuration" options.

#### Write a template

The next part is where you specify how your song information should be printed in the text file.
You specify a template in which the application will insert the actual information of the current playing song.
You can write the template in the big white text area, and you can see what it would look like in the yellow preview field.
The default template should make it pretty obvious how it works.

To get actual track information inserted in your template, you can use tags.
A tag is simply the name of the information you want to have inserted, wrapped in curly braces.
For example `{name}` is the name of the current song.
The tags that are available are listed in the list on the right of the text area.
Recognized tags will be highlighted so you know that you spelled it correctly.

Next we have the message that will be written to the text file if you are not listening to any songs
(or when we fail to retrieve the information).
You can leave it blank if you just want the file to be empty in that case.

#### Pick an output file

Now we can pick an output file where the current song will be written to.
You can either type it by hand or use the browse button.
The browse window can be very limited and does not allow you to make a new file.
It should allow you to at least select a folder.
Then just manually append the name of the file to the text field.

#### Start listening

Now that everything is set up the way you want it to be you can start listening.
Just press the big red button and it should turn to green.
As long as the button is green your track info should be automatically updated and written to the output file you specified.
To stop the updating process, just click the green button and it will turn off again.

#### Saving
You can save your current settings by pressing the save button.
Note that NowPlaying stores these configuration settings in a file called `nowplaying.properties` right next to itself (the `NowPlaying.jar` file).

Actually, it stores the properties file where the JVM running the jar file was started.
When opening the application by double-clicking this will be the directory where the jar file is located.

Either way, as long as you start the application the same way, it will be able to find the saved settings.

## Known limitations

Currently we only recognize current playing tracks if [Last.fm](http://last.fm) actually marks the song as `nowplaying=true`. This, for some reason, does not happen when scrobbling from [HypeMachine](http://hypem.com).

HypeMachine has indicated that they have no intention of fixing this issue.
However, I made a Chrome extension called NowPlaying - HypeMachine that will do the proper scrobbling for you.
You can install it through the chrome store by clicking [here](https://chrome.google.com/webstore/detail/nowplaying-hypemachine/dadplfmhpmchkhbhanoeaeagojlpafab).
Or you can check out the [code](https://github.com/volkerl/nowplaying-hypemachine) if you are interested.

## Building

To build it from source, download the source and execute `mvn package`.
The jar-file will be located in `target/NowPlaying.jar`.

#### Updating Licenses
To update the `LICENSE` and `THIRD-PARTY` files in both the base directory
and the `META-INF` folder, run

```
mvn license:update-project-license license:add-third-party
```

To add license headers on all java files run `mvn license:update-file-header`.

## Contact, Bug Tracker and Contributions

If you have any questions, remarks or bugs feel free to leave [an issue here on GitHub](https://github.com/volkerl/nowplaying/issues).

Contributions are very welcome. Use the standard git fork/pull-request mechanism.

## License

NowPlaying is licensed under the GNU General Public License, Version 3.0.
See [LICENSE](LICENSE) for the license.

We use several other libraries which should all be compatible with the GPLv3.
The licenses of the third party dependencies can be found in [THIRD-PARTY](THIRD-PARTY).
Please let me know if there are any licensing issues by creating an [issue here on GitHub](https://github.com/volkerl/nowplaying/issues).
