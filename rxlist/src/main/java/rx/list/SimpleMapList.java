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
 * return new SimpleMapList<ParseNotification, Notification>(notifications) {
 *     @Override
 *     public Notification map(ParseNotification notification) {
 *         return SimpleParse.load(Notification.class, notification);
 *     }
 * };
 * ```
 */
public class SimpleMapList<T, E> extends MapList<E> implements ListMappable<T, E>, Mappable<T, E>, Filter<E> {
    private List<T> mList;
    protected Mappable<T, E> mMapper;
    protected ListMappable<T, E> mIndexMapper;
    protected Filter<E> mFilter;

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

    @Override
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
    public int size() {
        return mList.size();
    }

    @Override
    public void clear() {
        mList = Collections.emptyList();
        mCache.evictAll();
    }

    @Override
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

        mList = ((MapList) collection).getData();
        return true;
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
