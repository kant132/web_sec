package org.example;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

public class Main {
    static String path = "/Volumes/Elements/vedio/";
    static String[] videoArr = {"社 區 最 新 情 報", "新 片 首 發 每 天 更 新 同 步 日 韓",
            "有 趣 的 臺 灣 妹 妹 直 播", "avmans最新导航地址"};

    void del_other(File file) {
        if (file.isFile()) {
            String filename = file.getName();

            Arrays.stream(videoArr).forEach(x -> {
                if (x.equals(FilenameUtils.getBaseName(filename))) {
                    try {
                        System.out.println(filename);
                        FileUtils.delete(file);
                        return;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            Pattern p = Pattern.compile("jpg|url|txt|png|DS_Store|strings|chm|apk|html|mnt",
                    Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
            if (p.matcher(FilenameUtils.getExtension(filename)).find()) {
                try {
                    System.out.println(filename);
                    FileUtils.delete(file);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return;
        }
        Arrays.stream(file.listFiles()).forEach(x -> del_other(x));
    }


    static String destPath = "/Volumes/Elements/video/";

    void mv_av(File file) {
        if (file.isDirectory()) {
            Arrays.stream(file.listFiles()).forEach(x -> mv_av(x));
            return;
        }
        Pattern p = Pattern.compile("jufe|ssni|ssis|ipx|jufe|cawd|abw|adn|tek|mide|lafbd|marks|pred|abp|jul|fsdss|miaa",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        String filename = file.getName();
        if (p.matcher(FilenameUtils.getExtension(filename)).find()) {
            try {
                System.out.println(filename);
                FileUtils.moveFile(file, new File(destPath + "av/" + filename));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    void mv_nor(File file, String date) {
        if (file.isDirectory()) {
            Arrays.stream(file.listFiles()).forEach(x -> mv_nor(x, date));
            return;
        }
        Pattern p = Pattern.compile("avi|wmv|mpeg|mp4|m4v|mov|asf|flv|f4v|rmvb|rm|3gp|vob",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

        String filename = file.getName();
        String suffix = FilenameUtils.getExtension(filename);

        if (p.matcher(suffix).find()) {
            try {
                String dest = destPath + "nor/" + date + "_" + filename;
                System.out.println(filename + "------" + dest + "------");
                FileUtils.moveFile(file, new File(dest));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    public void repair() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd");
        String date = dtf.format(LocalDate.now());
        File path = new File(destPath + "nor/");

        Arrays.stream(path.listFiles()).forEach(x-> {
            String filename = x.getName();
            String suffix = FilenameUtils.getExtension(filename);
            String dest = destPath + "nor/" + date + "_" + UUID.randomUUID() + "." + suffix;
            System.out.println(filename + "------" + dest + "------");
            try {
                FileUtils.moveFile(x, new File(dest));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }

    public static void main(String[] args) {
        Main main = new Main();
        main.del_other(new File(path));
        main.mv_av(new File(path));

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd");
        String date = dtf.format(LocalDate.now());
        main.mv_nor(new File(path), date);
    }
}