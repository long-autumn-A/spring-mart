package com.enshi.springmart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enshi.springmart.entity.Cart;
import com.enshi.springmart.vo.CartVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CartMapper extends BaseMapper<Cart> {
    //    连表查询将cart表和 product表拼起来
    @Select("SELECT " +
            "c.id AS id, c.user_id AS userId, c.product_id AS productId, c.quantity AS quantity, c.created_at AS createdAt, " +
            "p.name AS productName, p.main_image AS mainImage, p.price AS price, p.stock AS stock, p.status AS status " +
            "FROM cart c " +
            "LEFT JOIN product p ON c.product_id = p.id " +
            "WHERE c.user_id = #{userId} " +
            "ORDER BY c.updated_at DESC")
    List<CartVO> selectCartListByUserId(@Param("userId") Long userId);

}
