import me.flame.doorbells.xsound;
import org.bukkit.Sound;
import java.lang.reflect.*;
import com.google.common.base.Optional;

public class Probe {
    public static void main(String[] args) throws Exception {
        if (Sound.class.isEnum()) throw new AssertionError("Expected current interface API");
        for (String name : new String[]{"BLOCK_NOTE_BLOCK_FLUTE", "BLOCK_NOTE_BLOCK_BELL", "BLOCK_NOTE_BLOCK_CHIME", "BLOCK_NOTE_BLOCK_HARP"}) {
            xsound entry = xsound.valueOf(name);
            Object expected = Sound.class.getField(name).get(null);
            if (entry.parseSound() != expected || entry.parseSound() != expected)
                throw new AssertionError("Wrong sound/cache: " + name);
        }
        Method lookup = xsound.class.getDeclaredMethod("findSound", String.class);
        lookup.setAccessible(true);
        if (((Optional<?>)lookup.invoke(null, "NOT_A_REAL_SOUND")).isPresent())
            throw new AssertionError("Unknown sound must be absent");
        // Force a missing modern name with a supported legacy alias; verify fallback and cache.
        xsound legacy = xsound.valueOf("AMBIENT_CAVE");
        Field name = Enum.class.getDeclaredField("name"); name.setAccessible(true);
        name.set(legacy, "NOT_A_REAL_SOUND");
        Field aliases = xsound.class.getDeclaredField("legacy"); aliases.setAccessible(true);
        aliases.set(legacy, new String[]{"MISSING_ALIAS", "BLOCK_NOTE_BLOCK_BELL"});
        if (legacy.parseSound() != Sound.BLOCK_NOTE_BLOCK_BELL || legacy.parseSound() != Sound.BLOCK_NOTE_BLOCK_BELL)
            throw new AssertionError("Legacy fallback/cache broken");
        xsound missing = xsound.valueOf("AMBIENT_UNDERWATER_ENTER");
        name.set(missing, "ALSO_MISSING"); aliases.set(missing, new String[]{"MISSING_ALIAS"});
        if (missing.parseSound() != null || missing.parseSound() != null)
            throw new AssertionError("Unsupported sound/cache broken");
        System.out.println("PASS actual Sound interface: four configured tones, cache, legacy fallback, unsupported sounds");
    }
}
