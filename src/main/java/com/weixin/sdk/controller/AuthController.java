package com.weixin.sdk.controller;

import com.weixin.sdk.bean.AccessTokenVO;
import com.weixin.sdk.bean.SignatureVO;
import com.weixin.sdk.bean.TicketVO;
import com.weixin.sdk.http.response.Result;
import com.weixin.sdk.http.response.ResultEnum;
import com.weixin.sdk.service.SDKService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthController {


    @Autowired
    SDKService sdkService;

    @RequestMapping(value = "hello")
    public Result hello() {
        return new Result("hello world", ResultEnum.SUCCESS);
    }

    @Scheduled(cron = "0 0 */1 * * *")
    @RequestMapping(value = "getAccessToken")
    @CachePut(value = "auth",key = "'accesstoken'")
    public String getAccessToken() {
        AccessTokenVO accessTokenVO = sdkService.refreshAccessToken();
        return accessTokenVO.getAccessToken();
    }

    @RequestMapping(value = "getTicket")
    public Result getTicket() {
        TicketVO ticketVO = sdkService.getTicket();
        return new Result(ticketVO, ResultEnum.SUCCESS);


    }

    @RequestMapping(value = "getSignature")
    public Result getSignature() throws Exception {
        SignatureVO signatureVO = sdkService.getSignature("http://www.hidata.cc/");
        return new Result(signatureVO, ResultEnum.SUCCESS);
    }
}
