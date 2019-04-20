package option;

import Base.Data;
import Constant.DirParam;
import Constant.FixType;
import Utils.FileUtil;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;

public class Add extends AbstractOption{

    FileUtil fileUtil = new FileUtil();
    String s = System.getProperty(DirParam.ROOT_DIR);
    String REPOPARENT = DirParam.ROOT_DIR;//+File.separator+DirParam.REPO;
    String REPO = DirParam.ROOT_DIR+File.separator+DirParam.REPO;

    @Override
    public void option(String[] args) {
        add();
    }
    /**
     * desc:将最近修改的文件加入版本库，但不生成新的版本
     * 1.将修改的文件添加进版本库
     * 2.在INDEX中记录当前的目录结构
     * 3.记录本次添加被修改的文件
     */
    public void add(){
        File repo = new File(REPOPARENT);
        //先获取旧的index信息
        HashMap<String,String> oldIndex = getIndexMap();
        //清楚index中的内容，以便下一步写入新内容
        fileUtil.cleanFile(new File(REPO+File.separator+DirParam.INDEX));
        //生成新的index，并将最近修改过的文件添加进版本库
        travelFile(repo,repo.getPath().length());
        //获取新的index信息
        HashMap<String,String> newIndex = getIndexMap();
        //对比新旧index信息，生成status（本次提交被修改的内容会存在status中）
        setStatus(oldIndex,newIndex);

    }
    /**
     * desc:遍历当前目录，生成最新的index，并将最近修改过的文件添加进版本库
     * 1.遍历目录
     * 2.将更新过的文件添加进版本库
     * 3.将当前的目录结构以及目录中文件所对应的版本库中的文件写入index
     */
    private void travelFile(File f,int parentDirLength) {
        if (f.isDirectory()) {
            File[] fList = f.listFiles();
            for (File file : fList) {
                if(!file.getName().equals(".lvc"))
                    travelFile(file,parentDirLength);
            }
        }
        if (f.isFile()) {
            copyFile(f,parentDirLength);
        }
    }
    /**
     * desc:将文件复制到版本库
     */
    private void copyFile(File f,int parentDirLength) {
        try {
            FileInputStream fis = new FileInputStream(f.getPath());
            String s = DigestUtils.md5Hex(fis);
            writeIndex(f,s,parentDirLength);
            File newDir = new File(REPO+File.separator+DirParam.DATA+File.separator + s.substring(0, 2));
            if (!newDir.exists()) {
                newDir.mkdirs();
            }
            File newFile = new File(REPO+File.separator+DirParam.DATA+File.separator + s.substring(0, 2)+File.separator+s.substring(2));
            if (!newFile.exists()) {
                Files.copy(f.toPath(), newFile.toPath());
            }
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * desc:将目录信息以及文件存储位置写入index，写一条
     */
    private void writeIndex(File f,String s,int parentDirLength){
        try {
            String Index = s+"\t"+f.getPath().substring(parentDirLength+1)+"\n";
            FileOutputStream fos = new FileOutputStream(new File(REPO+File.separator+DirParam.INDEX),true);
            fos.write(Index.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private HashMap<String,String> getIndexMap(){
        Data index = fileUtil.getFileByPath(REPO+File.separator+DirParam.INDEX);
        HashMap<String,String> indexMap = new HashMap<String, String>();
        for(String s:index.content){
            String [] ss = s.split("\t");
            indexMap.put(ss[1],ss[0]);
        }
        return indexMap;
    }
    private void setStatus(HashMap<String,String> index1,HashMap<String,String> index2){
        File status = new File(REPO+File.separator+DirParam.STATUS);
        if(!status.exists()) {
            try {
                status.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for(String path:index2.keySet()){
            if(index1.containsKey(path)){
                if(!index1.get(path).equals(index2.get(path))){
                    System.out.println("update:"+path);
                    writeStatus(status, FixType.UPDATE,path,index2.get(path));
                }
            }else{
                System.out.println("add:"+path);
                writeStatus(status, FixType.ADD,path,index2.get(path));
            }
        }
    }
    private void writeStatus(File f,String stats,String path,String md5){
        try {
            String status = stats+"\t"+md5+"\t"+path+"\n";
            FileOutputStream fos = new FileOutputStream(new File(REPO+File.separator+DirParam.STATUS),true);
            fos.write(status.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
