package com.sky.controller.admin;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.impl.OrderServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/order")
@Api(tags = "订单管理相关接口")
@Slf4j
public class OrderController {

    @Autowired
    private OrderServiceImpl orderServiceImpl;
    @GetMapping("/conditionSearch")
    @ApiOperation("显示全部订单")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("搜索订单接受的数据为:{}",ordersPageQueryDTO);
        PageResult pageResult=orderServiceImpl.conditionSearch(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    // @GetMapping("/details/{id}")
    // @ApiOperation("查看某条订单数据")
    // public Result<PageResult> gerById(@PathVariable int id) {
    //     log.info("要查询的数据:{}",id);
    //     PageResult pageResult=orderServiceImpl.conditionSearch();
    //     return Result.success(pageResult);
    // }

}
