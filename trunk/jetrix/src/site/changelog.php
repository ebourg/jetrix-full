<? include("header.inc.php") ?>
      
<h1>Changelog</h1>

<pre>
Changes in version 0.0.10 (2002-09-28)
--------------------------------------
- implemented the client repository
- nickname uniqueness is now checked on logging
- incomming clients are now rejected when the server is full
- implemented the pluggable command system
- implemented the /who command
- implemented the /tell command (/msg and /cmsg are aliases)
- implemented the /op command
- implemented the /kick command
- implemented the /broadcast command
- implemented the /time command
- implemented the /motd command
- implemented the /emote command
- commands can now be invoked using their partial name (/ver, /t, etc...)
- reduced server startup time
- clients are now properly disconnected on server shutdown

Changes in version 0.0.9 (2002-06-23)
-------------------------------------
- improved channel switching
- implemented the game pause
- the end of the game in now detected
- improved the configuration system
- implemented the channel filter system
- filter: spam blocker
- filter: game auto-start when players say "go"
- filter: special block multiplier
- added the /conf command to display the channel settings
- added server log files
- added a debug mode (run JetriX with the -Djetrix.debug=true parameter)
- now displaying a message upon player disconnection
- implemented a special block check to prevent forged messages to crash clients
- added the /version command
- added a source distribution
- added a more unix friendly .tar.gz distribution

Changes in version 0.0.8 (2002-03-26)
-------------------------------------
- added the configuration file config.xml
- implemented multi-channel
- added the /list and /join commands

</pre>

<? include("footer.inc.php") ?>