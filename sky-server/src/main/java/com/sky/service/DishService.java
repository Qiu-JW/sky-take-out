package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
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
}
