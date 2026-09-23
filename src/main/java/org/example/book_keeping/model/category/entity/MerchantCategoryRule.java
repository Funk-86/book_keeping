package org.example.book_keeping.model.category.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.book_keeping.common.entity.BaseEntity;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("merchant_category_rule")
public class MerchantCategoryRule extends BaseEntity {

    private String keyword;

    private Long categoryId;

    private Integer priority;
}
