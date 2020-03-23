package com.harmony.vcap.utils;

import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.io.File;
import java.time.Duration;

/**
 * @author wuxin
 */
public class VideoUtils {

    public static String durationString(Duration duration) {
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;
        return (hours > 10 ? hours : "0" + hours) + ":" +
                (minutes > 10 ? minutes : "0" + minutes) + ":" +
                (seconds > 10 ? seconds : "0" + seconds);
    }

    public static boolean isVideoFile(File file) {
        try {
            return file.isFile() && new VideoCapture(file.getAbsolutePath()).get(Videoio.CV_CAP_PROP_FRAME_COUNT) > 0;
        } catch (Exception e) {
            return false;
        }
    }

}
