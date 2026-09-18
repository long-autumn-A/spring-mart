package com.enshi.springmart.utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.imageio.ImageIO;

/**
 * 登录验证码图片生成工具。
 * 只用 JDK 自带的 Java2D + ImageIO，不额外引第三方验证码库，
 * 生成的图片直接转成 data:image/png;base64,... 给前端 &lt;img&gt; 用。
 */
public final class CaptchaImageUtils {

    // 去掉了 0/O、1/I/L 这些肉眼容易看混的字符
    private static final char[] CHARS = "ABCDEFGHJKMNPQRSTUVWXY23456789".toCharArray();
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int WIDTH = 120;
    private static final int HEIGHT = 40;
    private static final int CODE_LENGTH = 4;

    private CaptchaImageUtils() {
    }

    /** 随机生成一个 4 位验证码答案 */
    public static String randomCode() {
        return randomCode(CODE_LENGTH);
    }

    public static String randomCode(int length) {
        StringBuilder code = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            code.append(CHARS[RANDOM.nextInt(CHARS.length)]);
        }
        return code.toString();
    }

    /** 把验证码答案画成图片，返回可直接放进 src 的 base64 字符串 */
    public static String toBase64Png(String code) {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        try {
            // 背景
            g.setColor(new Color(250, 245, 238));
            g.fillRect(0, 0, WIDTH, HEIGHT);

            // 干扰线
            g.setStroke(new BasicStroke(1.2f));
            for (int i = 0; i < 8; i++) {
                g.setColor(randomColor(140, 220));
                g.drawLine(RANDOM.nextInt(WIDTH), RANDOM.nextInt(HEIGHT),
                        RANDOM.nextInt(WIDTH), RANDOM.nextInt(HEIGHT));
            }

            // 噪点
            for (int i = 0; i < 60; i++) {
                g.setColor(randomColor(150, 230));
                g.fillRect(RANDOM.nextInt(WIDTH), RANDOM.nextInt(HEIGHT), 1, 1);
            }

            // 字符：位置和倾斜角度都加一点随机，不那么容易被脚本切图识别
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
            int step = WIDTH / (code.length() + 1);
            for (int i = 0; i < code.length(); i++) {
                AffineTransform origin = g.getTransform();
                g.rotate((RANDOM.nextDouble() - 0.5) * 0.5, step * (i + 1), HEIGHT / 2.0);
                g.setColor(randomColor(40, 140));
                g.drawString(String.valueOf(code.charAt(i)), step * (i + 1) - 9, HEIGHT / 2 + 11);
                g.setTransform(origin);
            }
        } finally {
            g.dispose();
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", out);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(out.toByteArray());
        } catch (IOException e) {
            throw new UncheckedIOException("验证码图片生成失败", e);
        }
    }

    private static Color randomColor(int min, int max) {
        int range = max - min;
        return new Color(min + RANDOM.nextInt(range), min + RANDOM.nextInt(range), min + RANDOM.nextInt(range));
    }
}
