package com.foodrescue.common.response;

import java.util.List;
import org.springframework.data.domain.Page;

public record PageResponse<T>(List<T> content, int page, int size, long totalElements, int totalPages) {

    public static <T> PageResponse<T> from(Page<T> p) {
        return new PageResponse<>(p.getContent(), p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
    }

    /** Manual paging for already-materialised lists (e.g. after in-memory distance filtering). */
    public static <T> PageResponse<T> of(List<T> all, int page, int size) {
        int safeSize = Math.max(1, size);
        int safePage = Math.max(0, page);
        int from = (int) Math.min((long) safePage * safeSize, all.size());
        int to = Math.min(from + safeSize, all.size());
        int totalPages = (int) Math.ceil(all.size() / (double) safeSize);
        return new PageResponse<>(all.subList(from, to), safePage, safeSize, all.size(), totalPages);
    }
}
