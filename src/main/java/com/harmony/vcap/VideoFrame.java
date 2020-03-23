package com.harmony.vcap;

import com.harmony.vcap.utils.VideoUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.Duration;

/**
 * @author wuxin
 */
@ToString
@Getter
@AllArgsConstructor
public class VideoFrame {

    private Mat frame;
    private Duration frameAt;

    public int getWidth() {
        return frame.width();
    }

    public int getHeight() {
        return frame.height();
    }

    public Image toImage() {
        return HighGui.toBufferedImage(frame);
    }

    public BufferedImage toImage(int width, int height) {
        return toImage(width, height, Color.WHITE);
    }

    public BufferedImage toImage(int width, int height, Color backgroundColor) {
        Image originImage = toImage();
        BufferedImage zoomedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = zoomedImage.createGraphics();
        graphics.setBackground(backgroundColor);
        graphics.drawImage(originImage, 0, 0, width, height, null);
        return zoomedImage;
    }

    public String getDurationString() {
        return VideoUtils.durationString(frameAt);
    }

}
