package edu.gatech.hava.hdt.views.filter;

/**
 * A filter for objects based on some search.
 *
 * @param <S> the type of object used to filter
 * @param <T> the type of objects being filtered
 */
public interface IFilter<S, T> {

    /**
     * Sets the selection criterion.
     *
     * @param search a string to match, which may use asterisk
     *               as a wildcard.
     */
    void setSearch(S search);

    /**
     * Determines whether an object meets the selection criterion.
     *
     * @param obj the object to check
     * @return true if obj is selected, false if it is not
     */
    boolean select(T obj);

}
