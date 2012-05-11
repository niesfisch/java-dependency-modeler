package de.marcelsauer.javamodel;

/**
 * @author msauer
 */
public interface TypeFilter {

    boolean accept(String fullQualifiedType);

    public static TypeFilter ACCEPTING_TYPE_FILTER = new AcceptingTypeFilter();

    static class AcceptingTypeFilter implements TypeFilter {
        @Override
        public boolean accept(String fullQualifiedType) {
            return true;
        }
    }

}
