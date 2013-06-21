# NetLogo Custom Logging Extension

This extension allows you to write your own customized NetLogo logging entries.

## Building

Run `sbt package`.  If compilation succeeds, `custom-logging.jar` will be created.

## Using

The Custom Logging has just a handful of primitives.

* `log-message <msg>`
  * Writes to the logs the customized message `<msg>`.  A common use of it might be something like `custom-logging:log-message (word "This is the turtle count: " (count turtles))`.
* `log-globals <global-name>*`
  * Writes to the logs the values of each valid global variable whose name was supplied to the primitive as an argument.
* `log-all-globals`
  * Writes to the logs the values of all globals in the model.
* `log-all-globals-but`
  * Writes to the logs the values of all globals in the model but the ones whose names are supplied to the primitive as arguments.

## Terms of Use

[![CC0](http://i.creativecommons.org/p/zero/1.0/88x31.png)](http://creativecommons.org/publicdomain/zero/1.0/)

The NetLogo Custom Logging extension is in the public domain.  To the extent possible under law, Uri Wilensky has waived all copyright and related or neighboring rights.
