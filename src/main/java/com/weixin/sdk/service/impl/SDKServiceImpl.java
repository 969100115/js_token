package com.weixin.sdk.service.impl;

import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson.JSONObject;
import com.weixin.sdk.bean.AccessTokenVO;
import com.weixin.sdk.bean.SignatureVO;
import com.weixin.sdk.bean.TicketVO;
import com.weixin.sdk.common.SecuritySHA1Utils;
import com.weixin.sdk.http.okhttp.Okhttp;
import com.weixin.sdk.service.SDKService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @author Wenbo
 * @date 2021/4/3 10:58
 * @Email 969100115@qq.com
 * @phone 17621847037
 */
@Service
public class SDKServiceImpl implements SDKService {

    String appId;
    String secret;

    @Override
    @Cacheable(value = {"accessToken"},key = "targetClass + methodName +#p0")
    public AccessTokenVO getAccessToken() {
        String result = Okhttp.builder(true)
                .url("https://api.weixin.qq.com/cgi-bin/token")
                .urlParam("grant_type","client_credential")
                .urlParam("appid",appId)
                .urlParam("secret",secret)
                .get().excut();

        JSONObject jsonObject = JSONObject.parseObject(result);
        AccessTokenVO accessTokenVO = JSONObject.parseObject(result,AccessTokenVO.class);
        return accessTokenVO;

    }

    @Override
    public TicketVO getTicket() {
        String accessToken = "";
        String result = Okhttp.builder(true)
                .url("https://api.weixin.qq.com/cgi-bin/ticket/getticket")
                .urlParam("type","jsapi")
                .urlParam("access_token",accessToken)
                .get().excut();
        TicketVO ticketVO = JSONObject.parseObject(result,TicketVO.class);
        return ticketVO;
    }

    @Override
    public SignatureVO getSignature(String url) throws Exception {
        Integer timestamp = (int)(System.currentTimeMillis() / 1000);
        String noncestr = MD5.create().digestHex(timestamp.toString());
        String ticket = "";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("jsapi_ticket=")
                .append(ticket)
                .append("&noncestr=")
                .append(noncestr)
                .append("&timestamp=")
                .append(timestamp)
                .append("&url=")
                .append(url);

        String signature = SecuritySHA1Utils.shaEncode(stringBuilder.toString());
        SignatureVO signatureVO = new SignatureVO(timestamp.toString(),noncestr,signature);
        return signatureVO;
    }

}
