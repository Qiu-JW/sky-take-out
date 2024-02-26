package com.sky.service;

import com.sky.dto.DishDTO;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;

public interface DishService {
    /* 新增菜品 */
    void saveWithFlavor(DishDTO dishDTO);

}
