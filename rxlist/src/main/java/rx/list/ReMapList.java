package rx.list;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import android.support.v4.util.LruCache;

public class ReMapList<E> extends ArrayList<E> {
    private ReMapList() {
        super();
    }

    private ReMapList(SimpleMapper<?, E> mapper) {
        super();

        mMapper = mapper;
    }

    public static <T, E> ReMapList create(List<? extends T> data, Mappable<T, E> mappable) {
        return new ReMapList(new SimpleMapper(data, mappable));
    }

    public static class SimpleMapper<T, R> {
        List<? extends T> data;
        Mappable<T, R> mappable;
        LruCache<Integer, R> cache;

        public SimpleMapper(List<? extends T> data, Mappable<T, R> mappable) {
            this.data = (data != null) ? Collections.emptyList() : data;
            this.mappable = mappable;
            cache = new LruCache<Integer, R>(1000);
        }

        public R map(int index) {
            return mappable.map(data, index);
        }
    }

    private SimpleMapper<?, E> mMapper;

    @Override
    public E get(int index) {
        E ret = mMapper.cache.get(index);
        if (ret == null) {
            ret = mMapper.map(index);
            if (ret != null) {
                mMapper.cache.put(index, ret);
            }
        }
        return ret;
    }

    public E update(int index) {
        E ret = mMapper.map(index);
        if (ret != null) {
            mMapper.cache.put(index, ret);
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
        return mMapper.data.size();
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
        mMapper.data = Collections.emptyList();
        mMapper.cache.evictAll();
    }

    public List getData() {
        return mMapper.data;
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {
        if (!mMapper.data.isEmpty()) {
            return false;
        }

        if (!(collection instanceof ReMapList)) {
            return false;
        }

        mMapper.data = ((ReMapList) collection).getData();
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

    public interface Mappable<T, E> {
        E map(List<? extends T> data, int index);
    }
}
