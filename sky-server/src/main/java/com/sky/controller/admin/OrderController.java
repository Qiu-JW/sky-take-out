package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.service.impl.OrderServiceImpl;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/order")
@Api(tags = "订单管理相关接口")
@Slf4j
public class OrderController {

    @Autowired
    private OrderServiceImpl orderServiceImpl;
    @Autowired
    private OrderService orderService;
    @GetMapping("/conditionSearch")
    @ApiOperation("显示全部订单")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("搜索订单接受的数据为:{}",ordersPageQueryDTO);
        PageResult pageResult=orderServiceImpl.conditionSearch(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 订单详情
     * @param id
     * @return
     */
    @GetMapping("/details/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderVO> details(@PathVariable("id") Long id) {
        OrderVO orderVO = orderService.details(id);
        return Result.success(orderVO);
    }

    /**
     * 拒绝接收订单接口
     * @param ordersRejectionDTO
     * @return
     */
    @PutMapping("/rejection")
    @ApiOperation("拒绝接受订单")
    public Result rejectionOrders(@RequestBody OrdersRejectionDTO ordersRejectionDTO) {
        orderService.refuseOrders(ordersRejectionDTO);
        return Result.success();
    }

    /**
     * 取消未付款订单接口
     * @param ordersCancelDTO
     * @return
     */
    @PutMapping("/cancel")
    @ApiOperation("取消订单")
    public Result cancelOrders(@RequestBody OrdersCancelDTO ordersCancelDTO) {
        orderService.cancelOrders(ordersCancelDTO);
        return Result.success();
    }

    /**
     * 统计各种状态订单的控制器
     * @return
     */
    @GetMapping("/statistics")
    @ApiOperation("统计各个状态的订单数量")
    public Result<OrderStatisticsVO> statisticsOrders() {
        /* 统计订单 */
        OrderStatisticsVO orderStatisticsVO = orderService.statisticsOrders();
        return Result.success(orderStatisticsVO);
    }

    /**
     * 进行接取订单
     * @param ordersCancelDTO
     * @return
     */
    @PutMapping("/confirm")
    @ApiOperation("接单")
    public Result confirmOrders(@RequestBody OrdersConfirmDTO ordersCancelDTO) {
        orderService.confirmOrders(ordersCancelDTO);
        return Result.success();
    }

}
