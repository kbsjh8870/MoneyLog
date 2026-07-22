package org.example.backend.common.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

public class PageMeta {

    @Getter
    @AllArgsConstructor
    public static class Pagination {
        private final int page;
        private final int size;
        private final long totalItems;
        private final int totalPages;
        private final boolean hasNext;
        private final boolean hasPrev;
    }

    private final Pagination pagination;

    public PageMeta(Pagination pagination) {
        this.pagination = pagination;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public static PageMeta from(Page<?> page) {
        return new PageMeta(new Pagination(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext(),
                page.hasPrevious()
        ));
    }
}
