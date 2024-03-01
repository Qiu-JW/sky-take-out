package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     *
     * @param dishIds
     * @return
     */
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

    /**
     * 通过套餐id查询菜品套餐关系表
     * @param id
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{id}")
    List<SetmealDish> getBySetmealId(Long id);

    /**
     * 批量插入套餐数据
     * @param setmealDishes
     */
    void setMealDish(List<SetmealDish> setmealDishes);

    /**
     * 批量删除套餐 和菜品数据
     * @param setmealIds
     */
    void deleteBatch(List<Long> setmealIds);
}
