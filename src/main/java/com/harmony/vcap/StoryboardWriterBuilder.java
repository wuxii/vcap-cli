package com.harmony.vcap;

import lombok.Setter;
import lombok.experimental.Accessors;

import java.awt.*;

/**
 * @author wuxin
 */
@Accessors(chain = true)
public class StoryboardWriterBuilder {

    /**
     * 列数
     */
    @Setter
    private int column;
    /**
     * 画布总
     */
    @Setter
    private int width;
    /**
     * 边距
     */
    @Setter
    private int marginWidth;

    @Setter
    private Color backgroundColor;

    @Setter
    private Font font;

    @Setter
    private Color fontColor;

    @Setter
    private int defaultFontSize = 24;

    public StoryboardWriter build() {
        StoryboardWriter writer = new StoryboardWriter();
        writer.setColumn(column);
        writer.setWidth(width);
        writer.setMarginWidth(marginWidth);
        writer.setFrameWidth((width - (column + 1) * marginWidth) / column);
        writer.setBackgroundColor(backgroundColor);
        writer.setFont(font);
        writer.setFontColor(fontColor);
        writer.setDefaultFontSize(defaultFontSize);
        return writer;
    }

}
