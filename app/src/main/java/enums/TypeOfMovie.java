package enums;

/**
 * @author igiagante, on 9/9/15.
 */
public enum TypeOfMovie {

    /**
     * Indicates which kind of list of movies should be loaded.
     */
    POPULARITY("popularity"),
    RATING("vote_average"),
    FAVORITE("favorites");

    private String sortBy;

    TypeOfMovie(String sortBy){
        this.sortBy = sortBy;
    }

    public String getSortBy() {
        return sortBy;
    }
}
