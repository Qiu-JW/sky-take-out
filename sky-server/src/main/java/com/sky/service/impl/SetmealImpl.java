package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SetmealImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;


    /**
     * 插入套餐数据和套餐关联的菜品数据
     * @param setmealDTO
     */
    @Override
    public void addMeal(SetmealDTO setmealDTO) {
        /*  将新增数据封装好 */
        Setmeal setmeal=new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        Integer setmealId= setmealMapper.addMeal(setmeal);

        /*  新增套餐相应的菜品,需要关联的id去setmealmapper中 */
        List <SetmealDish> setmealDishes= setmealDTO.getSetmealDishes();
        if (setmealDishes!=null && setmealDishes.size()>0){
            setmealDishes.forEach(setmealDish -> {
                setmealDish.setSetmealId(setmeal.getId());
                System.out.println(setmealDish.toString());
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
        log.info("正在进行套餐分页查询：{}"+setmealPageQueryDTO);
        // 该插件固定写法，该插件的作用就是查询后的分页
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page=setmealMapper.pageQuery(setmealPageQueryDTO);

        long total=page.getTotal();
        List<SetmealVO> records=page.getResult();

        return new PageResult(total,records);
    }


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

    @Override
    public void upMeal(SetmealVO setmealVO) {
        // setmealMapper.upMeal(setmealVO.getId());
    }
}
