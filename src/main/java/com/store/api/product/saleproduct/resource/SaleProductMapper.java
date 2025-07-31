package com.store.api.product.saleproduct.resource;

import com.store.api.common.typehandler.JsonMapTypeHandler;
import com.store.api.product.saleproduct.model.SaleProduct;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SaleProductMapper {

    @Result(property = "details", column = "details", typeHandler = JsonMapTypeHandler.class)
    @Select("""
    <script>
    SELECT * FROM sale_product
    WHERE product_id = #{productId}
    </script>
    """
    )
    SaleProduct selectById(Long productId);

    @Update("""
    <script>
    UPDATE sale_product
    SET regular_stock = regular_stock - #{quantity}
    WHERE product_id = #{productId} AND regular_stock &gt;= #{quantity}
    </script>
    """)
    int updateRegularStock(int quantity, Long productId);

    @Update("""
    <script>
    UPDATE sale_product
    SET promotion_stock = promotion_stock - #{quantity}
    WHERE product_id = #{productId} AND promotion_stock &gt;= #{quantity}
    </script>
    """)
    int updatePromotionStock(int quantity, Long productId);

    @Delete("""
    <script>
    DELETE FROM sale_product
    WHERE product_id = #{productId}
    </script>
    """)
    void deleteById(Long productId);

    @Insert("""
    <script>
    INSERT INTO sale_product(product_id, regular_stock, promotion_stock, promotion_id)
    VALUES (
    #{productId},
    #{regularStock},
    #{promotionStock},
    #{promotionId}
    )
    </script>
    """)
    void insert(Long productId, int regularStock, int promotionStock, Long promotionId);
}
