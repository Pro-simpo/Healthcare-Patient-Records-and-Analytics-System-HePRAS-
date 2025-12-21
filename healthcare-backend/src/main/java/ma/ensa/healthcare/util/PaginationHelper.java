package ma.ensa.healthcare.util;

public class PaginationHelper {
    public static int calculateOffset(int page, int size) {
        return (page - 1) * size;
    }
}