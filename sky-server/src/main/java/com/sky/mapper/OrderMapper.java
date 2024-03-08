package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.*;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {



    /**
     * 插入订单数据
     * @param order
     */
    void insert(Orders order);

    /**
     * 根据状态查询数据
     * @param ordersPageQueryDTO
     * @return
     */
    Page<Orders> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);


    /**
     * 根据id查询订单
     * @param id
     * @return
     */
    @Select("select  * from  orders where id=#{id}")
    Orders getOrdersById(Long id);

    /**
     * 根据订单id修改来status
     * @param ordersConfirmDTO
     */
    void refuseOrders(OrdersConfirmDTO ordersConfirmDTO);

    /**
     * 根据订单id来获得status属性
     * @param id
     * @return
     */
    @Select("SELECT status FROM orders WHERE id = #{id}")
    Integer getStatusById(Long id);

    /**
     * 根据订单id填写拒单原因
     * @param ordersRejectionDTO
     */
    @Update("UPDATE orders SET rejection_reason = #{rejectionReason} WHERE id = #{id}")
    void UpRejectionReason(OrdersRejectionDTO ordersRejectionDTO);

    /**
     * 根据订单id填写取消原因 这个是未付款
     * @param ordersCancelDTO
     */
    @Update("UPDATE orders SET cancel_reason = #{cancelReason} WHERE id = #{id}")
    void UpCancelReason(OrdersCancelDTO ordersCancelDTO);

    /**
     *根据订单id来取消订单
     * @param ordersConfirmDTO
     */
    void upCancelOrders(OrdersConfirmDTO ordersConfirmDTO);

    /**
     * 统计订单的各种状态，使用@mapkey
     * 因为处理不好类型转换的异常，现在已经废弃
     */
    // @MapKey("status")
    // Map<Integer, Integer> orderStatusStatistics();

    /**
     * 根据状态统计订单数量
     * @param status
     */
    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(Integer status);

    /**
     *接取订单接口 同时也是根据id和状态来变成各种订单状态的代码
     * @param ordersCancelDTO
     */
    @Update("update orders set status=#{status} where id=#{id}")
    void confirmOrders(OrdersConfirmDTO ordersCancelDTO);


    /**
     * 分页条件查询并按下单时间排序
     * @param ordersPageQueryDTO
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     *修改订单的支付状态
     * @param ordersPaymentAlterDTO
     */
    @Update("update orders set pay_status=#{payStatus} where id=#{id}")
    void upPayOrders(OrdersPaymentAlterDTO ordersPaymentAlterDTO);



    /**
     * 根据订单状态和下单时间查询订单
     * @param status
     * @param orderTime
     */
    @Select("select * from orders where status = #{status} and order_time < #{orderTime}")
    List<Orders> getByStatusAndOrdertimeLT(Integer status, LocalDateTime orderTime);

    /**
     * 根据动态条件统计营业额
     * @param map
     */
    Double sumByMap(Map map);


}
