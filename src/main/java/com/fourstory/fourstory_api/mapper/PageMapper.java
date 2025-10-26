package com.fourstory.fourstory_api.mapper;

import com.fourstory.fourstory_api.dto.response.PageResponse;
import org.springframework.data.domain.Page;

public class PageMapper {

    private PageMapper() {}

    public static <T> PageResponse<T> toPageResponse(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasPrevious(),
                page.hasNext()
        );
    }
}
