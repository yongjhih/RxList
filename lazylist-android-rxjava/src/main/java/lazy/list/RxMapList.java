package lazy.list;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.ListIterator;

import rx.Observable;
import rx.Observable.Transformer;
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

public class RxMapList<R, E> extends ArrayList<E> {
    private Observable<R> mObservable;
    private Transformer<R, E> mTransformer;
    private MapList<R, E> mMapList;
    private RxList<R> mList;

    public RxMapList() {
        super();
    }

    public static <U, V> RxMapList<U, V> create(Observable<U> observable,
            Transformer<U, V> transformer) {
        return new RxMapList<U, V>(observable, transformer);
    }

    public RxMapList(Observable<R> observable, Transformer<R, E> transformer) {
        this();

        mObservable = observable;
        mTransformer = transformer;

        mList = new RxList<R>(mObservable);
        mMapList = new MapList(mList, (l, index) -> {
            return mTransformer.call(Observable.just((R) l.get(index))).toBlocking().single();
        });
    }

    @Override
    public E get(int index) {
        return mMapList.get(index);
    }

    @Override
    public Object[] toArray() {
        return mMapList.toArray();
    }

    @Override
    public <T> T[] toArray(T[] contents) {
        return mMapList.toArray(contents);
    }

    @Override
    public int size() {
        return mMapList.size();
    }

    public MapList<R, E> get() {
        return mMapList;
    }

    @Override
    public boolean isEmpty() {
        return mMapList.isEmpty();
    }

    @Override
    public Iterator<E> iterator() {
        return mMapList.iterator();
    }

    @Override
    public ListIterator<E> listIterator() {
        return mMapList.listIterator();
    }

    @Override
    public ListIterator<E> listIterator(final int i) {
        return mMapList.listIterator(i);
    }

    @Override
    public void clear() {
        mMapList.clear();
    }

    @Override
    public boolean add(E object) {
        return mMapList.add(object);
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {
        if (!(collection instanceof RxMapList)) {
            return false;
        }
        return mMapList.addAll(((RxMapList) collection).get());
    }

    @Override
    public int indexOf(Object object) {
        return mMapList.indexOf(object);
    }
}
