package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

public interface DishService {
    /* 新增菜品 */
    void saveWithFlavor(DishDTO dishDTO);
    //菜品分类查询
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 菜品批量删除功能
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据id查询菜品和口味数据
     * @param id
     * @return
     */
    DishVO getByIdWithFlavor(long id);

    void updateWithFlavor(DishDTO dishDTO);

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    List<Dish> list(Long categoryId);
    /**
     * 菜品起售停售
     * @param status
     * @param id
     */
    void startOrStop(Integer status, Long id);

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    List<DishVO> listWithFlavor(Dish dish);
}
