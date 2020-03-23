package com.harmony.vcap;

import com.harmony.vcap.utils.VideoUtils;
import lombok.Getter;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.io.File;
import java.time.Duration;
import java.util.Iterator;

/**
 * @author wuxin
 */
public class Video {

    @Getter
    private final String videoName;
    @Getter
    private final double frameCount;
    @Getter
    private final double fps;
    @Getter
    private final File videoFile;
    @Getter
    private final Duration duration;
    @Getter
    private final Resolution resolution;

    public Video(String fileName) {
        this(new File(fileName));
    }

    public Video(File videoFile) {
        this(videoFile.getName(), videoFile);
    }

    public Video(String videoName, File videoFile) {
        this.videoName = videoName;
        this.videoFile = videoFile;
        VideoCapture vc = new VideoCapture(videoFile.getAbsolutePath());
        this.resolution = new Resolution(vc);
        this.frameCount = vc.get(Videoio.CV_CAP_PROP_FRAME_COUNT);
        this.fps = vc.get(Videoio.CV_CAP_PROP_FPS);
        this.duration = Duration.ofSeconds((long) (frameCount / fps));
    }

    public Iterator<VideoFrame> frameIterator(Duration interval) {
        VideoCapture vc = new VideoCapture(videoFile.getAbsolutePath());
        return new Iterator<VideoFrame>() {

            Duration current = Duration.ofSeconds(0);

            @Override
            public boolean hasNext() {
                return duration.toMillis() >= current.plus(interval).toMillis();
            }

            @Override
            public VideoFrame next() {
                current = current.plus(interval);
                vc.set(Videoio.CV_CAP_PROP_POS_MSEC, current.toMillis());
                Mat frame = new Mat();
                vc.read(frame);
                return new VideoFrame(frame, current);
            }

        };
    }

    public String getDurationString() {
        return VideoUtils.durationString(duration);
    }

}
