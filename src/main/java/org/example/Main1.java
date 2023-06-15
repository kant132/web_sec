package org.example;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main1 {
    static String destPath = "/Volumes/gen/video/";

    public static void check_av(File file) {
        if (file.isDirectory()) {
            Arrays.stream(file.listFiles()).forEach(x -> check_av(x));
        }
        String filename = file.getName().toLowerCase();
        String name = filename.replace("hhd800.com@", "")
                .replace("_x1080x", "");

        int i = name.lastIndexOf("_dasd");
        if (i < 0) {
            i = name.lastIndexOf("_ofje");
        }
        if (i < 0) {
            i = name.lastIndexOf("_atid");
        }
        if (i < 0) {
            i = name.lastIndexOf("_juq");
        }

        if (i > 0) {
            name = name.substring(i + 1);
            System.out.println(name);

        }
        if (!name.equals(filename)) {
            try {
                FileUtils.moveFile(file, new File(destPath + "av/" + name));
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    static void mv_av(File file) {
        if (file.isDirectory()) {
            Arrays.stream(file.listFiles()).forEach(x -> mv_av(x));
            return;
        }
        Pattern name = Pattern.compile("fsdss|mimk|achj|sdmm|gmem|dvaj|sdde|snis|ure|pppe|ymds|same|ofje|dasd|royd|mkmp|juq|atid|stars|tysf|legsjapan|ipz|hmn|dldss|ssni|ssis|ipx|jufe|cawd|abw|adn|tek|mide|lafbd|marks|pred|jul|fsdss|miaa|abp|nhdta|waaa|midv|fc2ppv|fcdss|cwp|meyd",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        Pattern suffix = Pattern.compile("avi|wmv|mpeg|mp4|m4v|mov|asf|flv|f4v|rmvb|rm|3gp|vob",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        String filename = file.getName().toLowerCase()
                .replace("hhd800.com@", "");
        if (!suffix.matcher(FilenameUtils.getExtension(filename)).find() || !name.matcher(filename).find()) {
            return;
        }
        try {
            System.out.println(filename);
            FileUtils.moveFile(file, new File(destPath + "av/" + filename), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    static int jpgCnt = 0;
    static void mv_jpg(File file, String date) {
        jpgCnt++;
        if (file.isDirectory()) {
            Arrays.stream(file.listFiles()).forEach(x -> mv_jpg(x, date));
            return;
        }

        Pattern suffix = Pattern.compile("jpg|png|img|gif",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        String filename = file.getName().toLowerCase();
        if (!suffix.matcher(FilenameUtils.getExtension(filename)).find()) {
            return;
        }
        try {
            System.out.println(filename);
            String dest = date + "_" + filename + "_" +  jpgCnt + "." + FilenameUtils.getExtension(filename);
            FileUtils.moveFile(file, new File(destPath + "jpg/" + dest), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.out.println(e);
        }
    }


    static void mv_nor(File file, String date)  {
        if (file.isDirectory()) {
            Arrays.stream(file.listFiles()).forEach(x -> mv_nor(x, date));
            return;
        }
        Pattern p = Pattern.compile("avi|wmv|mpeg|mp4|m4v|mov|asf|flv|f4v|rmvb|rm|3gp|vob",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

        String filename = file.getName();
        String suffix = FilenameUtils.getExtension(filename);
        if (!p.matcher(suffix).find()) {
            return;
        }

        String dest =  destPath + "nor/" + date + "_";
        try {
            if (!isContainChinese(filename) && isContainChinese(file.getParentFile().getName())) {
                dest = dest + file.getParentFile().getName() + "__";
            }
            dest = dest + filename;
            System.out.println(filename + "------" + dest + "------");
            FileUtils.moveFile(file, new File(dest));
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static boolean isContainChinese(String str) {

        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    public static void classification() throws IOException {
        File file = new File(destPath + "/nor");
        // 删除不要的
        String[] trash = {"同城", "uu", "社區", "社区最新情报","妹妹直播", "台湾妹妹", "更新同步", "有趣的小视频"};
        for (String s : trash) {
            Arrays.stream(file.listFiles(x -> x.getName().toLowerCase().replace(" ", "").contains(s))).forEach(x -> x.delete());
        }
        //归档
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd");
        String date = dtf.format(LocalDate.now());
        Map<String, Integer> map = new HashMap<>();
        String[] keys = {"jk", "ml", "家庭", "bt", "top", "eat", "beat", "preg", "迷奸", "露出", "黑丝", "美女", "whr", "抄底", "洗", "厕", "露脸", "摄影", "true", "情侣", "反差", "淫荡"};
        Map<String, String> clean = new HashMap<>();
        Arrays.stream(keys).forEach(x -> clean.put(x, x));
        clean.put("没穿内裤", "抄底_无");
        clean.put("泄密", "ml");
        for (String key : clean.keySet()) {
            for (File listFile : file.listFiles()) {
                if (listFile.getName().toLowerCase().contains(key)) {
                    String fileName = date + "_" + clean.get(key) + "_" + map.getOrDefault(key, 0) + ".mp4";
                    System.out.println(fileName);
                    map.put(key, map.getOrDefault(key, 0) + 1);
                    listFile.renameTo(new File(destPath + "nor/" + fileName));
                }
            }
        }
        //move to dest
        Map<String, List<String>> toDir = new HashMap<>();
        toDir.put("top", new ArrayList<>());
        toDir.get("top").add("top");
        toDir.get("top").add("jk");
        toDir.get("top").add("beat");


        //ml
        toDir.put("ml", new ArrayList<>());
        toDir.get("ml").add("ml");
        toDir.get("ml").add("黑丝");
        toDir.get("ml").add("美女");
        toDir.get("ml").add("反差");
        toDir.get("ml").add("露脸");
        toDir.get("ml").add("情侣");
        toDir.get("ml").add("淫荡");

        //bt
        toDir.put("bt", new ArrayList<>());
        toDir.get("bt").add("bt");
        toDir.get("bt").add("迷奸");
        toDir.get("bt").add("preg");
        toDir.get("bt").add("家庭");
        toDir.get("bt").add("true");

        toDir.put("whr", new ArrayList<>());
        toDir.get("whr").add("whr");

        toDir.put("hunter", new ArrayList<>());
        toDir.get("hunter").add("hunter");
        toDir.get("hunter").add("洗");
        toDir.get("hunter").add("洗");
        toDir.get("hunter").add("抄底");

        toDir.put("toilet", new ArrayList<>());
        toDir.get("toilet").add("厕");

        toDir.put("摄影", new ArrayList<>());
        toDir.get("摄影").add("摄影");

        toDir.put("eat", new ArrayList<>());
        toDir.get("eat").add("eat");

        for (String s : toDir.keySet()) {
            String path = destPath + s + "/";
            //FileUtils.forceMkdir(new File(path));
            for (File listFile : file.listFiles()) {
                toDir.get(s).forEach(x -> {
                    if (listFile.getName().contains(x)) {
                        System.out.println(path + listFile.getName());
                        listFile.renameTo(new File(path + listFile.getName()));
                    }
                });
            }
        }

    }


    public static void main(String[] args) throws IOException {
        String path = "/Volumes/gen/tmp_video/";

        mv_av(new File(path));
        check_av(new File(destPath + "/nor"));

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd");
        String date = dtf.format(LocalDate.now());

        Arrays.stream(new File(path).listFiles()).forEach(x -> mv_nor(x, date));
        mv_jpg(new File(path), date);
        //classification();

    }
}