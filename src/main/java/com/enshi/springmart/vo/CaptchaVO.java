package com.enshi.springmart.vo;

/**
 * 验证码接口返回给前端的内容。
 *
 * @param uuid  这次验证码的标识，登录时要原样带回来，后端靠它去 Redis 里取答案
 * @param image 已经编码好的图片（data:image/png;base64,...），前端直接塞进 img 的 src
 */
public record CaptchaVO(String uuid, String image) {
}
