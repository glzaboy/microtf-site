package com.microtf.framework.utils;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

/**
 * 图片工具
 * @author glzaboy
 */
@Slf4j
@SuppressWarnings("unused")
public class ImageUtil {

    /**
     * 彩色图片转黑白转变片
     * @param source 采购图片源
     * @return 黑白图片
     */
    public static BufferedImage grayImage(BufferedImage source) {
        BufferedImage grayImage = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_BYTE_GRAY);//重点，技巧在这个参数BufferedImage.TYPE_BYTE_GRAY
        Graphics graphics = grayImage.getGraphics();
        graphics.drawImage(source,0,0,source.getWidth(),source.getHeight(),null);
        return grayImage;
    }
    public static BufferedImage byte2BufferedImage(byte[] fileByte) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileByte);
        return ImageIO.read(byteArrayInputStream);
    }
    public static BufferedImage file2BufferedImage(File file) throws IOException {
        return ImageIO.read(file);
    }

    /**
     * 图片空白裁剪
     * @param fileByte 二进步
     * @return 生成好的图片文件
     * @throws IOException 计取二进制出错
     */
    public static BufferedImage compress(byte[] fileByte) throws IOException {
        return compress(byte2BufferedImage(fileByte));
    }
    /**
     * 图片空白裁剪
     * @param file 文件
     * @return 生成好的图片文件
     * @throws IOException 计取二进制出错
     */
    public static BufferedImage compress(File file) throws IOException {
        return compress(file2BufferedImage(file));
    }
    /**
     * 图片空白裁剪
     * @param image 图片BufferedImage
     * @return 生成好的图片文件
     */
    public static BufferedImage compress(BufferedImage image) {
        int x, y, w, h;
        x = 0;
        y = 0;
        w = image.getWidth();
        h = image.getHeight();
        log.info("原始文件w{},h{}", w, h);
        for (; x < w; x++) {
            int hasColor = 0;
            for (int i = 0; i < h; i++) {
                int rgb = image.getRGB(x, i);
                Color color = new Color(rgb, true);
                if (color.getAlpha() > 0) {
                    hasColor = 1;
                    break;
                }
            }
            if (hasColor > 0) {
                break;
            }
        }
        //y++
        for (; y < h; y++) {
            int hasColor = 0;
            for (int i = x; i < w; i++) {
                int rgb = image.getRGB(i, y);
                Color color = new Color(rgb, true);
                if (color.getAlpha() > 0) {
                    hasColor = 1;
                    break;
                }
            }
            if (hasColor > 0) {
                break;
            }
        }

        //w--
        w--;
        for (; x < w; w--) {
            int hasColor = 0;
            for (int i = y; i < h; i++) {
                int rgb = image.getRGB(w, i);
                Color color = new Color(rgb, true);
                if (color.getAlpha() > 0) {
                    hasColor = 1;
                    break;
                }
            }
            if (hasColor > 0) {
                break;
            }
        }
        //h--
        h--;
        for (; y < h; h--) {
            int hasColor = 0;
            for (int i = x; i < w; i++) {
                int rgb = image.getRGB(i, h);
                Color color = new Color(rgb, true);
                if (color.getAlpha() > 0) {
                    hasColor = 1;
                    break;
                }
            }
            if (hasColor > 0) {
                break;
            }
        }
        log.info("图片内容从x{},y{},wx{},hy{},w{},h{}", x, y, w, h, w - x, h - y);
        float nh = h - y;
        float nw = w - x;
        if (nw > 400.0f) {
            nw = 400.0f;
            nh = nw / ((float) (w - x) / (h - y));
        } else if (nh > 600.0f) {
            nw = ((float) (w - x) / (h - y)) * 600.0f;
        }
        log.info("需要生成图片大小w{},h{}", nw, nh);
        BufferedImage bufferedImage = new BufferedImage((int) nw, (int) nh, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = bufferedImage.getGraphics();
        graphics.drawImage(image, 0, 0, (int) nw, (int) nh, x, y, w, h, null);
        return bufferedImage;
    }

}
