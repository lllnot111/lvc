package Utils;

import Base.Data;
import Constant.DirParam;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;

public class BranchUtil {
    FileUtil fileUtil = new FileUtil();
    String s = System.getProperty(DirParam.ROOT_DIR);
    String PROJECT = DirParam.ROOT_DIR;//+File.separator+DirParam.REPO;
    String REPO = DirParam.ROOT_DIR+ File.separator+DirParam.REPO;

    public void changeBranch(String branchName){

        //通过tree获取项目当前的目录结构
        Data head = fileUtil.getFileByPath(REPO+File.separator+DirParam.HEAD);
        String nowBranchPath = head.content.get(0);
        Data nowBranch = fileUtil.getFileByPath(REPO+File.separator+nowBranchPath);
        String nowCommitMd5 = nowBranch.content.get(0);
        Data nowCommit = fileUtil.getFileFromData(nowCommitMd5);
        String nowTreeMd5 = nowCommit.content.get(0).split("\t")[1];
        Data nowTree = fileUtil.getFileFromData(nowTreeMd5);

        //把当前的目录结构添加到map里
        HashMap<String,String> nowTreeMap = new HashMap<String, String>();
        for(String s:nowTree.content){
            String[] ss = s.split("\t");
            nowTreeMap.put(ss[1],ss[0]);
        }

        //通过tree获取切换的分支的目录结构
        Data targetBranch = fileUtil.getFileByPath(REPO+File.separator+DirParam.BRANCH+File.separator+branchName);
        Data commit = fileUtil.getFileFromData(targetBranch.content.get(0));
        Data targeTree = fileUtil.getFileFromData(commit.content.get(0).split("\t")[1]);
        HashMap<String,String> targeTreeMap = new HashMap<String, String>();

        //把目标分支的目录结构添加到map里
        for(String s:targeTree.content){
            String[] ss = s.split("\t");
            targeTreeMap.put(ss[1],ss[0]);
        }

        //查询分支中不存在但是当前项目中存在的文件，删除这些文件
        for(String s:nowTreeMap.keySet()){
            if(!targeTreeMap.containsKey(s)){
                File f = new File(PROJECT+File.separator+s);
                f.delete();
            }
        }
        //最后，将当前项目的文件替换成目标分支的文件
        for(String s:targeTree.content){
            String[] ss = s.split("\t");
            File f = new File(PROJECT+File.separator+ss[1]);
            if(!f.exists()) {
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Data data = fileUtil.getFileFromData(ss[0]);
            String dat = new String(fileUtil.FileString(data.file));
            fileUtil.writeFile(f,dat);
        }

        //最后的最后，更新INDEX
        try {
            File index = new File(REPO+File.separator+DirParam.INDEX);
            index.delete();
            Files.copy(targeTree.file.toPath(), index.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void changeCommit(String nowCommitMd5,String targetCommitMd5){

        //通过tree来获取项目当前的目录结构
        Data nowCommit = fileUtil.getFileFromData(nowCommitMd5);
        String treeMd5 = nowCommit.content.get(0).split("\t")[1];
        Data nowTree = fileUtil.getFileFromData(treeMd5);
        HashMap<String,String> nowTreeMap = new HashMap<String, String>();
        for(String s:nowTree.content){
            String[] ss = s.split("\t");
            nowTreeMap.put(ss[1],ss[0]);
        }

        //通过tree来获取目标版本的目录结构
        Data targetCommit = fileUtil.getFileFromData(targetCommitMd5);
        String targeTreeMd5 = targetCommit.content.get(0).split("\t")[1];
        Data targeTree = fileUtil.getFileFromData(targeTreeMd5);
        HashMap<String,String> targeTreeMap = new HashMap<String, String>();
        for(String s:targeTree.content){
            String[] ss = s.split("\t");
            targeTreeMap.put(ss[1],ss[0]);
        }

        //查询目标版本中不存在但是当前项目中存在的文件，删除这些文件
        for(String s:nowTreeMap.keySet()){
            if(!targeTreeMap.containsKey(s)){
                File f = new File(PROJECT+File.separator+s);
                f.delete();
            }
        }

        //最后，将当前项目的文件替换成目标分支的文件
        for(String s:targeTree.content){
            String[] ss = s.split("\t");
            File f = new File(PROJECT+File.separator+ss[1]);
            if(!f.exists()) {
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Data data = fileUtil.getFileFromData(ss[0]);
            String dat = new String(fileUtil.FileString(data.file));
            fileUtil.writeFile(f,dat);
        }

        //最后的最后，更新INDEX
        try {
            File index = new File(REPO+File.separator+DirParam.INDEX);
            index.delete();
            Files.copy(targeTree.file.toPath(), index.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

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
        }
    }

    /**
     * desc:获取项目中所有文件的路径以及md5
     */
    private HashMap<String,String> getName(File f, int parentDirLength) {
        HashMap<String,String> map = new HashMap<String, String>();
        try {
            FileInputStream fis = new FileInputStream(f.getPath());
            String s = DigestUtils.md5Hex(fis);
            writeIndex(f,s,parentDirLength);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    private void writeIndex(File f,String s,int parentDirLength){
            String Index = s+"\t"+f.getPath().substring(parentDirLength+1);
    }
    /**
     * desc:遍历当前目录，生成最新的index，并将最近修改过的文件添加进版本库
     * 1.遍历目录
     * 2.将更新过的文件添加进版本库
     * 3.将当前的目录结构以及目录中文件所对应的版本库中的文件写入index
     */
    int count;
    public void countLine(File f,int count) {
        if (f.isDirectory()) {
            File[] fList = f.listFiles();
            for (File file : fList) {
                if(!file.getName().equals(".lvc"))
                    countLine(file,count);
            }
        }
        if (f.isFile()) {
            this.count+= fileUtil.readFile(f).size();
            System.out.println(this.count);
        }
    }

}
