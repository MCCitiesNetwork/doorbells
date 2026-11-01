# Sound compatibility regression

On modern Bukkit, `Sound` is an interface. Guava `Enums.getIfPresent` throws
`ClassCastException: interface org.bukkit.Sound not an enum` on a doorbell press.
The fix resolves named constants through public fields; modern constants, legacy
aliases, unsupported values and the existing cache keep their behavior.

With JDK 25 and an existing Paper 26.3 build 206 server library layout:

```sh
JAVA_HOME=/path/to/jdk25 python3 tests/run.py /path/to/server /new/output/directory
```

The script compiles all plugin sources, executes the regression against the real
Paper Sound interface (with simulated registry storage), and packages only plugin
classes plus `plugin.yml` and `config.yml` as `Doorbells.jar`. No licensed plugin
JAR or server state is required. The server is never started or modified.

Checks cover all four configured note-block tones, repeated cached lookups,
a missing modern name with a supported legacy alias, and unsupported sounds.
`--add-opens` is used only by the test to simulate missing enum names; the plugin
does not require it. Optionally pass the original Doorbells JAR as a third argument to reproduce
the exact enum failure before running the fixed probe. The original source also
fails to compile against this API because Guava requires an enum type.

This does not test audible playback, command registration, or player interactions
inside a running server. The compiled plugin requires Java 25.
