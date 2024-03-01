package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;
import org.springframework.stereotype.Service;


public interface SetmealService {

    /**
     * 新增套餐
     * @param setmealDTO
     */
    void addMeal(SetmealDTO setmealDTO);

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 通过id查询套餐
     * @param id
     * @return
     */
    SetmealVO getMealById(Long id);

    /**
     * 修改套餐数据
     * @param setmealVO
     */
    void upMeal(SetmealVO setmealVO);
}
