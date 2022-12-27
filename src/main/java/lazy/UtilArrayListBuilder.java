package lazy;

import java.util.ArrayList;

public class UtilArrayListBuilder<T> {
        private final ArrayList<T> list = new ArrayList<>();

        ArrayList<T> build() {
            return list;
        }

        UtilArrayListBuilder<T> add(T o) {
            list.add(o);
            return this;
        }

        @SafeVarargs
        public final UtilArrayListBuilder<T> add(T... options) {
            for (T t :
                    options) {
                add(t);
            }
            return this;
        }


}
