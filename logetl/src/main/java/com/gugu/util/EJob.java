package com.gugu.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

/**
 * @author gugu
 * @Classname EJob
 * @Description 创建临时文件*.jar
 * @Date 2020/1/2 18:23
 */
public class EJob {
    public static File createTempJar(String root) throws IOException {
        if (!new File(root).exists()) {
            return null;
        }
        final File jarFile = File.createTempFile("EJob-", ".jar", new File(System.getProperty("java.io.tmpdir")));

        // 系统关闭时删除临时文件
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                jarFile.delete();
            }
        });
        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(jarFile));
        createTempJarInner(jarOutputStream, new File(root),"" );
        jarOutputStream.flush();
        jarOutputStream.close();
        return jarFile;

    }
    private static void createTempJarInner(JarOutputStream jarOutputStream,File file,String base) throws IOException {
        if (file.isDirectory()){
            File[] files = file.listFiles();
            if ( base.length() > 0){
                base = base + "/";
            }
            for (int i = 0; i < files.length; i++) {
                createTempJarInner(jarOutputStream, files[i],base+files[i].getName());
            }
        }else {
            jarOutputStream.putNextEntry(new JarEntry(base));
            FileInputStream in = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int n = in.read(buffer);
            while(-1 != n){
                jarOutputStream.write(buffer, 0 , n);
                n = in.read(buffer);
            }
            in.close();
        }
    }
}
