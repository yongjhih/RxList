package rx.list;

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
 * return new MapList<ParseNotification, Notification>(notifications) {
 *     @Override
 *     public Notification map(ParseNotification notification) {
 *         return SimpleParse.load(Notification.class, notification);
 *     }
 * };
 * ```
 */
public class MapList2<T, T2, E> extends MapList<T, E> implements ListMappable2<T, T2, E>, Mappable2<T, T2, E>, Filter2<T, T2, E>, Filter<E> {
    private List<T> mList;
    private List<T2> mList2;
    protected Mappable2<T, T2, E> mMapper;
    protected ListMappable2<T, T2, E> mIndexMapper;
    protected Filter<E> mFilter;
    protected Filter2<T, T2, E> mFilter2;

    public MapList2(List<T> list, List<T2> list2) {
        this(list, list2, null, null);
    }

    public MapList2(List<T> list, List<T2> list2, Mappable2<T, T2, E> mapper) {
        this(list, list2, mapper, null);
    }

    public MapList2(List<T> list, List<T2> list2, ListMappable2<T, T2, E> mapper) {
        this(list, list2, null, mapper);
    }

    private MapList2(List<T> list, List<T2> list2, Mappable2<T, T2, E> mapper, ListMappable2<T, T2, E> indexMapper) {
        super();

        mList = Collections.emptyList();
        if (list != null) {
            mList = list;
        }
        mList2 = Collections.emptyList();
        if (list2 != null) {
            mList2 = list2;
        }
        mMapper = mapper == null ? this : mapper;
        mIndexMapper = indexMapper == null ? this : indexMapper;
        mFilter = this;
        mFilter2 = this;
        mCache = new LruCache<Integer, E>(1000);
    }

    @Override
    public E get(int index) {
        E ret = mCache.get(index);
        if (ret == null) {
            ret = mIndexMapper.map(mList, mList2, index);

            T item = mList.get(index);
            T2 item2 = mList2.get(index);

            if (ret == null) {
                ret = mMapper.map(item, item2);
            }

            if (!mFilter.filter(ret)) {
                return null;
            }

            if (!mFilter2.filter(item, item2, ret)) {
                return null;
            }

            if (ret != null) {
                mCache.put(index, ret);
            }
        }
        return ret;
    }

    @Override
    public boolean filter(E item) {
        return true;
    }

    @Override
    public boolean filter(T t, T2 t2, E item) {
        return true;
    }

    public MapList2<T, T2, E> filter(Filter filter) {
        mFilter = filter;
        return this;
    }

    public MapList2<T, T2, E> filter(Filter2 filter) {
        mFilter2 = filter;
        return this;
    }

    @Override
    public E update(int index) {
        return null;
    }

    @Override
    public int size() {
        return Math.min(mList.size(), mList2.size());
    }

    @Override
    public void clear() {
        mList = Collections.emptyList();
        mList2 = Collections.emptyList();
        mCache.evictAll();
    }

    @Override
    public List<T> getData() {
        return get();
    }

    @Override
    public List<T> getList() {
        return get();
    }

    public List<T> get() {
        return mList;
    }

    public List<T2> get2() {
        return mList2;
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {
        if (!mList.isEmpty()) {
            return false;
        }

        if (!(collection instanceof MapList)) {
            return false;
        }

        mList = ((MapList) collection).getData();
        return true;
    }

    @Override
    public E map(List<T> list, List<T2> list2, int index) { // abstract
        return (E) null;
    }

    @Override
    public E map(T item, T2 item2) { // abstract
        return (E) null;
    }

    public MapList2<T, T2, E> map(Mappable2<T, T2, E> mapper) {
        mMapper = mapper;
        return this;
    }

    public MapList2<T, T2, E> index(ListMappable2<T, T2, E> mapper) {
        mIndexMapper = mapper;
        return this;
    }
}

