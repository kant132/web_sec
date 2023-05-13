package org.example;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

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

    void check_av(File file) {
        if (file.isDirectory()) {
            Arrays.stream(file.listFiles()).forEach(x -> check_av(x));
        }
        String filename = file.getName().toLowerCase();
        String name = filename.replace("hhd800.com@", "")
                .replace("_x1080x", "");
        if(!name.equals(filename)) {
            try {
                FileUtils.moveFile(file, new File(destPath + "av/" + name));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    void mv_av(File file) {
        if (file.isDirectory()) {
            Arrays.stream(file.listFiles()).forEach(x -> mv_av(x));
            return;
        }
        Pattern name = Pattern.compile("tysf|legsjapan|ipz|abw|hmn|dldss|jufe|ssni|ssis|ipx|jufe|cawd|abw|adn|tek|mide|lafbd|marks|pred|abp|jul|fsdss|miaa",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        Pattern suffix = Pattern.compile("avi|wmv|mpeg|mp4|m4v|mov|asf|flv|f4v|rmvb|rm|3gp|vob",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        String filename = file.getName().toLowerCase();
        if (suffix.matcher(FilenameUtils.getExtension(filename)).find() && name.matcher(filename).find()) {
            try {
                System.out.println(filename);
                FileUtils.moveFile(file, new File(destPath + "av/" + filename));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    static void mv_nor(File file, String date) {
        if (file.isDirectory()) {
            Arrays.stream(file.listFiles()).forEach(x -> mv_nor(x, date));
            return;
        }
        Pattern p = Pattern.compile("avi|wmv|mpeg|mp4|m4v|mov|asf|flv|f4v|rmvb|rm|3gp|vob",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

        String filename = file.getName();
        String suffix = FilenameUtils.getExtension(filename);

        if (p.matcher(suffix).find()) {
            String tmp = date;
            try {

                if (!date.contains(file.getParentFile().getName())) {
                    tmp = date + "_" + file.getParentFile().getName();
                }
                String dest = destPath + "nor/" + tmp + "_" + filename;
                System.out.println(filename + "------" + dest + "------");
                FileUtils.moveFile(file, new File(dest));
            } catch (IOException e) {
                String dest = destPath + "nor/" + tmp + "_" + new Random().nextInt() + "_" + filename;
                try {
                    FileUtils.moveFile(file, new File(dest));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }


    @Test
    public void repairAv() {
        File path = new File(destPath + "av/");
        Arrays.stream(path.listFiles()).forEach(x -> {
            String filename = x.getName();
            String removeSpecial = filename
                    .toLowerCase()
                    .replace("[98t.tv]", "")
                    .replace("hhd800.com@", "");
            String dest = destPath + "av/" + removeSpecial;
            try {
                if (filename != removeSpecial) {
                    FileUtils.moveFile(x, new File(dest));
                }
            } catch (IOException e) {
            }
        });
    }

    @Test
    public void repairHunter() {
        String dest = destPath + "nor/";
        File path = new File(dest);
        Arrays.stream(path.listFiles()).forEach(x -> {
            String filename = x.getName();
            if(filename.contains("gui")) {
                return;
            }
            System.out.println(filename);
            String removeSpecial = filename;
            //removeSpecial = "gui_" + removeSpecial+ new Random().nextInt(10);


            if(filename.contains("做爱")) {
                removeSpecial = "gui_beat_做爱_" + new Random().nextInt(100);
            }
            if(filename.contains("beat_逼")) {
                removeSpecial = "gui_beat_逼_" + new Random().nextInt(500);
            }
            if(filename.contains("beat_自慰")) {
                removeSpecial = "gui_beat_自慰_" + new Random().nextInt(500);
            }
            if(filename.contains("_eat")) {
                removeSpecial = "gui_eat_" + new Random().nextInt(500);
            }
            try {
                if (filename != removeSpecial ) {
                    System.out.println(removeSpecial);
                    FileUtils.moveFile(x, new File(dest + removeSpecial + ".mp4"));
                }
            } catch (IOException e) {
            }
        });
    }

    @Test
    public void repairNoAge() {
        File path = new File(destPath + "nonage/");
        Arrays.stream(path.listFiles()).forEach(x -> {
            String filename = x.getName();
            String removeSpecial = filename
                    .toLowerCase()
                    .replace("[98t.tv]", "")
                    .replace("hhd800.com@", "");
            String dest = destPath + "av/" + removeSpecial;
            try {
                if (filename != removeSpecial) {
                    FileUtils.moveFile(x, new File(dest));
                }
            } catch (IOException e) {
            }
        });
    }

    @Test
    public void special() {
        Map<String,String> map = new HashMap<>();

        map.put("幼", "nonage");
        map.put("厕", "toilet");
        map.put("toilet", "toilet");
        map.put("电话", "phone");
        map.put("phone", "phone");
        map.put("通话", "phone");
        map.put("抄底", "hunter");
        map.put("漫展", "hunter");
        map.put("空姐", "air");
        map.put("腿", "leg");
        map.put("other", "other");
        map.put("极品", "top");
        map.put("top", "top");
        map.put("无码", "av");
        map.put("whr", "whr");
        map.put("门", "door");

        map.put("live", "live");
        map.put("eat", "eat");
        map.put("hunter", "hunter");
        map.put("true", "true");
        //map.put("做爱", "ml");
        map.put("自慰", "sy");
        map.put("beat", "beat");
        File path = new File(destPath + "nonage/");
        Arrays.stream(path.listFiles()).forEach(x -> {
            String filename = x.getName();
            for (String key : map.keySet()) {
                if(filename.contains(key) && (!filename.contains("top") || key == "top")) {
                    String dest = destPath + map.get(key);
                    try {
                        FileUtils.forceMkdir(new File(dest));
                        FileUtils.moveFile(x, new File(dest + "/"+ filename));
                    } catch (IOException e) {

                    }
                }
            }
        });
//        for (String key : map.keySet()) {
//            path = new File(destPath + map.get(key));
//            Arrays.stream(path.listFiles()).forEach(x -> {
//                String filename = x.getName().replace(key, map.get(key));
//                try {
//                    FileUtils.moveFile(x, new File(destPath + map.get(key) + "/"+ filename));
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            });
//
//        }

    }
    @Test
    public void repairNor() {
        File path = new File(destPath + "nor/");
        Arrays.stream(path.listFiles()).forEach(x -> {
            String dest;
            String filename = x.getName().toLowerCase();
            String removeSpecial = x.getName();
            if(filename.contains("uur76") ||
            filename.contains("uue29") ||
            filename.contains("澳门")) {
                x.delete();
            }
            if (x.length() / 1000 / 1000 / 1000 >= 5) {
                int s = filename.lastIndexOf("_");
                dest = destPath + "av/" + filename.substring(s + 1);
            } else {
                 removeSpecial = filename
                        .replace("《", "")
                        .replace("》", "")
                        .replace("【国产】", "")
                        .replace(" ", "")
                        .replace("", "")
                        .replace("[", "")
                        .replace("]", "")
                        .replace("22_077_5v", "hunter_车站")
                         .replace("干了我不敢干的事，色胆包天重磅推荐团队协作地铁公交围猎少女少妇_v_-", "hunter_围猎_")
                         .replace("无内抄底精选国内外无内抄底个个都是不穿内内就出门的主毛毛照的一清二楚22v_-", "hunter_无内_")
                         .replace("盛筵", "")
                        .replace("【绝版收藏】", "")
                        .replace("【", "")
                        .replace("】", "")
                        .replace("gc2048.com", "")
                        .replace("guochan2048.com", "")
                         .replace("_中学生_","_toilet_学校_")
                         .replace("1012-1-33_33v","eat" )
                         .replace("_156_","other_cao_")
                         .replace("_309_","_air_")
                         .replace("109_3185", "hunter_地铁_")
                        .replace("98t.tv", "")
                         .replace("(", "")
                         .replace(")", "")
                         .replace("（", "")
                         .replace("）", "");



                dest = destPath + "nor/" + removeSpecial;
            }
            try {
                if (filename != removeSpecial) {
                    System.out.println(dest);
                    FileUtils.moveFile(x, new File(dest));
                }
            } catch (IOException e) {

            }
        });
    }


    public static void main(String[] args) {
        Main main = new Main();
        //main.del_other(new File(path));
        main.mv_av(new File(path));
        main.check_av(new File(destPath + "/av"));

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd");
        String date = dtf.format(LocalDate.now());
        Arrays.stream(new File(path).listFiles()).forEach(x -> mv_nor(x, date + "_" + x.getName()));
    }
}