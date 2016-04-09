package rx.list;

public interface Mappable<T, E> {
    E map(T item);
}
