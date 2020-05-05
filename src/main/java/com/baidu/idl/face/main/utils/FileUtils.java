package com.baidu.idl.face.main.utils;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * author : shangrong
 * date : 2019/5/23 2:05 PM
 * description :文件工具类
 */
public class FileUtils {
    /**
     * 读取txt文件的内容
     *
     * @param filePath 想要读取的文件对象
     * @return 返回文件内容
     */
    public static String txt2String(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s = null;
            while ((s = br.readLine()) != null) {//使用readLine方法，一次读一行
                result.append(System.lineSeparator() + s);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }


    /**
     * 写入TXT文件
     */
    public static boolean writeTxtFile(String content, String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            //return false;
            file.createNewFile();
        }

        boolean flag = false;
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);      //覆盖 追加为new FileOutputStream(file, true)
            fileOutputStream.write(content.getBytes("utf-8"));
            fileOutputStream.close();
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 写入文件
     */
    public static boolean write2SDFromInput(InputStream input, String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) { file.createNewFile(); }
        Log.e("write2SDFromInput","Begin , path = "+filePath );
        FileOutputStream  output = null;
        BufferedInputStream  bis =null;
        BufferedOutputStream bos=null;
        boolean flag = false;
        try {
            output = new FileOutputStream(file);
            bis = new BufferedInputStream(input);
            bos= new BufferedOutputStream(output);
            /*
            byte [] buffer = new byte[4 * 1024];
            while(input.read(buffer) != -1){
                output.write(buffer);
                output.flush();
            }
            */
            int b = 0;
            byte[] byArr = new byte[1024];
            while((b= bis.read(byArr))!=-1){
                bos.write(byArr, 0, b);
            }
            flag=true;
            Log.e("write2SDFromInput","End." );
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            /*
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            */
            try {
                if(bis !=null){
                    bis.close();
                }
                if(bos !=null){
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    /**
     * Checks if is sd card available.检查SD卡是否可用
     */
    public static boolean isSdCardAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * Gets the SD root file.获取SD卡根目录
     */
    public static File getSDRootFile() {
        if (isSdCardAvailable()) {
            return Environment.getExternalStorageDirectory();
        } else {
            return null;
        }
    }

    /**
     * 获取导入图片文件的目录信息
     */
    public static File getBatchImportDirectory() {
        File sdRootFile = getSDRootFile();
        File file = null;
        if (sdRootFile != null && sdRootFile.exists()) {
            file = new File(sdRootFile, "Face-Import");
            if (!file.exists()) {
                file.mkdirs();
            }
        }
        return file;
    }

    /**
     * 获取导入图片成功的目录信息
     */
    public static File getBatchImportSuccessDirectory() {
        File sdRootFile = getSDRootFile();
        File file = null;
        if (sdRootFile != null && sdRootFile.exists()) {
            file = new File(sdRootFile, "Success-Import");
            if (!file.exists()) {
                file.mkdirs();
            }
        }
        return file;
    }
    public static File getCacheDirectory() {
        File sdRootFile = getBatchImportSuccessDirectory();
        File file = null;
        if (sdRootFile != null && sdRootFile.exists()) {
            file = new File(sdRootFile, "cache");
            if (!file.exists()) {
                file.mkdirs();
            }
        }
        return file;
    }

    /**
     * 判断文件是否存在
     */
    public static File isFileExist(String fileDirectory, String fileName) {
        File file = new File(fileDirectory + "/" + fileName);
        try {
            if (!file.exists()) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
        return file;
    }

    /**
     * 删除文件
     */
    public static void deleteFile(String filePath) {
        try {
            // 找到文件所在的路径并删除该文件
            File file = new File(filePath);
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * 获取不带扩展名的文件名
     * */
    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    /**
     * 保存图片
     */
    public static boolean saveBitmap(File file, Bitmap bitmap) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean copyFile(String oldPath, String newPath) {
        InputStream inStream = null;
        FileOutputStream fs = null;
        boolean result = false;
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            // 判断目录是否存在
            File newfile = new File(newPath);
            File newFileDir = new File(newfile.getPath().replace(newfile.getName(), ""));
            if (!newFileDir.exists()) {
                newFileDir.mkdirs();
            }
            if (oldfile.exists()) { // 文件存在时
                inStream = new FileInputStream(oldPath); // 读入原文件
                fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; // 字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                result = true;
            } else {
                result = false;
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fs != null) {
                try {
                    fs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    // 压缩路径 解压路径 解压路径父（处理多文件）
    public static void unzipFile(File zipFile, String folderPath){
        File desDir = new File(folderPath);
        if (!desDir.exists()) {
            desDir.mkdirs();
        }
        try {
            ZipFile zf = new ZipFile(zipFile);
            for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements(); ) {
                ZipEntry entry = ((ZipEntry) entries.nextElement());

                InputStream in = zf.getInputStream(entry);
                String str = folderPath + File.separator + entry.getName();
                //str = new String(str.getBytes("8859_1"), "GB2312");   //转码
                File desFile = new File(str);
                if (!desFile.exists()) {
                    File fileParentDir = desFile.getParentFile();
                    if (!fileParentDir.exists()) {
                        fileParentDir.mkdirs();
                    }
                    desFile.createNewFile();
                }
                OutputStream out = new FileOutputStream(desFile);
                byte buffer[] = new byte[4096];                //BUFF_SIZE
                int realLength;
                while ((realLength = in.read(buffer)) > 0) {
                    out.write(buffer, 0, realLength);
                }
                in.close();
                out.close();
            }
        }
        catch (Exception ex){LogUtils.e("UNZIP","Unzip ERROR.");}
    }

    // 源文件夹 输出路径 包内路径 是否close
    public static void zipFile(File sourceFile, ZipOutputStream out, String base, boolean _root) throws Exception {
        //如果路径为目录（文件夹）
        if(sourceFile.isDirectory()) {
            //取出文件夹中的文件（或子文件夹）
            File[] flist = sourceFile.listFiles();

            if(flist.length==0) {//如果文件夹为空，则只需在目的地zip文件中写入一个目录进入点
                System.out.println(base + File.separator);
                out.putNextEntry(new ZipEntry(base + File.separator));
            } else {//如果文件夹不为空，则递归调用compress,文件夹中的每一个文件（或文件夹）进行压缩
                for(int i=0; i<flist.length; i++) {
                    zipFile(flist[i], out,  base+File.separator+flist[i].getName(), false);
                }
                if (_root) out.close();
            }
        } else {
            if (_root)
                out.putNextEntry(new ZipEntry(base+File.separator+sourceFile.getName()));
            else out.putNextEntry(new ZipEntry(base));
            FileInputStream fos = new FileInputStream(sourceFile);
            BufferedInputStream bis = new BufferedInputStream(fos);
            int len;

            byte[] buf = new byte[4096];
            System.out.println(base);
            while((len=bis.read(buf, 0, 4096)) != -1) {
                out.write(buf, 0, len);
            }
            bis.close();
            fos.close();
            out.closeEntry();
            if(_root)  out.close();
        }
    }
}
