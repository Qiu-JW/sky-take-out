package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SetmealImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private DishMapper dishMapper;

    /**
     * 插入套餐数据和套餐关联的菜品数据
     * @param setmealDTO
     */
    @Transactional
    public void addMeal(SetmealDTO setmealDTO) {
        /*  将新增数据封装好 */
        Setmeal setmeal=new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.addMeal(setmeal);
        /*  新增套餐相应的菜品,需要关联的id去setmealmapper中 */
        List <SetmealDish> setmealDishes= setmealDTO.getSetmealDishes();

        if (setmealDishes!=null && setmealDishes.size()>0){
            setmealDishes.forEach(setmealDish -> {
                setmealDish.setSetmealId(setmeal.getId());
            });
        }
        setmealDishMapper.setMealDish(setmealDishes);
    }

    /**
     * 分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("正在进行套餐分页查询：{}",setmealPageQueryDTO);
        // 该插件固定写法，该插件的作用就是查询后的分页
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        long total;
        List<SetmealVO> records;
        try (Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO)) {

            total = page.getTotal();
            records = page.getResult();
        }

        return new PageResult(total,records);
    }

    /**
     * 根据id查询对应菜品
     * @param id
     * @return
     */
    @Override
    public SetmealVO getMealById(Long id) {
        //根据id查询套餐
        Setmeal setmeal = setmealMapper.getMealById(id);

        //根据id查询对应菜品

        List<SetmealDish> setmealDishs = setmealDishMapper.getBySetmealId(id);

        //将结果封装到VO
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal,setmealVO);
        setmealVO.setSetmealDishes(setmealDishs);

        return setmealVO;
    }

    /**
     * 修改套餐
     * @param setmealDTO
     */

    @Transactional
    public void upMeal(SetmealDTO setmealDTO) {
        // 准备更新数据
        Setmeal setmeal=new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);

        setmealMapper.upMeal(setmeal);
        // 直接删除原本套餐菜品表中的数据

        List<Long> setmealId = new ArrayList<>();
        setmealId.add(setmeal.getId());
        setmealDishMapper.deleteBatch(setmealId);
        /*  修改套餐相应的菜品,需要关联的id去setmealmapper中 */
        List <SetmealDish> setmealDishes= setmealDTO.getSetmealDishes();
        if (setmealDishes!=null && !setmealDishes.isEmpty()){
            setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmeal.getId()));
        }
        setmealDishMapper.setMealDish(setmealDishes);

    }

    /**
     * 批量删除套餐
     * @param ids
     */
    @Transactional
    public void deleteBatch(List<Long> ids) {
        /* 因为业务逻辑是起售中、关联菜品的套餐不能进行删除，所以先查询是否是起售中的数据 */
        for (Long id : ids) {
            Setmeal setmeal = setmealMapper.getMealById(id);
            if (setmeal.getStatus()== StatusConstant.ENABLE){
                log.info("套餐正在售卖，无法删除");
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //为了提升性能，选择一次性完成删除操作
        /* 先删除套餐菜品关系表 */
        setmealDishMapper.deleteBatch(ids);
        /* 将套餐删除 */
        setmealMapper.deleteBatch(ids);

    }

    /**
     * 起售停售套餐
     * @param status
     * @param id
     */
    public void upStatus(Integer status,Long id) {
        //起售套餐时，判断套餐内是否有停售菜品
        if(status == StatusConstant.ENABLE){
            //select a.* from dish a left join setmeal_dish b on a.id = b.dish_id where b.setmeal_id = ?
            List<Dish> dishList = dishMapper.getBySetmealId(id);
            if(dishList != null && dishList.size() > 0){
                dishList.forEach(dish -> {
                    if(StatusConstant.DISABLE == dish.getStatus()){
                        throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                    }
                });
            }
        }
        // Setmeal setmeal = Setmeal.builder()
        //         .id(id)
        //         .status(status)
        //         .build();
        // todo 这里可以修改前面的修改方法，来简化代码，但我不想动
        long status1=status;
        setmealMapper.update(status1, id);
    }
}
