package game.model;

/**
 * class for representing a pair of two generic elements of arbitrary types.
 * similar to the pair type in Haskell.
 */
public class Pair <U,V> {
    public U fst;
    public V snd;

    /**
     * constructor method.
     * @param val1 element 1 of the pair.
     * @param val2 element 2 of the pair.
     */
    public Pair (U val1, V val2) {
        fst = val1;
        snd = val2;
    }

    /**
     * overridden toString method used for debugging.
     */
    @Override
    public String toString() {
        return "(" + String.valueOf(fst) + ", " + String.valueOf(snd) + ")";
    }
}