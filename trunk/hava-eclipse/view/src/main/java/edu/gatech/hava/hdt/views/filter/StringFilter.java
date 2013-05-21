package edu.gatech.hava.hdt.views.filter;

import java.util.regex.Pattern;

/**
 * A filter implementation which filters by a search String.
 *
 * Search strings may use asterisk as a wildcard,
 * and always have an implicit wildcard at the end.
 *
 * @param <T> the type of object being filtered
 */
public class StringFilter<T> implements IFilter<String, T> {

    private String search = "";

    private boolean useRegex = false;
    private boolean compiled = false;
    private Pattern searchPattern = null;

    /** {@inheritDoc} */
    @Override
    public boolean select(final T obj) {

        final String string = toString(obj);

        final boolean select = select(string);

        return select;

    }

    /**
     * Converts an object to a string, which will be matched against
     * the search string to determine the object's inclusion.
     *
     * @param obj the object to convert
     * @return a string representation of the object
     */
    protected String toString(final T obj) {

        return obj == null ? null : obj.toString();

    }

    /**
     * Determines whether a {@link String} meets the
     * selection criterion.
     *
     * @param string the string to check
     * @return true if string is selected, false if it is not
     */
    public boolean select(final String string) {

        if (string != null) {

            if (useRegex) {

                if (!compiled) {
                    compile();
                }

                return searchPattern != null
                    && searchPattern.matcher(string).matches();

            } else {

                return string.toLowerCase().startsWith(
                        search.toLowerCase());

            }

        }

        return true;

    }

    /** {@inheritDoc} */
    @Override
    public void setSearch(final String search) {

        this.search = search;
        this.compiled = false;
        this.useRegex = search.contains("*");

    }

    /**
     * Sanitizes input and compiles regex for re-use.
     */
    private void compile() {

        // Split on asterisks and sanitize the rest
        String[] pieces = search.split("\\*", -1);
        for (int i = 0; i < pieces.length; i++) {
            pieces[i] = Pattern.quote(pieces[i]);
        }

        // Join the sanitized search with ".*"
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < pieces.length; i++) {
            builder.append(pieces[i]);
            builder.append(".*");
        }

        // Compile the pattern
        searchPattern = Pattern.compile(
                builder.toString(), Pattern.CASE_INSENSITIVE);
        compiled = true;

    }

}
