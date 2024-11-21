package com.elephant.common.domin.enums;

import lombok.Data;

/**
 * @Author: Elephant-FZY
 * @Email: https://github.com/Elephant-BIG-LEG
 * @ClassName: WSBaseReq
 * @Date: 2024/11/19/20:32
 * @Description: 基本请求
 */
@Data
public class WSBaseResp<T>{
    /**
     * 请求类型  1.请求登录扫码 2.心跳机制
     * @see com.elephant.common.domin.enums.WSReqTypeEnum
     */
    private Integer type;

    /**
     * 请求结果
     */
    private String data;
}
