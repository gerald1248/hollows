Hollows
=======

<img src="screenshots/screenshot.png" height="400" alt=    "Hollows screenshot"/>

Hollows is a touch UI PoC that:

* doesn't use buttons
* supports multitouch
* considers left-handed users

It recreates the keyboard controls of the 1986 Firebird classic Thrust using only touch interactions.

The original offers five keyboard commands:

* `A`: rotate anti-clockwise
* `S`: rotate clockwise
* `Shift`: thrust
* `Return`: fire
* `Space`: shield

The PoC replaces these interactions with the following:

* swipe left: rotate anti-clockwise
* swipe right: rotate clockwise
* long press: thrust
* short press: fire

The 'shield' interaction of the original is missing. It would be possible to replace it with a proximity-trigger (for 'pick up') and an untimely demise (for 'protect').

While it is possible to use the interface without multitouch, it is much easier to use one finger for rotation and another to apply thrust. For increased thrust, place two or more fingers on the screen.

![Touch controls](plantuml/ui.png?raw=true "Touch controls")

Sadly there is no Rob Hubbard title music.

Acknowledgements
----------------
ImpulseEngine 2D library [github.com/RandyGaul/ImpulseEngine](https://github.com/RandyGaul/ImpulseEngine/)) by @randypgaul.

Java port [github.com/ClickerMonkey/ImpulseEngine](https://github.com/ClickerMonkey/ImpulseEngine/) by Philip Diffenderfer.

Loops created in Smasher: [smasher.sourceforge.net](http://smasher.sourceforge.net).

Original sample "Synth loop" by LS: [www.freesound.org](samplesViewSingle.php?id=66381).