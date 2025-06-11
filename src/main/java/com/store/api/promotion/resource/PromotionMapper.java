package com.store.api.promotion.resource;

import com.store.api.common.typehandler.JsonMapTypeHandler;
import com.store.api.promotion.model.Promotion;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PromotionMapper {

    @Result(property = "details", column = "details", typeHandler = JsonMapTypeHandler.class)
    @Select("""
    <script>
    SELECT * FROM promotion
    WHERE id = #{promotionId}
    </script>
    """)
    Promotion selectById(Long promotionId);

    @Select("""
    <script>
    SELECT id FROM promotion
    WHERE start_at &lt;= #{now} AND end_at &gt;= #{now}
    </script>
    """)
    List<Promotion> selectAllCurrentPromotions(Long now);

    boolean isPromotionActive();
}
