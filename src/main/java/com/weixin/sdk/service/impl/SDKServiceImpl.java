package com.weixin.sdk.service.impl;

import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson.JSONObject;
import com.weixin.sdk.bean.AccessTokenVO;
import com.weixin.sdk.bean.SignatureVO;
import com.weixin.sdk.bean.TicketVO;
import com.weixin.sdk.common.SecuritySHA1Utils;
import com.weixin.sdk.common.exception.asserts.Assert;
import com.weixin.sdk.http.okhttp.Okhttp;
import com.weixin.sdk.http.response.ResultEnum;
import com.weixin.sdk.service.SDKService;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @author Wenbo
 * @date 2021/4/3 10:58
 * @Email 969100115@qq.com
 * @phone 17621847037
 */
@Service
@Slf4j
public class SDKServiceImpl implements SDKService {

    String appId = "wx58380c3983085a12";
    String secret = "11743b883916368dd073bc0f3c8e6f09";

    @Autowired
    SDKService sdkService;

    @Override
    public AccessTokenVO refreshAccessToken() {
        String result = Okhttp.builder(true)
                .url("https://api.weixin.qq.com/cgi-bin/token")
                .urlParam("grant_type","client_credential")
                .urlParam("appid",appId)
                .urlParam("secret",secret)
                .get().excut();
        AccessTokenVO accessTokenVO = JSONObject.parseObject(result,AccessTokenVO.class);
        if (StringUtils.isBlank(accessTokenVO.getAccessToken())){
            Assert.defaultAssert(ResultEnum.ACCESS_TOKEN_ERROR,result);
        }
        return accessTokenVO;
    }

    @Override
    public String getAccessToken(){
        String accessToken = refreshAccessToken().getAccessToken();
        log.info("refresh AccessToken ,accessToken:{}",accessToken);
        return accessToken;

    }

    @Override
    public TicketVO refreshTicket() {
        String accessToken = sdkService.getAccessToken();
        if (StringUtils.isBlank(accessToken)){
            Assert.defaultAssert(ResultEnum.ACCESS_TOKEN_ERROR);
        }
        log.info("ticket get AccessToken,accessToken :{} ",accessToken);
        String result = Okhttp.builder(true)
                .url("https://api.weixin.qq.com/cgi-bin/ticket/getticket")
                .urlParam("type","jsapi")
                .urlParam("access_token",accessToken)
                .get().excut();
        TicketVO ticketVO = JSONObject.parseObject(result,TicketVO.class);
        if (StringUtils.isBlank(ticketVO.getTicket())){
            Assert.defaultAssert(ResultEnum.TICKET_ERROR,result);
        }
        return ticketVO;
    }

    @Override
    @Cacheable(value = "auth",key = "'ticket'")
    public String getTicket(){
        String ticket = refreshTicket().getTicket();
        log.info("get ticket ,ticket:{}",ticket);
        return ticket;
    }

    @Override
    public SignatureVO getSignature(String url){
        Integer timestamp = (int)(System.currentTimeMillis() / 1000);
        String noncestr = MD5.create().digestHex(timestamp.toString());
        String ticket = sdkService.getTicket();
        log.info("signature get ticket,ticket :{} ",ticket);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("jsapi_ticket=")
                .append(ticket)
                .append("&noncestr=")
                .append(noncestr)
                .append("&timestamp=")
                .append(timestamp)
                .append("&url=")
                .append(url);

        String signature = null;
        try {
            signature = SecuritySHA1Utils.shaEncode(stringBuilder.toString());
        } catch (Exception e) {
            Assert.defaultAssert(ResultEnum.ENCRYPT_ERROR,stringBuilder.toString());
        }

        SignatureVO signatureVO = new SignatureVO(timestamp.toString(),noncestr,signature);
        return signatureVO;
    }

}
