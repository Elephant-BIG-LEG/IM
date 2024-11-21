package com.elephant.common.server.adapter;

import com.elephant.common.domin.enums.WSBaseResp;
import com.elephant.common.domin.enums.WSReqTypeEnum;
import com.elephant.common.domin.vo.response.ws.WSLoginUrl;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;

/**
 * @Author: Elephant-FZY
 * @Email: https://github.com/Elephant-BIG-LEG
 * @ClassName: WsbSocketAdapter
 * @Date: 2024/11/21/19:31
 * @Description: ws消息适配器
 */
public class WebSocketAdapter {
    /**
     * 构建响应
     * @param wxMpQrCodeTicket
     * @return
     */
    public static WSBaseResp<?> buildResp(WxMpQrCodeTicket wxMpQrCodeTicket) {
        WSBaseResp<WSLoginUrl> resp = new WSBaseResp();
        resp.setType(WSReqTypeEnum.LOGIN.getType());
        resp.setData(String.valueOf(new WSLoginUrl(wxMpQrCodeTicket.getUrl())));
        return resp;
    }
}
