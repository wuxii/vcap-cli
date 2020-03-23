package com.harmony.vcap;

import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author wuxin
 */
@Setter(AccessLevel.PACKAGE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StoryboardWriter {

    /**
     * 列数
     */
    int column;
    /**
     * 画布总
     */
    int width;
    /**
     * 边距
     */
    int marginWidth;
    /**
     * 单镜的宽
     */
    int frameWidth;
    /**
     * 背景色
     */
    Color backgroundColor;
    /**
     * 字体
     */
    Font font;
    /**
     * 字体颜色
     */
    Color fontColor;

    /**
     * 默认字体大小
     */
    int defaultFontSize = 24;

    /**
     * 创建分镜板
     *
     * @param video 视频信息
     * @return 分镜板
     */
    public BufferedImage createStoryboard(Video video, int size) {
        long seconds = video.getDuration().getSeconds();
        int maxSize = (int) Math.min(size, seconds);
        Duration interval = Duration.ofSeconds(seconds / maxSize);
        List<VideoFrame> storyboardFrames = getStoryboardFrames(video, interval);
        Resolution originResolution = video.getResolution();

        BufferedImage header = writeStoryboardHeader(video);
        BufferedImage content = writeStoryboardContent(storyboardFrames, originResolution);

        int height = header.getHeight() + content.getHeight();
        int width = this.width;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.drawImage(header, 0, 0, null);
        graphics.drawImage(content, 0, header.getHeight(), null);
        return image;
    }

    public BufferedImage writeStoryboardHeader(Video video) {
        List<String> lines = buildVideoHeaderLines(video);
        int lineHeight = (font != null ? font.getSize() : defaultFontSize);
        int lineSize = lines.size();

        int width = this.width;
        int height = lineHeight * lineSize + marginWidth * (lineSize + 1);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = createGraphics(image);

        int writeHeight = marginWidth + lineHeight;
        for (String line : lines) {
            graphics.drawString(line, marginWidth, writeHeight);
            writeHeight += lineHeight + marginWidth;
        }
        graphics.dispose();
        return image;
    }

    private List<String> buildVideoHeaderLines(Video video) {
        Resolution resolution = video.getResolution();
        return Arrays.asList(
                "名称: " + video.getVideoName(),
                "文件大小: " + video.getDurationString(),
                "分辨率: " + (int) resolution.getWidth() + "x" + (int) resolution.getHeight(),
                "时长: " + video.getDurationString()
        );
    }

    private BufferedImage writeStoryboardContent(List<VideoFrame> frames, Resolution originResolution) {
        int frameSize = frames.size();
        VideoFrame firstFrame = frames.get(0);
        int frameWidth = this.frameWidth;
        int frameHeight = getRelativeFrameHeight(firstFrame);
        int row = (int) Math.round(1.0 * frameSize / column);

        int width = this.width;
        int height = marginWidth * (row + 1) + row * frameHeight;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setBackground(backgroundColor);
        graphics.fillRect(0, 0, width, height);
        int x = marginWidth, y = marginWidth;
        for (int i = 0; i < frames.size(); i++) {
            VideoFrame frame = frames.get(i);
            BufferedImage frameImage = frame.toImage(frameWidth, frameHeight, backgroundColor);
            Graphics2D frameGraphics = frameImage.createGraphics();
            if (font != null) {
                frameGraphics.setFont(new Font(font.getName(), Font.PLAIN, 12));
            }
            frameGraphics.drawString(frame.getDurationString(), 10, 17);
            graphics.drawImage(frameImage, x, y, null);
            if ((i + 1) % column == 0) {
                x = marginWidth;
                y = y + marginWidth + frameHeight;
            } else {
                x = x + marginWidth + frameWidth;
            }
        }
        return image;
    }

    private int getRelativeFrameHeight(VideoFrame firstFrame) {
        int height = firstFrame.getHeight();
        int width = firstFrame.getWidth();
        return (int) Math.round(height * (1.0 * frameWidth / width));
    }

    private List<VideoFrame> getStoryboardFrames(Video video, Duration interval) {
        Iterator<VideoFrame> frameIterator = video.frameIterator(interval);
        List<VideoFrame> frames = new ArrayList<>();
        frameIterator.forEachRemaining(frames::add);
        return frames;
    }

    private Graphics2D createGraphics(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        Graphics2D graphics = image.createGraphics();
        if (backgroundColor != null) {
            graphics.setBackground(backgroundColor);
            graphics.fillRect(0, 0, width, height);
        }
        if (fontColor != null) {
            graphics.setPaint(fontColor);
        }
        if (font != null) {
            graphics.setFont(font);
        }
        return graphics;
    }

}
