package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 订单
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;

    /**
     * 用户下单
     *
     * @param ordersSubmitDTO
     * @return
     */
    @Transactional
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        //异常情况的处理（收货地址为空、购物车为空）
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);

        //查询当前用户的购物车数据
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);
        if (shoppingCartList == null || shoppingCartList.size() == 0) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        //构造订单数据
        Orders order = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, order);
        order.setPhone(addressBook.getPhone());
        order.setAddress(addressBook.getDetail());
        order.setConsignee(addressBook.getConsignee());
        UUID uuid = UUID.randomUUID();

        order.setNumber(uuid.toString());
        order.setUserId(userId);
        order.setStatus(Orders.PENDING_PAYMENT);
        order.setPayStatus(Orders.UN_PAID);
        order.setOrderTime(LocalDateTime.now());

        //向订单表插入1条数据
        orderMapper.insert(order);
        //订单明细数据
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (ShoppingCart cart : shoppingCartList) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(order.getId());
            orderDetailList.add(orderDetail);
        }
        //向明细表插入n条数据
        orderDetailMapper.insertBatch(orderDetailList);

        //清理购物车中的数据
        shoppingCartMapper.deleteByUserId(userId);

        //封装返回结果
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(order.getId())
                .orderNumber(order.getNumber())
                .orderAmount(order.getAmount())
                .orderTime(order.getOrderTime())
                .build();
        return orderSubmitVO;
    }

    /**
     * 根据条件进行分页查询
     *
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Page<Orders> page = orderMapper.conditionSearch(ordersPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        //调用微信支付接口，生成预支付交易单
        JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal(0.01), //支付金额，单位 元
                "苍穹外卖订单", //商品描述
                user.getOpenid() //微信用户的openid
        );

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        return vo;
    }

    /**
     * 支付成功和开始修改订单状态
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
    }

    /**
     * 订单支付修改版
     *
     * @param ordersPaymentDTO
     * @return
     */
    @Override
    public OrdersSubmitModifyDTO submitOrderModify(OrdersPaymentDTO ordersPaymentDTO) {
        OrdersSubmitModifyDTO ordersSubmitModifyDTO = new OrdersSubmitModifyDTO();
        paySuccess(ordersPaymentDTO.getOrderNumber());
        /*  获取预计送达时间*/
        Orders ordersDB = orderMapper.getByNumber(ordersPaymentDTO.getOrderNumber());
        ordersSubmitModifyDTO.setEstimatedDeliveryTime(ordersDB.getEstimatedDeliveryTime());
        return ordersSubmitModifyDTO;
    }

    /**
     * 查询订单详情
     *
     * @param id
     * @return
     */
    public OrderVO details(Long id) {
        // 根据id查询订单
        Orders orders = orderMapper.getOrdersById(id);

        /* 这样写减少一个方法 */
        Long orderId=orders.getId();
        // 查询该订单对应的菜品/套餐明细
        List<OrderDetail> orderDetailList = orderDetailMapper.getDetailByOrderId(orderId);

        // 将该订单及其详情封装到OrderVO并返回
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDetailList(orderDetailList);

        return orderVO;
    }



    /**
     * 拒绝接收订单
     *
     * @param ordersRejectionDTO
     */
    @Transactional
    public void refuseOrders(OrdersRejectionDTO ordersRejectionDTO) {
        /* 业务逻辑需要判断用户是否已经把钱付了 */
        OrdersConfirmDTO ordersConfirmDTO = new OrdersConfirmDTO();
        /* 先填写不接单原因 */
        orderMapper.UpRejectionReason(ordersRejectionDTO);
        /* 取消订单代码，如果真付钱了应该有更多操作 */
        ordersConfirmDTO.setId(ordersRejectionDTO.getId());

        /* 获取当前订单的状态 */
        Integer status = orderMapper.getStatusById(ordersRejectionDTO.getId());

        // 根据状态设置要更新的状态
        if (status != null) {
            if (status == 1) {
                ordersConfirmDTO.setStatus(6); // 待接单状态修改为已取消状态
            } else if (status == 2) {
                ordersConfirmDTO.setStatus(7); // 待接单状态修改为退款状态
            }
        }
        orderMapper.refuseOrders(ordersConfirmDTO);
    }

    @Override
    public void cancelOrders(OrdersCancelDTO ordersCancelDTO) {
        /* 先填写取消单子原因 */
        orderMapper.UpCancelReason(ordersCancelDTO);

        /* 业务逻辑需要判断用户是否已经把钱付了 */
        OrdersConfirmDTO ordersConfirmDTO = new OrdersConfirmDTO();
        ordersConfirmDTO.setId(ordersCancelDTO.getId());

        /* 取消订单代码，如果真付钱了应该有更多操作 */
        /* 获取当前订单的状态 */
        Integer status = orderMapper.getStatusById(ordersCancelDTO.getId());

        // 根据状态设置要更新的状态
        //本段代码其实没什么必要的，因为前端发来的请求虽然都是取消订单请求但居然不一样。
        // 上面也是如此。目前我还不知道为什么要这么设计。可能一个是取消订单一个是不接订单吧
        if (status != null) {
            if (status == 1) {
                ordersConfirmDTO.setStatus(6); // 待接单状态修改为已取消状态
            } else if (status == 2) {
                ordersConfirmDTO.setStatus(7); // 待接单状态修改为退款状态
            }
        }

        orderMapper.upCancelOrders(ordersConfirmDTO);
    }



    // /**
    //  * 一次sql查询订单多种状态，然后进行赋值
    //  * 处理不了类型转换异常，已废弃
    //  */
    // @Override
    // public OrderStatisticsVO statisticsOrders() {
    //     OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
    //
    //     // 获取订单状态统计信息
    //     Map<Integer, Integer> statusStatistics = orderMapper.orderStatusStatistics();
    //     if (statusStatistics != null) {
    //         // 遍历查询结果，根据不同的状态值设置对应的属性值
    //         for (Map.Entry<Integer, Integer> entry : statusStatistics.entrySet()) {
    //             Integer status = entry.getKey();
    //             Integer count = entry.getValue();
    //             switch (status) {
    //                 case 2:
    //                     orderStatisticsVO.setToBeConfirmed(count);
    //                     break;
    //                 case 3:
    //                     orderStatisticsVO.setConfirmed(count);
    //                     break;
    //                 case 4:
    //                     orderStatisticsVO.setDeliveryInProgress(count);
    //                     break;
    //                 default:
    //                     // 处理其他状态值
    //                     break;
    //             }
    //         }
    //     }
    //
    //     return orderStatisticsVO;
    // }
    /**
     * 各个状态的订单数量统计
     *
     * @return
     */
    public OrderStatisticsVO statisticsOrders() {
        // 根据状态，分别查询出待接单、待派送、派送中的订单数量
        Integer toBeConfirmed = orderMapper.countStatus(Orders.TO_BE_CONFIRMED);
        Integer confirmed = orderMapper.countStatus(Orders.CONFIRMED);
        Integer deliveryInProgress = orderMapper.countStatus(Orders.DELIVERY_IN_PROGRESS);

        // 将查询出的数据封装到orderStatisticsVO中响应
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        orderStatisticsVO.setToBeConfirmed(toBeConfirmed);
        orderStatisticsVO.setConfirmed(confirmed);
        orderStatisticsVO.setDeliveryInProgress(deliveryInProgress);
        return orderStatisticsVO;
    }

    /**
     * 接取订单的接口
     * @param ordersCancelDTO
     */
    @Override
    public void confirmOrders(OrdersConfirmDTO ordersCancelDTO) {
        ordersCancelDTO.setStatus(3);
        orderMapper.confirmOrders(ordersCancelDTO);
    }
    /**
     * 派送订单id
     * @param id
     */
    @Override
    public void deliveryOrders(Long id) {
        OrdersConfirmDTO ordersConfirmDTO =new OrdersConfirmDTO();
        ordersConfirmDTO.setId(id);
        ordersConfirmDTO.setStatus(4);
        orderMapper.confirmOrders(ordersConfirmDTO);
    }

    /**
     * 完成订单
     * @param id
     */
    @Override
    public void completeOrders(Long id) {
        OrdersConfirmDTO ordersConfirmDTO =new OrdersConfirmDTO();
        ordersConfirmDTO.setId(id);
        ordersConfirmDTO.setStatus(5);
        orderMapper.confirmOrders(ordersConfirmDTO);
    }

    /**
     * 用户端订单分页查询
     * @param pageNum
     * @param pageSize
     * @param status
     * @return
     */
    public PageResult pageQueryUser(int pageNum, int pageSize, Integer status) {
        // 设置分页
        PageHelper.startPage(pageNum, pageSize);

        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        ordersPageQueryDTO.setStatus(status);

        // 分页条件查询
        Page<Orders> page = orderMapper.pageQuery(ordersPageQueryDTO);

        List<OrderVO> list = new ArrayList();

        // 查询出订单明细，并封装入OrderVO进行响应
        if (page != null && page.getTotal() > 0) {
            for (Orders orders : page) {
                Long orderId = orders.getId();// 订单id

                // 查询订单明细
                List<OrderDetail> orderDetails = orderDetailMapper.getDetailByOrderId(orderId);

                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                orderVO.setOrderDetailList(orderDetails);

                list.add(orderVO);
            }
        }
        return new PageResult(page.getTotal(), list);
    }
}