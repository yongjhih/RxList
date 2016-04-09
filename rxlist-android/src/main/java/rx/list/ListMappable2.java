package rx.list;

import java.util.List;

public interface ListMappable2<T, T2, E> {
    E map(List<T> list, List<T2> list2, int index);
}
