package com.elephant.user.domin.vo.request.user;

import lombok.Data;
import lombok.Getter;

/**
 * @Author: Elephant-FZY
 * @Email: 1085062843@qq.com
 * @ClassName: WSBaseReq
 * @Date: 2024/11/19/20:32
 * @Description: 基本请求
 */
@Data
public class WSBaseReq {
    /**
     * 请求类型  1.请求登录扫码 2.心跳机制
     * @see com.elephant.user.domin.enums.WSReqTypeEnum
     */
    private Integer type;

    /**
     * 请求结果
     */
    private String data;
}
