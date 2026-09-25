package org.example.book_keeping.common.result;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class PageResult<T> {
    private List<T> list;
    private long total;
    private long pageNum;
    private long pageSize;
    private long pages;

    public static <T> PageResult<T> of(IPage<T> page) {
        PageResult<T> result = new PageResult<>();
        result.setList(page.getRecords());
        result.setTotal(page.getTotal());
        result.setPageNum(page.getCurrent());
        result.setPageSize(page.getSize());
        result.setPages(page.getPages());
        return result;
    }

    public static <T> PageResult<T> empty(Integer pageNum, Integer pageSize) {
        PageResult<T> result = new PageResult<>();
        result.setList(Collections.emptyList());
        result.setTotal(0);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setPages(0);
        return result;
    }
}
