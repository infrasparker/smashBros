package src.smash;

import src.image.ImageHandler;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class ImportHandler {
    private static GraphicsConfiguration gc =
            GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    
    public final static Object navigateData(String[] keys, Map data) {
        if (data != null) {
            if (data.containsKey(keys[0])) {
                Object value = data.get(keys[0]);
                if (keys.length > 1) {
                    String[] otherKeys = new String[keys.length - 1];
                    for (int i = 0; i < otherKeys.length; i++) {
                        otherKeys[i] = keys[i+1];
                    }
                    return navigateData(otherKeys, (Map)value);
                }
                else {
                    return value;
                }
            }
        }
        return null;
    }
    
    //<editor-fold defaultstate="collapsed" desc="generateImageData">

    //<editor-fold defaultstate="collapsed" desc="readImageData">
    public static Map<String, Object> readImageData(String p) {
        return readImageData(new File(p), 1);
    }

    public static Map<String, Object> readImageData(String p, double scale) {
        return readImageData(new File(p), scale);
    }

    public static Map<String, Object> readImageData(File f, double s) {
        Map<String, Object> map = new HashMap();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(f.getPath()));
            String line = reader.readLine();
            System.out.println(f.getPath());
            while (line != null) {
                String key = line.substring(0, line.indexOf(":")).replaceAll(" ", "");
                String value;
                if (!key.equals("new")) {
                    value = line.substring(line.indexOf(":") + 2, line.length());
                } else {
                    value = null;
                }
                switch (key) {
                    case "width":
                        map.put(key, Integer.parseInt(value));
                        break;
                    case "SCDs":
                        int[][] SCDs = new int[4][2];
                        value = value.substring(2);
                        for (int[] SCD : SCDs) {
                            SCD[0] = (int) (Integer.parseInt(value.substring(
                                    0, value.indexOf(","))) * s);
                            SCD[1] = (int) (Integer.parseInt(value.substring(
                                    value.indexOf(",") + 1, value.indexOf("}")))
                                    * s);
                            value = value.substring(value.indexOf("{") + 1);
                        }
                        map.put(key, SCDs);
                        break;
                    case "frames":
                        int first = Integer.parseInt(value.substring(
                                1, value.indexOf(",")));
                        int last = Integer.parseInt(value.substring(
                                value.indexOf(",") + 1, value.indexOf(")")));
                        map.put(key, new int[]{first, last});
                        break;
                    case "next":
                        int start = Integer.parseInt(value.substring(
                                1, value.indexOf(",")));
                        int end = Integer.parseInt(value.substring(
                                value.indexOf(",") + 1, value.indexOf(")")));
                        map.put(key, new int[]{start, end});
                        break;
                    case "hitbubbles":
                        value = value.substring(1, value.length() - 1);
                        ArrayList<Hitbubble> hBs = new ArrayList();
                        while (value.length() > 1) {
                            value = value.substring(value.indexOf("{") + 1);
                            int x = (int) (Integer.parseInt(value.substring(
                                    0, value.indexOf(","))) * s);
                            value = value.substring(value.indexOf(",") + 1);
                            int y = (int) (Integer.parseInt(value.substring(
                                    0, value.indexOf(","))) * s);
                            value = value.substring(value.indexOf(",") + 1);
                            int r = (int) (Integer.parseInt(value.substring(
                                    0, value.indexOf("}"))) * s / 2);
                            value = value.substring(value.indexOf("(") + 1);
                            int d = Integer.parseInt(value.substring(
                                    0, value.indexOf(",")));
                            value = value.substring(value.indexOf(",") + 1);
                            int bKB = Integer.parseInt(value.substring(
                                    0, value.indexOf(",")));
                            value = value.substring(value.indexOf(",") + 1);
                            double kBS = Double.parseDouble(value.substring(
                                    0, value.indexOf(",")));
                            value = value.substring(value.indexOf(",") + 1);
                            int a = Integer.parseInt(value.substring(
                                    0, value.indexOf(",")));
                            value = value.substring(value.indexOf(",") + 1);
                            int p = Integer.parseInt(value.substring(
                                    0, value.indexOf(",")));
                            value = value.substring(value.indexOf(",") + 1);
                            String sfx = value.substring(0, value.indexOf(")"));
                            value = value.substring(value.indexOf(")"));
                            hBs.add(new Hitbubble(x, y, r, d, bKB, kBS, a, p, sfx));
                        }
                        map.put(key, hBs);
                        break;
                    case "new":
                        map.put(key, null);
                        break;
                    default :
                        System.out.println("Error occurred reading key - " + key + " - from file" + f.getPath());
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            System.out.println("Could not read file: " + f.getName());
        } catch (NumberFormatException e) {
            System.out.println("Could not translate String to int from "
                    + f.getPath());
        }
        return map;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="readImage">
    public static BufferedImage optimizeImage(BufferedImage img) {
        BufferedImage img2 = gc.createCompatibleImage(img.getWidth(), img.getHeight(), Transparency.TRANSLUCENT);
        Graphics2D g = img2.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        return img2;
    }
    
    public static BufferedImage readImage(File f, double scale) {
        try {
            BufferedImage i = ImageIO.read(f);
            if (scale != 1) {
                i = ImageHandler.resize(i, scale);
            }
            return optimizeImage(i);
        } catch (Exception e) {
            System.out.println("Could not load file: " + f.getName());
            return null;
        }
    }

    public static BufferedImage readImage(String p, double scale) {
        return readImage(new File(p), scale);
    }

    public static BufferedImage readImage(String p) {
        return readImage(p, 1);
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Generate Image Data">
    public static HashMap generateImageData(String s) {
        return generateImageData(s, 1);
    }

    public static HashMap generateImageData(String s, double scale) {
        System.gc();
        File[] orig = new File(s).listFiles();
        ArrayList<File> sorted = new ArrayList();
        for (File f : orig) {
            if (!f.getName().equals("Thumbs.db")) {
                sorted.add(f);
            }
        }
        File[] files = new File[sorted.size()];
        for (int i = 0; i < files.length; i++) {
            files[i] = sorted.get(i);
        }
        if (files.length > 0) {
            if (files[0].isFile()) {
                HashMap<String, Object> x = new HashMap();
                for (File f : files) {
                    String n = f.getName();
                    String ex = n.substring(n.indexOf(".") + 1, n.length());
                    if (ex.equals("png")) {
                        x.put(n, readImage(f, scale));
                    } else if (ex.equals("txt")) {
                        x.put(n, readImageData(f, scale));
                    }
                }
                return x;
            } else {
                HashMap<String, Map> x = new HashMap();
                for (File f : files) {
                    x.put(f.getName(), generateImageData(s + "\\" + f.getName(),
                            scale));
                }
                return x;
            }
        } else {
            return null;
        }
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Generate Sorted Image Data">
    public static LinkedHashMap generateSortedImageData(String s) {
        return generateSortedImageData(s, 1);
    }

    public static LinkedHashMap generateSortedImageData(String s, double scale) {
        System.gc();
        File[] orig = new File(s).listFiles();
        ArrayList<File> sorted = new ArrayList();
        for (File f : orig) {
            if (!f.getName().equals("Thumbs.db")) {
                sorted.add(f);
            }
        }
        File[] files = new File[sorted.size()];
        for (int i = 0; i < files.length; i++) {
            files[i] = sorted.get(i);
        }
        if (files.length > 0) {
            if (files[0].isFile()) {
                LinkedHashMap<String, Object> x = new LinkedHashMap();
                for (File f : files) {
                    String n = f.getName();
                    String ex = n.substring(n.indexOf(".") + 1, n.length());
                    if (ex.equals("png")) {
                        x.put(n, readImage(f, scale));
                    } else if (ex.equals("txt")) {
                        x.put(n, readImageData(f, scale));
                    }
                }
                return x;
            } else {
                LinkedHashMap<String, Map> x = new LinkedHashMap();
                for (File f : files) {
                    x.put(f.getName(), generateSortedImageData(s + "\\"
                            + f.getName(), scale));
                }
                return x;
            }
        } else {
            return null;
        }
    }
//</editor-fold>

//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="generateAudioData">
    public static HashMap generateAudioData(String s) {
        System.gc();
        File[] orig = new File(s).listFiles();
        ArrayList<File> sorted = new ArrayList();
        for (File f : orig) {
            if (!f.getName().equals("Thumbs.db")) {
                sorted.add(f);
            }
        }
        File[] files = new File[sorted.size()];
        for (int i = 0; i < files.length; i++) {
            files[i] = sorted.get(i);
        }
        if (files.length > 0) {
            if (files[0].isFile()) {
                HashMap<String, File> x = new HashMap();
                for (File f : files) {
                    String n = f.getName();
                    String ex = n.substring(n.indexOf(".") + 1, n.length());
                    if (ex.equals("wav")) {
                        x.put(n, f);
                    }
                }
                return x;
            } else {
                HashMap<String, Map> x = new HashMap();
                for (File f : files) {
                    x.put(f.getName(), generateAudioData(s + "\\" + f.getName()));
                }
                return x;
            }
        } else {
            return null;
        }
    }
//</editor-fold>
}