package com.krab.lazy.utils;

import java.util.ArrayList;
import java.util.List;

public class ListBuilder<T> {
	
        private final List<T> list = new ArrayList<>();

        public List<T> build() {
            return list;
        }

        ListBuilder<T> add(T o) {
            list.add(o);
            return this;
        }

        @SafeVarargs
        public final ListBuilder<T> add(T... items) {
            for (T t : items) {
                add(t);
            }
            return this;
        }


}
