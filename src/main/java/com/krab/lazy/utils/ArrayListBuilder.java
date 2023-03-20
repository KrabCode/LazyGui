package com.krab.lazy.utils;

import java.util.ArrayList;

public class ArrayListBuilder<T> {
        private final ArrayList<T> list = new ArrayList<>();

        public ArrayList<T> build() {
            return list;
        }

        ArrayListBuilder<T> add(T o) {
            list.add(o);
            return this;
        }

        @SafeVarargs
        public final ArrayListBuilder<T> add(T... items) {
            for (T t : items) {
                add(t);
            }
            return this;
        }


}
