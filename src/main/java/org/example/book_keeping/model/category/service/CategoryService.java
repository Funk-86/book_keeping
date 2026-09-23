package org.example.book_keeping.model.category.service;

/**
 * 分类业务接口。
 */
public interface CategoryService {

    /**
     * 按商户名匹配分类 ID，未命中返回未分类。
     *
     * @param merchant 商户/交易对象
     * @return categoryId
     */
    Long matchCategoryId(String merchant);
}
