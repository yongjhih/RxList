package lazy.list;

public interface Mappable<T, E> {
    E map(T item);
}
