package rx.list;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import java.util.LruCache;

/**
 * Mapper.
 *
 * Example:
 *
 * ```java
 * return new SimpleMapList<ParseNotification, Notification>(notifications) {
 *     @Override
 *     public Notification map(ParseNotification notification) {
 *         return SimpleParse.load(Notification.class, notification);
 *     }
 * };
 * ```
 */
public class SimpleMapList<T, E> extends ArrayList<E> implements ListMappable<T, E>, Mappable<T, E>, Filter<E> {
    private List<T> mList;
    protected Mappable<T, E> mMapper;
    protected ListMappable<T, E> mIndexMapper;
    protected Filter<E> mFilter;
    protected LruCache<Integer, E> mCache;

    public SimpleMapList() {
        super();
    }

    public SimpleMapList(List<T> list) {
        this(list, null, null);
    }

    public SimpleMapList(List<T> list, Mappable<T, E> mapper) {
        this(list, mapper, null);
    }

    public SimpleMapList(List<T> list, ListMappable<T, E> mapper) {
        this(list, null, mapper);
    }

    private SimpleMapList(List<T> list, Mappable<T, E> mapper, ListMappable<T, E> indexMapper) {
        super();

        mList = Collections.emptyList();
        if (list != null) {
            mList = list;
        }

        mMapper = mapper == null ? this : mapper;
        mIndexMapper = indexMapper == null ? this : indexMapper;
        mFilter = this;
        mCache = new LruCache<Integer, E>(1000);
    }

    @Override
    public E get(int index) {
        E ret = mCache.get(index);
        if (ret == null) {
            ret = mIndexMapper.map(mList, index);
            if (ret == null) {
                ret = mMapper.map(mList.get(index));
            }

            if (!mFilter.filter(ret)) {
                return null;
            }

            if (ret != null) {
                mCache.put(index, ret);
            }
        }
        return ret;
    }

    public E update(int index) {
        E ret = mIndexMapper.map(mList, index);
        if (ret == null) {
            ret = mMapper.map(mList.get(index));
        }

        if (ret != null) {
            mCache.put(index, ret);
        }
        return ret;
    }

    @Override
    public boolean filter(E item) {
        return true;
    }

    public SimpleMapList filter(Filter filter) {
        mFilter = filter;
        return this;
    }

    @Override
    public Object[] toArray() {
        int s = size();
        Object[] result = new Object[s];
        for (int i = 0; i < s; i++) {
            result[i] = get(i);
        }
        return result;
    }

    @Override
    public <T> T[] toArray(T[] contents) {
        int s = size();
        if (contents.length < s) {
            @SuppressWarnings("unchecked")
            T[] newArray = (T[]) Array.newInstance(contents.getClass().getComponentType(), s);
            contents = newArray;
        }

        for (int i = 0; i < s; i++) {
            contents[i] = (T) get(i);
        }

        if (contents.length > s) {
            contents[s] = null;
        }

        return contents;
    }

    @Override
    public int size() {
        return mList.size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public Iterator<E> iterator() {
        return listIterator();
    }

    @Override
    public ListIterator<E> listIterator() {
        return listIterator(0);
    }

    @Override
    public void clear() {
        mList = Collections.emptyList();
        mCache.evictAll();
    }

    public List<T> getData() {
        return getList();
    }

    public List<T> getList() {
        return mList;
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {
        if (!mList.isEmpty()) {
            return false;
        }

        if (!(collection instanceof MapList)) {
            return false;
        }

        mList = ((SimpleMapList) collection).getData();
        return true;
    }

    @Override
    public ListIterator<E> listIterator(final int i) {
        return new ListIterator<E>() {
            private int index = i;

            @Override
            public boolean hasNext() {
                return index < size();
            }

            @Override
            public E next() {
                return get(index++);
            }

            @Override
            public void add(E event) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean hasPrevious() {
                return index > 0;
            }

            @Override
            public int nextIndex() {
                return index + 1;
            }

            @Override
            public E previous() {
                return get(--index);
            }

            @Override
            public int previousIndex() {
                return index - 1;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void set(E event) {
                throw new UnsupportedOperationException();
            }
        };

    }

    @Override
    public int indexOf(Object object) {
        int s = size();
        if (object != null) {
            for (int i = 0; i < s; i++) {
                if (object.equals(get(i))) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < s; i++) {
                if (get(i) == null) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public E map(List<T> list, int index) { // abstract
        return (E) null;
    }

    @Override
    public E map(T item) { // abstract
        return (E) null;
    }

    public SimpleMapList map(Mappable<T, E> mapper) {
        mMapper = mapper;
        return this;
    }

    public SimpleMapList index(ListMappable<T, E> mapper) {
        mIndexMapper = mapper;
        return this;
    }
}
