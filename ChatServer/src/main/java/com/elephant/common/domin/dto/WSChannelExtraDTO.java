package com.elephant.common.domin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Elephant-FZY
 * @Email: https://github.com/Elephant-BIG-LEG
 * @ClassName: WSReqTypeEnum
 * @Date: 2024/11/19/20:35
 * @Description: 记录和前端连接的一些映射信息
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSChannelExtraDTO {
    /**
     * 前端如果登录了，记录uid
     */
    private Long uid;
}
