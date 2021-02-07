package com.harmony.vcap;

import com.harmony.vcap.utils.VideoUtils;
import org.bytedeco.ffmpeg.ffmpeg;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.opencv_java;
import org.junit.Test;

import java.io.File;

/**
 * @author wuxin
 */
public class VcapTest {

    static {
        Loader.load(opencv_java.class);
        Loader.load(ffmpeg.class);
    }

    @Test
    public void testAllFormatVideos() {
        File[] videoFiles = new File("./videos").listFiles();
        for (File videoFile : videoFiles) {
            if (!VideoUtils.isVideoFile(videoFile)) {
                System.out.println("读视频文件失败, " + videoFile);
            }
        }
    }

    @Test
    public void testMp4() {
        File file = new File("./videos/sample-mp4-file.mp4");
        readVideo(file);
    }

    public Video readVideo(File file) {
        return new Video(file);
    }

}
