package rx.list;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import android.util.LruCache;

public class MapList<E> extends ArrayList<E> {
    private List<? extends Object> mData;
    private Mapper<E> mMapper;
    protected LruCache<Integer, E> mCache;

    public MapList() {
        super();
    }

    public MapList(List<? extends Object> data, Mapper<E> mapper) {
        this();

        mData = Collections.emptyList();
        if (data != null) {
            mData = data;
        }
        mMapper = mapper;
        mCache = new LruCache<Integer, E>(1000);
    }

    @Override
    public E get(int index) {
        E ret = mCache.get(index);
        if (ret == null) {
            ret = mMapper.map(mData, index);
            if (ret != null) {
                mCache.put(index, ret);
            }
        }
        return ret;
    }

    public E update(int index) {
        E ret = mMapper.map(mData, index);
        if (ret != null) {
            mCache.put(index, ret);
        }
        return ret;
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
        return mData.size();
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
        mData = Collections.emptyList();
        mCache.evictAll();
    }

    public List<? extends Object> getData() {
        return mData;
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {
        if (!mData.isEmpty()) {
            return false;
        }

        if (!(collection instanceof MapList)) {
            return false;
        }

        mData = ((MapList) collection).getData();
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

    public interface Mapper<E> {
        E map(List<? extends Object> data, int index);
    }
}
