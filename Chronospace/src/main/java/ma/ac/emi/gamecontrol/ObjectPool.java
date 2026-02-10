package ma.ac.emi.gamecontrol;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ObjectPool<T> {

    private final List<T> freeObjects = new ArrayList<>();
    private final Supplier<T> creator;

    public ObjectPool(Supplier<T> creator, int initialSize) {
        this.creator = creator;
        for (int i = 0; i < initialSize; i++) {
            freeObjects.add(creator.get());
        }
    }

    public T obtain() {
        if (freeObjects.isEmpty()) {
            return creator.get();
        }
        return freeObjects.remove(freeObjects.size() - 1);
    }

    public void free(T object) {
        freeObjects.add(object);
    }
}
