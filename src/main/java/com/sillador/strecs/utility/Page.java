package com.sillador.strecs.utility;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Page {
    private long totalCount;
    private int currentPage;

    public Page(long totalCount, int currentPage){
        this.totalCount = totalCount;
        this.currentPage = currentPage;
    }
}
