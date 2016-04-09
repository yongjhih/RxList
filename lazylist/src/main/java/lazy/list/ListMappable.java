package lazy.list;

import java.util.List;

public interface ListMappable<T, E> {
    E map(List<T> data, int index);
}
