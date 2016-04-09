package rx.list;

public interface Mappable2<T, T2, E> {
    E map(T item, T2 item2);
}
