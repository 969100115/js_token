package com.weixin.sdk.controller;

import com.weixin.sdk.bean.AccessTokenVO;
import com.weixin.sdk.bean.SignatureVO;
import com.weixin.sdk.bean.TicketVO;
import com.weixin.sdk.http.response.Result;
import com.weixin.sdk.http.response.ResultEnum;
import com.weixin.sdk.service.SDKService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("auth")
public class AuthController {


    @Autowired
    SDKService sdkService;

    @RequestMapping(value = "hello")
    public Result hello() {
        return new Result("hello world", ResultEnum.SUCCESS);
    }

    @RequestMapping(value = "getAccessToken")
    public String getAccessToken() {
        AccessTokenVO accessTokenVO = sdkService.refreshAccessToken();
        return accessTokenVO.getAccessToken();
    }

    @Scheduled(cron = "0 0 */1 * * *")
    @CachePut(value = "auth",key = "'ticket'")
    @RequestMapping(value = "getTicket")
    public String getTicket() {
        TicketVO ticketVO = sdkService.refreshTicket();
        log.info("refresh ticket ,ticket:{}",ticketVO.getTicket());
        return ticketVO.getTicket();


    }

    @RequestMapping(value = "getSignature")
    public Result getSignature(@RequestParam String url) throws Exception {
        SignatureVO signatureVO = sdkService.getSignature(url);
        return new Result(signatureVO, ResultEnum.SUCCESS);
    }
}
