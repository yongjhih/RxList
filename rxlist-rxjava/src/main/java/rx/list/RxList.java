package rx.list;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.ListIterator;

import rx.Observable;
import rx.observables.BlockingObservable;
import rx.schedulers.Schedulers;
import rx.subjects.*;
import rx.Subscriber;

/*
Observable.zip(Observable.from(mLocations), Observable.from(mImages), (location, image) -> {
   if (location == null || image == null) return null;
   return rpp.addPhoto(location.getLatitude(), location.getLongitude(), image);
).filter((photo) -> photo != null);
*/

public class RxList<E> extends ArrayList<E> {
    protected List<E> mList = new LinkedList<>();
    protected Observable<E> mObservable;
    protected IterableSubscriber<E> mSubscriber;

    public RxList() {
        super();
    }

    public RxList(Observable<E> observable) {
        this();

        mObservable = observable.cache();

        mSubscriber = new IterableSubscriber<E>();

        mObservable.onBackpressureBuffer().subscribeOn(Schedulers.io()).subscribe(mSubscriber);
    }

    @Override
    public E get(int index) {
        has(index);
        return mList.get(index);
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
        return mObservable.count().toBlocking().single();
    }

    public Observable<E> get() {
        return mObservable;
    }

    @Override
    public boolean isEmpty() {
        return !has(0);
    }

    @Override
    public Iterator<E> iterator() {
        return listIterator();
    }

    @Override
    public ListIterator<E> listIterator() {
        return listIterator(-1);
    }

    public boolean hasNext(int index) {
        return has(index + 1);
    }

    private boolean has(int index) {
        if (index < mList.size()) return true;

        while (mSubscriber.hasNext()) {
            E element = mSubscriber.next();
            if (element == null) break;
            mList.add(element);
            if (index < mList.size()) break;
        }

        return index < mList.size();
    }

    @Override
    public ListIterator<E> listIterator(final int i) {
        return new ListIterator<E>() {
            private int index = i;

            @Override
            public boolean hasNext() {
                return RxList.this.hasNext(index);
            }

            @Override
            public E next() {
                return get(++index);
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
    public void clear() {
        mList.clear();

        mObservable = Observable.empty();
        mSubscriber = new IterableSubscriber<E>();

        mObservable.onBackpressureBuffer().subscribeOn(Schedulers.io()).subscribe(mSubscriber);
    }

    @Override
    public boolean add(E object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {
        if (!mList.isEmpty()) {
            return false;
        }

        if (!(collection instanceof RxList)) {
            return false;
        }

        mObservable = ((RxList) collection).get();
        mSubscriber = new IterableSubscriber<E>();

        mObservable.onBackpressureBuffer().subscribeOn(Schedulers.io()).subscribe(mSubscriber);
        return true;
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

    static class IterableSubscriber<T> extends Subscriber<T> {
        private boolean completed = false;
        private Subject<T, T> subject;

        public IterableSubscriber() {
        }

        @Override
        public void onStart() {
            request(0);
        }

        @Override
        public synchronized void onError(Throwable e) {
            completed = true;
            subject.onError(e);
        }

        @Override
        public synchronized void onCompleted() {
            completed = true;
            subject.onCompleted();
        }

        @Override
        public void onNext(T t) {
            subject.onNext(t);
        }

        public boolean hasNext() {
            return !completed;
        }

        public T next() {
            synchronized(this) {
                if (!hasNext()) {
                    return null;
                }
                subject = new SerializedSubject(ReplaySubject.create());
            }
            return subject.asObservable().doOnSubscribe(() -> request(1)).toBlocking()
                    .firstOrDefault(null);
        }
    }
}
