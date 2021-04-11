package com.weixin.sdk.service;

import com.weixin.sdk.bean.AccessTokenVO;
import com.weixin.sdk.bean.SignatureVO;
import com.weixin.sdk.bean.TicketVO;

/**
 * @author Wenbo
 * @date 2021/4/3 10:58
 * @Email 969100115@qq.com
 * @phone 17621847037
 */
public interface SDKService {

    AccessTokenVO refreshAccessToken();

    TicketVO getTicket();

    String getAccessToken();
    SignatureVO getSignature(String url) throws Exception;
}
