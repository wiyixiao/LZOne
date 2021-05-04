package com.wiyixiao.lzone.utils;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * FileUtil
 *
 * @author baishixian
 * @date 2018/3/29 12:20
 */

public class FileUtil {
    /**
     * check dir
     *
     * @param dir
     * @return
     */
    public static boolean checkDir(String dir) {
        File destDir = new File(dir);
        if (!destDir.exists()) {
            return destDir.mkdirs();
        }

        return true;
    }

    public static boolean checkFile(String filePth) {
        File file = new File(filePth);
        // 文件不存在
        //throw new RuntimeException("file not exist!");
        return file.exists();
    }

    /**
     * 重命名文件
     *
     * @param oldPath 原来的文件地址
     * @param newPath 新的文件地址
     */
    public static void renameFile(String oldPath, String newPath, boolean copy) {
        File oleFile = new File(oldPath);
        File newFile = new File(newPath);

        if (copy) {
            //执行拷贝
            copyfile(oleFile, newFile, true);
        } else {
            //执行重命名
            boolean b = oleFile.renameTo(newFile);
        }

    }

    /**
     * get files
     *
     * @param path
     * @return
     */
    public static ArrayList<String> getFilesAllName(String path) {
        ArrayList<String> s = new ArrayList<>();

        for (File file : getFileSort(path)
        ) {
            s.add(file.getAbsolutePath());
        }

        return s;
    }

    /**
     * @param path
     * @return
     * @按修改时间返回文件列表
     */
    public static List<File> getFileSort(String path) {

        List<File> list = getFiles(path, new ArrayList<File>());

        if (list != null && list.size() > 0) {

            Collections.sort(list, new Comparator<File>() {
                @Override
                public int compare(File file, File newFile) {
                    if (file.lastModified() < newFile.lastModified()) {
                        return 1;
                    } else if (file.lastModified() == newFile.lastModified()) {
                        return 0;
                    } else {
                        return -1;
                    }
                }
            });

        }

        return list;
    }

    /**
     * @param realpath
     * @param files
     * @return
     * @获取目录下所有文件
     */
    public static List<File> getFiles(String realpath, List<File> files) {
        File realFile = new File(realpath);
        if (realFile.isDirectory()) {
            File[] subfiles = realFile.listFiles();
            if (subfiles == null) {
                Log.e("error", "空目录");
                return null;
            }
            for (File file : subfiles) {
                if (file.isDirectory()) {
                    getFiles(file.getAbsolutePath(), files);
                } else {
                    files.add(file);
                }
            }
        }
        return files;
    }


    /** 删除文件，可以是文件或文件夹
     * @param delFile 要删除的文件夹或文件名
     * @return 删除成功返回true，否则返回false
     */
    public static boolean delete(String delFile) {
        File file = new File(delFile);
        if (!file.exists()) {
//            Toast.makeText(HnUiUtils.getContext(), "删除文件失败:" + delFile + "不存在！", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (file.isFile()) {
                return deleteSingleFile(delFile);
            } else {
                return deleteDirectory(delFile);
            }
        }
    }


    /** 删除目录及目录下的文件
     * @param filePath 要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String filePath) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        if(files == null) {
            return false;
        }
        for (File file : files) {
            // 删除子文件
            if (file.isFile()) {
                flag = deleteSingleFile(file.getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
            // 删除子目录
            else if (file.isDirectory()) {
                flag = deleteDirectory(file
                        .getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
        }
        if (!flag) {
            return false;
        }
        // 删除当前目录
        return dirFile.delete();
    }


    /** 删除单个文件
     * @param filepath 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteSingleFile(String filepath) {

        if(!TextUtils.isEmpty(filepath)){
            File file = new File(filepath);
            // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
            if (file.exists() && file.isFile()) {
                return file.delete();
            } else {
                return false;
            }
        }

        return false;
    }

    /**
     * image file check
     *
     * @param filePath
     * @return
     */
    public static boolean isImageFile(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        return options.outWidth != -1;
    }

    public static void writeData(String str, String fileName) {
        String filePath = "/sdcard/";
        writeTxtToFile(str, filePath, fileName);
    }

    //region Private Methods
    // 将字符串写入到文本文件中
    private static void writeTxtToFile(String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(filePath, fileName);

        String strFilePath = filePath + fileName;
        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                Log.d("TestFile", "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
        }
    }

    //生成文件
    private static File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    //生成文件夹
    private static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e + "");
        }
    }
    //endregion

    public static void copyfile(File fromFile, File toFile, Boolean rewrite) {

        if (!fromFile.exists() || !fromFile.isFile()) {
            return;
        }

        if (!fromFile.canRead()) {
            return;
        }

        if (toFile.exists() && rewrite) {
            boolean b = toFile.delete();
        }

        //当文件不存时，canWrite一直返回的都是false

//        if (!toFile.canWrite()) {
//            MessageDialog.openError(new Shell(), "错误信息", "不能够写将要复制的目标文件" + toFile.getPath());
//            Toast.makeText(this, "不能够写将要复制的目标文件", Toast.LENGTH_SHORT);
//            return;
//        }

        try {
            java.io.FileInputStream fosfrom = new java.io.FileInputStream(fromFile);
            FileOutputStream fosto = new FileOutputStream(toFile);

            byte[] bt = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c); //将内容写到新文件当中
            }

            fosfrom.close();
            fosto.close();

        } catch (Exception ex) {
            Log.e("readfile", ex.toString());
        }
    }

    /**
     * @获取SD卡根目录
     * @param mContext
     * @param isRemovale
     * @return
     */
    public static String getStoragePath(Context mContext, boolean isRemovale) {
        // is_removale  false代表获取内置存储  true 代表外置sd卡根路径
        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            assert mStorageManager != null;
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                if (isRemovale == removable) {
                    return path;
                }
            }
        } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


}
