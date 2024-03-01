package com.sky.controller.admin;


import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
@Api(tags = "套餐相关接口")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    /**
     * 新增套餐接口
     * @param setmealDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增套餐")
    public Result addMeal(@RequestBody SetmealDTO setmealDTO){
        log.info("新增套餐数据:{}",setmealDTO);
        setmealService.addMeal(setmealDTO);
        return Result.success();
    }

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */

    @GetMapping("/page")
    @ApiOperation("套餐分页查询")
    public Result<PageResult> pageQuery(@ModelAttribute SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("接收到的套餐数据:{}",setmealPageQueryDTO);
        PageResult pageResult=setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public Result<SetmealVO> getMealById(@PathVariable Long id ){
        log.info("要查询的套餐id:{}",id);
        SetmealVO setmealVO=setmealService.getMealById(id);
        return Result.success(setmealVO);
    }

    /**
     * 要修改的套餐数据
     * @param setmealDTO
     * @return
     */

    @PutMapping
    @ApiOperation("修改套餐")
    public Result upMeal(@RequestBody SetmealDTO setmealDTO){
        log.info("要修改的套餐数据:{}",setmealDTO);
        setmealService.upMeal(setmealDTO);
        return Result.success();
    }

    /**
     * 批量删除套餐数据
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("套餐批量删除")
    public Result delete(@RequestParam List<Long> ids){
        log.info("套餐批量删除：{}", ids);
        setmealService.deleteBatch(ids);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("起售停售")
    public Result addMeal(@PathVariable Integer status, Long  id){
        log.info("目前套餐的状态:{},要修改的套餐是:{}",status,id);
        setmealService.upStatus(status,id);
        return Result.success();
    }

}