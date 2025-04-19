package echoflux.core.core.queue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConcurrentBatchQueue<T> extends ConcurrentLinkedQueue<T> {

    public final static int UNLIMITED = -1;

    public Collection<T> pollAll() {
        if (isEmpty()) {
            return List.of();
        }

        var elements = new ArrayList<T>();

        while (true) {
            var element = poll();

            if (element == null) {
                break;
            }

            elements.add(element);
        }

        return Collections.unmodifiableCollection(elements);
    }

    public Collection<T> pollAll(int limit) {
        if (limit == 0 || isEmpty()) {
            return List.of();
        }
        if (limit == UNLIMITED) {
            return pollAll();
        }

        var elements = new ArrayList<T>();

        for (int i = 0; i < limit; i++) {
            var element = poll();

            if (element == null) {
                break;
            }

            elements.add(element);
        }

        return Collections.unmodifiableCollection(elements);
    }

}
