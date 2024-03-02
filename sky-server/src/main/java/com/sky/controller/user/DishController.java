package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
@Api(tags = "C端-菜品浏览接口")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    // @GetMapping("/list")
    // @ApiOperation("根据分类id查询菜品")
    // public Result<List<DishVO>> list(Long categoryId) {
    //     Dish dish = new Dish();
    //     dish.setCategoryId(categoryId);
    //     dish.setStatus(StatusConstant.ENABLE);//查询起售中的菜品
    //
    //     List<DishVO> list = dishService.listWithFlavor(dish);
    //
    //     return Result.success(list);
    // }


    /* 进行接口改造，使得能存放进redis中 */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<DishVO>> list(Long categoryId) {

        // 构造redis中的key dish_分类id
        String key="dish_"+categoryId;

        // 先查找redis中是否存在菜品数据 此处数据类型和存放进去的数据类型一致
        List<DishVO> list =(List<DishVO>) redisTemplate.opsForValue().get(key);

        if (list!=null &&list.size()>0){
        //     直接返回内容无需查询数据库
            return Result.success(list);
        }

        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);//查询起售中的菜品
        /* 如果不存在，查询数据库，将查询道德数据放入reids中 */
        list = dishService.listWithFlavor(dish);
        redisTemplate.opsForValue().set(key,list);

        return Result.success(list);
    }

}
