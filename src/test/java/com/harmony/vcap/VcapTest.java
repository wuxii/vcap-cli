package com.harmony.vcap;

import com.harmony.vcap.utils.VideoUtils;
import nu.pattern.OpenCV;
import org.junit.Test;

import java.io.File;

/**
 * @author wuxin
 */
public class VcapTest {

    static {
        OpenCV.loadLocally();
    }

    @Test
    public void testAllFormatVideos() {
        File[] videoFiles = new File("./videos").listFiles();
        for (File videoFile : videoFiles) {
            if (!VideoUtils.isVideoFile(videoFile)) {
                System.out.println("去读视频文件失败, " + videoFile);
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
