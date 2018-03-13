package com.wzw.sharding.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author:WangZhiwen
 * @Description:
 * @Date:2018-1-19
 */
public class SimpleFile {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleFile.class);

    private File file = null;

    //文件输入流相关
    private FileInputStream fis = null;
    private InputStreamReader isr = null;
    private BufferedReader br = null;

    //文件输出流相关
    private FileOutputStream fos = null;
    private PrintStream ps = null;

    private ReentrantLock reentrantLock = new ReentrantLock();

    /**
     * 初始化指定文件
     * @param filePath
     */
    public void init(String filePath){
        file = new File(filePath);
    }

    /**
     * 向指定文件写入多行内容
     * @param lines
     */
    public void writeLines(List<String> lines){

        reentrantLock.lock();

        if(file==null){
            LOGGER.error("文件未初始化");
        }
        try {
            fos = new FileOutputStream(file);
            ps = new PrintStream(fos);
            for(String line:lines){
                ps.println(line);
            }
        } catch (FileNotFoundException e) {
            LOGGER.error("找不到{}文件",file.getAbsolutePath(),e);
        } finally {
            closePS(ps);
        }

        reentrantLock.unlock();

    }

    /**
     * 向文件追加行内容
     * @param lines
     */
    public synchronized void appendLine(List<String> lines){
        if(file==null){
            LOGGER.error("文件未初始化");
            return;
        }
        try {
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);

            List<String> oldLines = new LinkedList<String>();
            String line = null;
            while ((line=br.readLine())!=null) {
                oldLines.add(line);
            }
            oldLines.addAll(lines);

            writeLines(oldLines);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeBR(br);
        }
    }

    /**
     * 读取[from,to)行内容
     * @param from
     * @param to
     */
    public List<String> readLines(int from,int to){

        List<String> lines = new ArrayList<String>();

        try {
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);

            String line = null;
            int lineNum=0;
            while((line=br.readLine())!=null){
                lineNum++;//当前是第多少行
                if(lineNum>=from){
                    if(lineNum==to){
                        break;
                    }
                    lines.add(line);
                }else{
                    continue;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeBR(br);
        }

        return lines;

    }

    /**
     * 读取[from,)行内容
     * @param from
     */
    public List<String> readLines(int from){

        List<String> lines = new ArrayList<String>();

        try {
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);

            String line = null;
            int lineNum=0;
            while((line=br.readLine())!=null){
                lineNum++;//当前是第多少行
                if(lineNum>=from){
                    lines.add(line);
                }else{
                    continue;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeBR(br);
        }

        return lines;

    }

    /**
     * 读取指定行的内容
     * @param lineNum
     * @return
     */
    public String readLine(int lineNum){
        String line = null;
        try {
            int i=0;
            while((line=br.readLine())!=null){
                i++;//当前是第多少行
                if(lineNum!=i){
                    continue;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeBR(br);
        }
        return line;
    }

    /**
     * 获取文件行数
     * @return
     */
    public Integer getLineCount(){
        Integer lineCount=0;
        try {
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);

            String line = null;

            while((line=br.readLine())!=null){
                lineCount++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeBR(br);
        }
        return lineCount;
    }

    private void closeBR(BufferedReader br){
        if(br!=null){
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void closePS(PrintStream ps){
        if(ps!=null){
            ps.close();
        }
    }

    public SimpleFile(File file) {
        this.file = file;
    }

    public SimpleFile(String filePath) {
        init(filePath);
    }

    public SimpleFile() {
    }

    public File getFile() {
        return file;
    }


}
