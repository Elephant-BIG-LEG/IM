package com.elephant.user.domin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author: Elephant-FZY
 * @Email: 1085062843@qq.com
 * @ClassName: WSReqTypeEnum
 * @Date: 2024/11/19/20:35
 * @Description: 基本请求枚举类
 */
@Getter
@AllArgsConstructor
public enum WSReqTypeEnum {
    LOGIN(1, "请求登录二维码"),
    HEARTBEAT(2, "心跳包"),
    AUTHORIZE(3, "登录认证");

    //类型
    private final Integer type;
    //类型描述
    private final String desc;

    //缓存
    private static Map<Integer, WSReqTypeEnum> cache;

    //类加载到时候就初始化 时间复杂度也n(1)
    static {
        cache = Arrays.stream(WSReqTypeEnum.values())
                .collect(Collectors.toMap(WSReqTypeEnum::getType, Function.identity()));
    }

    public static WSReqTypeEnum of(Integer type) {
        return cache.get(type);
    }
}