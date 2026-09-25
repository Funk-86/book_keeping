package org.example.book_keeping.model.transaction.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.book_keeping.model.transaction.entity.Transaction;
import org.example.book_keeping.model.transaction.vo.StatsItemVO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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

    @Select("SELECT category_id, COALESCE(SUM(amount), 0) AS amount " +
            "FROM `transaction` " +
            "WHERE user_id = #{userId} AND type = #{type} " +
            "AND trade_time >= #{from} AND trade_time <= #{to} " +
            "AND status = 0 AND deleted = 0 " +
            "GROUP BY category_id ")
    List<StatsItemVO> sumAmountByCategoryId(@Param("userId") Long userId,
                                            @Param("type") Integer type,
                                            @Param("from") LocalDateTime from,
                                            @Param("to") LocalDateTime to);
}
