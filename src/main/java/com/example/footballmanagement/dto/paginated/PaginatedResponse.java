package com.example.footballmanagement.dto.paginated;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginatedResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;
}