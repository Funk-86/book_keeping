package org.example.book_keeping.model.auth.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.book_keeping.common.entity.BaseEntity;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("`user`")
public class User extends BaseEntity {

    private String username;

    private String passwordHash;
}
