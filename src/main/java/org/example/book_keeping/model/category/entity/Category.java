package org.example.book_keeping.model.category.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.book_keeping.common.entity.BaseEntity;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("category")
public class Category extends BaseEntity {

    private String name;

    private Long parentId;

    private String icon;
}
