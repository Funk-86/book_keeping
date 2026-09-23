package org.example.book_keeping.model.parserule.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.book_keeping.common.entity.BaseEntity;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("parse_rule")
public class ParseRule extends BaseEntity {

    private String packageName;

    private String sourceCode;

    private String pattern;

    private Integer amountGroup;

    private Integer merchantGroup;

    /** 1支出 2收入 */
    private Integer typeHint;

    private Integer version;

    /** 1启用 0禁用 */
    private Integer enabled;
}
