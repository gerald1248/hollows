Hollows
=======

Note that development of the game continues at [gerald1248/hollows-ng](https://github.com/gerald1248/hollows-ng).

<img src="screenshots/screenshot.png" height="400" alt="Hollows screenshot"/>

`Hollows` is an arcade game in the style of the 80s. Your task is to rescue settlers from a sequence of caves known as the Hollows. If you have an Android device or compatible Chromebook, you can download it from the [Play Store](https://play.google.com/store/apps/details?id=gerald1248.hollows).

Here's a [playthrough of the first level](https://www.youtube.com/watch?v=R0ldZ9svasA).

Gameplay is optimized for public transport and open spaces:

* startup time is short
* strictly offline
* battery use is moderate
* levels are brief
* contrast is high for open air use
* audio is off until you turn it on

In terms of UI, `Hollows` breaks with arcade conventions in that it

* doesn't use buttons
* supports multitouch
* considers left-handed users

It recreates the keyboard controls of the 1986 Firebird classic Thrust using touch interactions.

The original offers five keyboard commands:

* `A`: rotate anti-clockwise
* `S`: rotate clockwise
* `Shift`: thrust
* `Return`: fire
* `Space`: shield

`Hollows` replaces these interactions with the following:

* swipe left: rotate anti-clockwise
* swipe right: rotate clockwise
* long press: thrust
* short press: fire

The 'shield' interaction of the original is missing. It has been replaced with a proximity trigger (for 'pick up'), but the 'protect' aspect has been dropped.

While it is possible to play `Hollows` without multitouch, it is much easier to use one finger for rotation and another to apply thrust. For increased thrust, place two or more fingers on the screen.

![Touch controls](plantuml/ui.png?raw=true "Touch controls")

Sadly there is no Rob Hubbard title music.

Make and share your own levels
------------------------------
`Hollows` levels are ASCII art in a simple 50&times;50 grid. If you've created a new level, please consider sharing it by submitting a pull request. Take a look at the [existing levels](app/src/main/res/values/levels.xml).

Acknowledgements
----------------
ImpulseEngine 2D library [github.com/RandyGaul/ImpulseEngine](https://github.com/RandyGaul/ImpulseEngine/)) by @randypgaul.

Java port [github.com/ClickerMonkey/ImpulseEngine](https://github.com/ClickerMonkey/ImpulseEngine/) by Philip Diffenderfer.

PressStart2P font [fontlibrary.org/en/font/press-start-2p](https://fontlibrary.org/en/font/press-start-2p/) by Cody &ldquo;CodeMan38&rdquo; Boisclair.

Loops created in Smasher: [smasher.sourceforge.net](http://smasher.sourceforge.net).

Original sample "Synth loop" by LS: [www.freesound.org](http://www.freesound.org/samplesViewSingle.php?id=66381).
