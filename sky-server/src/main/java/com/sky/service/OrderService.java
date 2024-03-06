package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
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
}
