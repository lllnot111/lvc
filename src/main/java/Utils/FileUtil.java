package Utils;

import Base.Data;
import Constant.DirParam;

import java.io.*;

public class FileUtil {
    public Data getFileFromData(String fileMd5){
        Data data = new Data();
        File f = new  File(DirParam.REPO+File.separator+DirParam.DATA+File.separator+
                fileMd5.substring(2)+File.separator+fileMd5.substring(0,2));
        data.name = fileMd5;
        data.file = f;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            String line = "";
            while((line = br.readLine())!=null){
                data.content.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public Data getFileByPath(String path){
        Data data = new Data();
        File f = new  File(path);
        data.name = f.getName();
        data.file = f;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            String line = "";
            while((line = br.readLine())!=null){
                data.content.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
    public void writeFile(File f,String content){
        try {
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
}
