package rx.list;

public interface Filter2<T, T2, E> {
    boolean filter(T item1, T2 item2, E item);
}
