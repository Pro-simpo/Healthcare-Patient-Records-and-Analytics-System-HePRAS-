package ma.ensa.healthcare.util;

/**
 * Utilitaires pour la pagination
 */
public class PaginationHelper {

    /**
     * Calcule l'offset pour une requête SQL
     * @param page Numéro de page (commence à 1)
     * @param size Taille de la page
     * @return Offset pour LIMIT/OFFSET
     */
    public static int calculateOffset(int page, int size) {
        if (page < 1) {
            page = 1;
        }
        if (size < 1) {
            size = 10;
        }
        return (page - 1) * size;
    }

    /**
     * Calcule le nombre total de pages
     * @param totalElements Nombre total d'éléments
     * @param pageSize Taille de la page
     * @return Nombre total de pages
     */
    public static int calculateTotalPages(long totalElements, int pageSize) {
        if (pageSize <= 0) {
            pageSize = 10;
        }
        return (int) Math.ceil((double) totalElements / pageSize);
    }

    /**
     * Vérifie si une page existe
     */
    public static boolean isValidPage(int page, long totalElements, int pageSize) {
        if (page < 1) {
            return false;
        }
        int totalPages = calculateTotalPages(totalElements, pageSize);
        return page <= totalPages;
    }

    /**
     * Obtient le numéro de la première page
     */
    public static int getFirstPage() {
        return 1;
    }

    /**
     * Obtient le numéro de la dernière page
     */
    public static int getLastPage(long totalElements, int pageSize) {
        return calculateTotalPages(totalElements, pageSize);
    }

    /**
     * Vérifie s'il y a une page précédente
     */
    public static boolean hasPrevious(int currentPage) {
        return currentPage > 1;
    }

    /**
     * Vérifie s'il y a une page suivante
     */
    public static boolean hasNext(int currentPage, long totalElements, int pageSize) {
        return currentPage < calculateTotalPages(totalElements, pageSize);
    }

    /**
     * Classe Page pour encapsuler les informations de pagination
     */
    public static class Page<T> {
        private final java.util.List<T> content;
        private final int pageNumber;
        private final int pageSize;
        private final long totalElements;
        private final int totalPages;

        public Page(java.util.List<T> content, int pageNumber, int pageSize, long totalElements) {
            this.content = content;
            this.pageNumber = pageNumber;
            this.pageSize = pageSize;
            this.totalElements = totalElements;
            this.totalPages = calculateTotalPages(totalElements, pageSize);
        }

        public java.util.List<T> getContent() {
            return content;
        }

        public int getPageNumber() {
            return pageNumber;
        }

        public int getPageSize() {
            return pageSize;
        }

        public long getTotalElements() {
            return totalElements;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public boolean isFirst() {
            return pageNumber == 1;
        }

        public boolean isLast() {
            return pageNumber == totalPages;
        }

        public boolean hasNext() {
            return pageNumber < totalPages;
        }

        public boolean hasPrevious() {
            return pageNumber > 1;
        }

        public int getNumberOfElements() {
            return content.size();
        }

        @Override
        public String toString() {
            return String.format("Page %d/%d (%d elements, %d total)", 
                               pageNumber, totalPages, getNumberOfElements(), totalElements);
        }
    }
}