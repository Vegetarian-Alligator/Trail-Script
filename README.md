# Trail-Script

This is software that is meant to write choose-your-own adventure games, in multiplayer, as easy as possible. "Advanced" features (such as tracking data, for example hitpoints) are provided.  Limited graphical support is planned as well.

The project's language is designed to easily faciliate a block-like programming interface, inspired by MIT scratch.  (This interface is not yet implemented.)

Currently, the project is written in Java, and interfaced with TomCat. However, the vast majority of the project exists as a system outside of this interface, so porting it to a different interface - for example, single player command line, or a different websocket server - should be nearly as easy as setting up the alternative interface.
