import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountedCompleter;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by lightray on 12/11/16.
 */
public class Searcher<E> extends CountedCompleter<E> {
    final E[] array;
    final AtomicReference<E> result;
    final int lo;
    final int hi;
    ConcurrentHashMap<E,String> map;
    ConcurrentSkipListSet set;
    AtomicReference<String> ref;
    public E getRawResult(){return result.get();}
    Searcher(CountedCompleter<?> parent, E[] array,
             AtomicReference<E> result, int lo, int hi){
        super(parent);
        this.array = array; this.result = result; this.lo = lo;
        this.hi = hi;
    }

    @Override
    public void compute() {
        int l = lo, h = hi;
        while(result.get() == null && h >= l){
            if(h - l >= 2){
                int mid = (l+h)>>>l;
            }
        }
    }
}
