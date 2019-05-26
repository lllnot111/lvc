package Utils;

import Base.Data;
import Constant.DirParam;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.util.ArrayList;

public class FileUtil {
    public Data getFileFromData(String fileMd5){
        Data data = new Data();
        File f = new  File(System.getProperty(DirParam.ROOT_DIR)+File.separator+DirParam.REPO+File.separator+DirParam.DATA+File.separator+
                fileMd5.substring(0,2)+File.separator+fileMd5.substring(2));
        data.name = fileMd5;
        data.file = f;
        data.content = readFile(f);
        return data;
    }

    public Data getFileByPath(String path){
        Data data = new Data();
        File f = new  File(path);
        data.name = f.getName();
        data.file = f;
        data.content = readFile(f);
        return data;
    }

    public void writeFile(File f,String content){
        try {
            if(f.getParentFile()!=null){
                if(!f.getParentFile().exists()){
                    f.getParentFile().mkdirs();
                }
            }
            if(!f.exists()){
                f.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(content.getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createData(String md5,String content){
        String REPO = System.getProperty(DirParam.ROOT_DIR)+File.separator+DirParam.REPO;
        try {
            File f = new File(REPO+File.separator+DirParam.DATA+File.separator+md5.substring(0,2)+File.separator +md5.substring(2));
            if(f.getParentFile()!=null){
                if(!f.getParentFile().exists()){
                    f.getParentFile().mkdirs();
                }
            }
            if(!f.exists()){
                f.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(content.getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createFile(String path,String content){
        try {
            File f = new File(path);
            if(f.getParentFile()!=null){
                if(!f.getParentFile().exists()){
                    f.getParentFile().mkdirs();
                }
            }
            if(!f.exists()){
                f.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(content.getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void appendFile(File f,String content){
        try {
            if(!f.exists()){
                f.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(f,true);
            fos.write(content.getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cleanFile(File f){
        try {
            FileWriter fileWriter =new FileWriter(f);
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> readFile(File f){
        ArrayList<String> content = new ArrayList<String>();
        if(f.exists()){
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
                String line = "";
                while((line = br.readLine())!=null){
                    content.add(line);
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return content;
    }

    public StringBuffer FileString(File f){
        ArrayList<String> content = new ArrayList<String>();
        StringBuffer stringBuffer = new StringBuffer();
        if(f.exists()){
            try {
                FileInputStream fis = new FileInputStream(f);
                byte[] buffer = new byte[1024];
                for(int read = fis.read(buffer, 0, 1024); read > -1; read = fis.read(buffer, 0, 1024)) {
                    String s = new String(buffer,0,read);
                    stringBuffer.append(s);
                    //System.out.println(DigestUtils.md5Hex(new String(stringBuffer)));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stringBuffer;
    }
}
