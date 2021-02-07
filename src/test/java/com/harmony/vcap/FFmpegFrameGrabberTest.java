package com.harmony.vcap;

import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @author wuxin
 */
@Slf4j
public class FFmpegFrameGrabberTest {

    private static final Java2DFrameConverter converter = new Java2DFrameConverter();

    private File parentDir;

    @Before
    public void setup() {
        parentDir = new File("./target", UUID.randomUUID().toString());
        parentDir.mkdirs();
        log.info("image will write to: {}", parentDir);
    }

    @Test
    public void testGrabber() throws IOException {
        File file = new File("./videos/sample-mp4-file.mp4");
        FFmpegFrameGrabber frameGrabber = FFmpegFrameGrabber.createDefault(file);
        frameGrabber.start();
        int lengthInFrames = frameGrabber.getLengthInFrames();
        for (int i = 0; i < lengthInFrames; i = i + 15) {
            frameGrabber.setFrameNumber(i);
            log.info("current frame number: {}, timestamp: {}", frameGrabber.getFrameNumber(), frameGrabber.getTimestamp());
            Frame frame = frameGrabber.grabImage();
            writeFrame(frame, i);
        }
        frameGrabber.stop();
    }

    public void writeFrame(Frame frame, int i) throws IOException {
        File file = new File(parentDir, i + ".jpg");
        BufferedImage image = converter.convert(frame);
        ImageIO.write(image, "jpg", file);
    }

}
