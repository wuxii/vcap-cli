package com.harmony.vcap;

import org.apache.commons.cli.*;
import org.apache.commons.cli.Option.Builder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.*;

import static com.harmony.vcap.utils.Logger.info;
import static com.harmony.vcap.utils.VideoUtils.isVideoFile;

/**
 * @author wuxin
 */
public class Capture {

    public static void main(String[] args) throws ParseException {
        Options options = options();
        DefaultParser parser = new DefaultParser();
        CommandLine cli = parser.parse(options, args);

        if (cli.hasOption("h")) {
            printHelpInfo();
            return;
        }

        boolean additionSubDirectory = cli.hasOption("addition");
        String source = cli.getOptionValue("source", args.length == 1 ? args[0] : ".");

        List<File> sources = getSourceFiles(new File(source), additionSubDirectory);
        if (sources.isEmpty()) {
            System.out.println("源目录下未找到视频文件.");
            return;
        }

        String fontColor = cli.getOptionValue("font-color", "BLACK");
        String fontSize = cli.getOptionValue("font-size", "24");
        String fontFamily = cli.getOptionValue("font-family", Font.SANS_SERIF);
        String column = cli.getOptionValue("column", "3");
        String width = cli.getOptionValue("width", "1800");
        String margin = cli.getOptionValue("margin", "5");
        String backgroundColor = cli.getOptionValue("background-color", "WHITE");
        String size = cli.getOptionValue("size", "60");
        String dest = cli.getOptionValue("dest");

        StoryboardWriter writer = new StoryboardWriterBuilder()
                .setFontColor(ofColor(fontColor))
                .setBackgroundColor(ofColor(backgroundColor))
                .setFont(new Font(fontFamily, Font.BOLD, Integer.parseInt(fontSize)))
                .setColumn(Integer.parseInt(column))
                .setWidth(Integer.parseInt(width))
                .setMarginWidth(Integer.parseInt(margin))
                .build();

        sources.forEach(e -> {
            info("处理视频文件: " + e.getAbsolutePath());
            Video video = new Video(e);
            BufferedImage image = writer.createStoryboard(video, Integer.parseInt(size));
            String pictureName = getPictureName(video);
            File destFile = dest == null
                    ? new File(e.getParentFile(), pictureName)
                    : new File(dest, pictureName);
            writeImageFile(image, destFile);
            info("创建分镜版文件: " + destFile.getAbsolutePath());
        });
    }

    private static void printHelpInfo() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("vcap", options());
    }

    private static void writeImageFile(BufferedImage image, File file) {
        File parentFile = file.getParentFile();
        if (parentFile.isDirectory() || parentFile.mkdirs()) {
            try {
                ImageIO.write(image, "jpg", file);
            } catch (IOException e) {
                System.out.println("无法将图片写入目标文件, " + file.getAbsolutePath());
            }
        } else {
            System.out.println("无法创建输出目录, " + parentFile.getAbsolutePath());
        }
    }

    private static String getPictureName(Video video) {
        String videoName = video.getVideoName();
        int index = videoName.lastIndexOf(".");
        return (index < 0 ? videoName : videoName.substring(0, index)) + ".jpg";
    }

    private static List<File> getSourceFiles(File source, boolean additionSubDirectory) {
        if (source.isFile() && isVideoFile(source)) {
            return Collections.singletonList(source);
        }
        List<File> result = new ArrayList<>();
        List<File> subDirectory = new ArrayList<>();
        File[] files = source.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && isVideoFile(file)) {
                    result.add(file);
                } else if (file.isDirectory()) {
                    subDirectory.add(file);
                }
            }
        }
        if (additionSubDirectory && !subDirectory.isEmpty()) {
            for (File file : subDirectory) {
                result.addAll(findSourceFiles(file));
            }
        }
        return result;
    }

    private static List<File> findSourceFiles(File file) {
        if (isVideoFile(file)) {
            return Collections.singletonList(file);
        }
        List<File> files = new ArrayList<>();
        File[] subFiles = file.listFiles();
        if (subFiles != null) {
            for (File subFile : subFiles) {
                files.addAll(findSourceFiles(subFile));
            }
        }
        return files;
    }

    public static Options options() {
        return new Options()
                .addOption(
                        newOption().hasArg().longOpt("font-name").desc("字体名称").build()
                )
                .addOption(
                        newOption().hasArg().longOpt("font-size").desc("字体大小").build()
                )
                .addOption(
                        newOption().hasArg().longOpt("font-color").desc("字体颜色").build()
                )
                .addOption(
                        newOption("s").hasArg().argName("s").longOpt("source").desc("源文件目录").build()
                )
                .addOption(
                        newOption("d").hasArg().longOpt("dest").desc("源文件目录").build()
                )
                .addOption(
                        newOption("c").hasArg().longOpt("column").desc("分镜的列数").build()
                )
                .addOption(
                        newOption("w").hasArg().longOpt("width").desc("画板宽").build()
                )
                .addOption(
                        newOption().hasArg().longOpt("margin").desc("分镜间的边距").build()
                )
                .addOption(
                        newOption().hasArg().longOpt("size").desc("分镜数, 总截取帧数(按总帧数平均截取).").build()
                )
                .addOption(
                        newOption().hasArg().longOpt("background-color").desc("背景色").build()
                )
                .addOption(
                        newOption("h").longOpt("help").desc("帮助信息").build()
                )
                .addOption(
                        newOption().longOpt("addition").desc("迭代处理子文件夹").build()
                );
    }

    private static Builder newOption() {
        return Option.builder();
    }

    private static Builder newOption(String name) {
        return Option.builder(name);
    }

    private static Color ofColor(String name) {
        return colorMap.getOrDefault(name, Color.getColor(name));
    }

    public static Map<String, Color> colorMap = new HashMap<>();

    static {
        Class<Color> colorClass = Color.class;
        for (Field field : colorClass.getDeclaredFields()) {
            if (field.getType() == Color.class) {
                try {
                    Color c = (Color) field.get(colorClass);
                    colorMap.put(field.getName(), c);
                } catch (Exception e) {
                    // ignore
                }
            }
        }
    }

}
