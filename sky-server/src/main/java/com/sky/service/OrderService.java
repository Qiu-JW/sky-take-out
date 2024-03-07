package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {

    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 根据条件进行搜索订单
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);


    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /**
     * 订单支付个人修改版
     * @param ordersPaymentDTO
     * @return
     */
    OrdersSubmitModifyDTO submitOrderModify(OrdersPaymentDTO ordersPaymentDTO);

    /**
     * 根据id查询订单详情
     * @param id
     * @return
     */
    OrderVO details(Long id);

    /**
     * 拒绝接收订单
     * @param ordersRejectionDTO
     */
    void refuseOrders(OrdersRejectionDTO ordersRejectionDTO);

    /**
     * 取消订单
     * @param ordersCancelDTO
     */
    void cancelOrders(OrdersCancelDTO ordersCancelDTO);

    /**
     *统计各种类型订单的代码
     * @return
     */
    OrderStatisticsVO statisticsOrders();

    /**
     * 接单代码，修改订单状态即可
     * @param ordersCancelDTO
     */
    void confirmOrders(OrdersConfirmDTO ordersCancelDTO);

    /**
     * 派送订单id
     * @param id
     */
    void deliveryOrders(Long id);

    /**
     * 完成订单
     * @param id
     */
    void completeOrders(Long id);

    /**
     * 历史订单查询接口
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    PageResult pageQueryUser(int page, int pageSize, Integer status);

    /**
     * 取消订单
     * @param id
     */
    void userCancelById(Long id);

    /**
     * 再来一单代码
     * @param id
     */
    void repetition(Long id);
}
