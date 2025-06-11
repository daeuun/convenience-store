package com.store.api.product.resource;

import com.store.api.product.model.Product;
import com.store.api.product.model.ProductSearchParam;
import com.store.api.product.model.ProductUpdateRequest;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ProductMapper {

    @Select("""
    <script>
    SELECT * FROM product
    WHERE <if test="lastId != null"> product.id &gt; #{lastId}</if>
    ORDER BY product.id DESC
    LIMIT #{limit}
    </script>
    """
    )
    List<Product> selectAll(ProductSearchParam productSearchParam);

    @Select("""
    <script>
    SELECT * FROM product
    WHERE id = #{productId}
    </script>
    """
    )
    Product selectById(Long productId);

    @Update("""
    <script>
    UPDATE product
    <set>
        <if test="request.name != null"> name = #{request.name},</if>
        <if test="request.price != 0"> price = #{request.price},</if>
    </set>
    WHERE id = #{id}
    </script>
    """)
    void update(@Param("id") Long id, @Param("request") ProductUpdateRequest request);

    @Select("""
    <script>
    DELETE FROM product
    WHERE id = #{id}
    </script>
    """
    )
    void delete(@Param("id") Long id);
}
