package net.easynaps.easyfiles.utils;

import android.support.annotation.Nullable;

import java.util.Map;

public class ImmutableEntry<K, V> implements Map.Entry<K, V> {
    private final K key;
    private final V value;

    public ImmutableEntry(@Nullable K key, @Nullable V value) {
        this.key = key;
        this.value = value;
    }

    @Nullable
    @Override
    public final K getKey() {
        return key;
    }

    @Nullable
    @Override
    public final V getValue() {
        return value;
    }

    @Override
    public final V setValue(V value) {
        throw new UnsupportedOperationException();
    }

}