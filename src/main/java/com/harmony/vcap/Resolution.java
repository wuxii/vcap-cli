package com.harmony.vcap;

import lombok.Getter;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

/**
 * @author wuxin
 */
@Getter
public class Resolution {

    double height;
    double width;
    double fps;

    public Resolution(VideoCapture vc) {
        this.fps = vc.get(Videoio.CAP_PROP_FPS);
        this.height = vc.get(Videoio.CAP_PROP_FRAME_HEIGHT);
        this.width = vc.get(Videoio.CAP_PROP_FRAME_WIDTH);
    }

}
