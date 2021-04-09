package com.weixin.sdk.bean;

import lombok.Data;

/**
 * @author Wenbo
 * @date 2021/4/3 11:30
 * @Email 969100115@qq.com
 * @phone 17621847037
 */
@Data
public class SignatureVO {
    String timestamp;
    String noncestr;
    String signature;

    public SignatureVO(String timestamp, String noncestr, String signature) {
        this.timestamp = timestamp;
        this.noncestr = noncestr;
        this.signature = signature;
    }
}
