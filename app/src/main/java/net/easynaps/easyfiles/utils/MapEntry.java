package net.easynaps.easyfiles.utils;

public class MapEntry extends ImmutableEntry<ImmutableEntry<Integer, Integer>, Integer> {

    /**
     * Constructor to provide values to the pair
     * @param key object of {@link ImmutableEntry} which is another key/value pair
     * @param value integer object in the pair
     */
    public MapEntry(ImmutableEntry<Integer, Integer> key, Integer value) {
        super(key, value);
    }
}
