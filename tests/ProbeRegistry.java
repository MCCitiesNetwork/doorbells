import io.papermc.paper.registry.*;
import java.lang.reflect.Proxy;
import org.bukkit.*;

/** Only registry storage is simulated; Sound and xsound are the installed API/plugin classes. */
public class ProbeRegistry implements RegistryAccess {
    public <T extends Keyed> Registry<T> getRegistry(Class<T> type) { return registry(); }
    public <T extends Keyed> Registry<T> getRegistry(RegistryKey<T> key) { return registry(); }
    @SuppressWarnings("unchecked")
    private <T extends Keyed> Registry<T> registry() {
        return (Registry<T>) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{Registry.class}, (proxy, method, args) -> {
            if (method.getName().equals("getOrThrow") || method.getName().equals("get")) {
                String key = args[0].toString();
                return Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{Sound.class}, (sound, m, a) -> switch(m.getName()) {
                    case "getKey" -> NamespacedKey.fromString(key);
                    case "toString", "name" -> key;
                    case "hashCode" -> System.identityHashCode(sound);
                    case "equals" -> sound == a[0];
                    default -> throw new UnsupportedOperationException(m.getName());
                });
            }
            throw new UnsupportedOperationException(method.getName());
        });
    }
}
