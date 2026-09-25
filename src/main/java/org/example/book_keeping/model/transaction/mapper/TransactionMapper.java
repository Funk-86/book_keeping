package org.example.book_keeping.model.transaction.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.book_keeping.model.transaction.entity.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface TransactionMapper extends BaseMapper<Transaction> {

    @Select("SELECT COALESCE(SUM(amount), 0) FROM `transaction` " +
            "WHERE user_id = #{userId} AND type = #{type} " +
            "AND trade_time >= #{from} AND trade_time <= #{to} " +
            "AND status = 0 " +
            "AND deleted = 0")
    BigDecimal sumAmount(@Param("userId") Long userId,
                         @Param("type") Integer type,
                         @Param("from") LocalDateTime from,
                         @Param("to") LocalDateTime to);
}
