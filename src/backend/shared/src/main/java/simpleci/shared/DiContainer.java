package simpleci.shared;

import java.util.HashMap;
import java.util.Map;

public class DiContainer {
    private Map<String, Object> services = new HashMap<>();

    public void add(String name, Object service) {
        if(services.containsKey(name)) {
            throw new RuntimeException(String.format("Service %s already in container", name));
        }
        services.put(name, service);
    }

    public <T> T get(String name, Class<T> clazz) {
        if(!services.containsKey(name)) {
            throw new RuntimeException(String.format("Service %s does not registered in container", name));
        }
        Object service = services.get(name);
        if(!clazz.isAssignableFrom(service.getClass())) {
            throw new RuntimeException(String.format("%s must me instance of %s", name, clazz.getName()));
        }
        return (T) service;
    }
}
