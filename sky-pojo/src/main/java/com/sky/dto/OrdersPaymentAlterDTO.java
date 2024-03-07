package com.sky.dto;

import lombok.Data;

@Data
public class OrdersPaymentAlterDTO {
    // 根据id来查询
    private Long id;

    //退款字段设置
    private Integer payStatus;

}
