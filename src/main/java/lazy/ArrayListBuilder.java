package lazy;

import java.util.ArrayList;

public class ArrayListBuilder<T> {
        private final ArrayList<T> list = new ArrayList<>();

        ArrayList<T> build() {
            return list;
        }

        ArrayListBuilder<T> add(T o) {
            list.add(o);
            return this;
        }

        @SafeVarargs
        public final ArrayListBuilder<T> add(T... options) {
            for (T t :
                    options) {
                add(t);
            }
            return this;
        }


}
