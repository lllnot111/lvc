package option;

import Base.Data;
import Constant.DirParam;
import Utils.FileUtil;
import difflib.Chunk;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Diff extends AbstractOption {

    FileUtil fileUtil = new FileUtil();
    String s = System.getProperty(DirParam.ROOT_DIR);
    String REPOPARENT = s;//DirParam.ROOT_DIR;//+File.separator+DirParam.REPO;
    //String REPO = DirParam.ROOT_DIR+ File.separator+DirParam.REPO;
    String REPO = s+File.separator+DirParam.REPO;
    @Override
    public void option(String[] args) {
        HashMap<String,String> projectFilePath = new HashMap<String,String>();
        if(args.length==1){
            paramOne(projectFilePath);
        } else if(args.length==2){
            paramTwo(args[1],projectFilePath);
        }else if(args.length>=3){
            paramThree(args[1],args[2]);
        }

    }

    public void diffFile(Data now,Data old) {
        Patch<String> p = DiffUtils.diff(old.content, now.content);
        List<Delta<String>> deltas = p.getDeltas();

        int l = 0;
        for(Delta<String> delta:deltas){
            Chunk<String> oldChunk = delta.getOriginal();
            Chunk<String> nowChunk = delta.getRevised();
            for(;l<old.content.size()&&oldChunk.getPosition()!=l;l++){
                System.out.println("|"+old.content.get(l));
            }
            List<String> oldLines = oldChunk.getLines();
            if(oldLines.size()>0){
                for(String s:oldLines){
                    System.out.println("|-"+s);
                }
            }
            List<String> nowLines = nowChunk.getLines();
            if(nowLines.size()>0){
                for(String s:nowLines){
                    System.out.println("|+"+s);
                }
            }
        }
        /**
         * for (int i = 0; i < old.content.size(); i++) {
            String line = old.content.get(i);
            if(oldChunk.getPosition()==i){
                List<String> oldLines = oldChunk.getLines();
                if(oldLines.size()>0){
                    for(String s:oldLines){
                        System.out.println("-"+s);
                    }
                }
                List<String> nowLines = nowChunk.getLines();
                if(nowLines.size()>0){
                    for(String s:nowLines){
                        System.out.println("+"+s);
                    }
                }
                if(deltas.size()>pos) {
                    pos += 1;
                    if(deltas.size()>pos) {
                        oldChunk = deltas.get(pos).getOriginal();
                        nowChunk = deltas.get(pos).getRevised();
                    }
                }
            }else{
                System.out.println(line);
            }
            //if(deltas.get(pos).)
        }
        **/
    }

    private void travelFile(File f,HashMap<String,String> projectFilePath) {
        if (f.isDirectory()) {
            File[] fList = f.listFiles();
            for (File file : fList) {
                if(!file.getName().equals(".lvc"))
                    travelFile(file,projectFilePath);
            }
        }
        if (f.isFile()) {
            try {
                FileInputStream fis = new FileInputStream(f.getPath());
                projectFilePath.put(f.getCanonicalPath().substring(REPOPARENT.length()+1), DigestUtils.md5Hex(fis));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void paramOne(HashMap<String,String> projectFilePath){
        HashMap<String,String> indexMap = new HashMap<String,String>();
        Data index = fileUtil.getFileByPath(REPO+File.separator+DirParam.INDEX);

        for(String s:index.content){
            String[] line = s.split("\t");
            indexMap.put(line[1],line[0]);
        }
        travelFile(new File(REPOPARENT),projectFilePath);
        for(String path:projectFilePath.keySet()){
            //如果有相同的文件
            if(indexMap.containsKey(path)){
                //如果md5相同
                if(indexMap.get(path).equals(projectFilePath.get(path)))
                    System.out.println("NO:"+path);
                //如果不同
                else {
                    System.out.println("<=====Change:"+path+"=====>");
                    diffFile(fileUtil.getFileByPath(REPOPARENT+File.separator+path),fileUtil.getFileFromData(indexMap.get(path)));
                }
            }
        }
    }

    private void paramTwo(String commitMd5,HashMap<String,String> projectFilePath){
        HashMap<String,String> treeMap = new HashMap<String,String>();
        Data commit = fileUtil.getFileFromData(commitMd5);
        Data tree = fileUtil.getFileFromData(commit.content.get(0).split("\t")[1]);

        for(String s:tree.content){
            String[] line = s.split("\t");
            treeMap.put(line[1],line[0]);
        }
        travelFile(new File(REPOPARENT),projectFilePath);
        for(String path:projectFilePath.keySet()){
            if(treeMap.containsKey(path)){
                if(treeMap.get(path).equals(projectFilePath.get(path)))
                    System.out.println("NO:"+path);
                else {
                    System.out.println("<=====Change:"+path+"=====>");
                    diffFile(fileUtil.getFileByPath(REPOPARENT+File.separator+path),fileUtil.getFileFromData(treeMap.get(path)));
                }
            }
        }
    }

    private void paramThree(String commitMd51,String commitMd52){
        HashMap<String,String> treeMap1 = new HashMap<String,String>();
        Data commit1 = fileUtil.getFileFromData(commitMd51);
        Data tree1 = fileUtil.getFileFromData(commit1.content.get(0).split("\t")[1]);

        HashMap<String,String> treeMap2 = new HashMap<String,String>();
        Data commit2 = fileUtil.getFileFromData(commitMd52);
        Data tree2 = fileUtil.getFileFromData(commit2.content.get(0).split("\t")[1]);

        for(String s:tree1.content){
            String[] line = s.split("\t");
            treeMap1.put(line[1],line[0]);
        }

        for(String s:tree2.content){
            String[] line = s.split("\t");
            treeMap2.put(line[1],line[0]);
        }

        for(String path:treeMap2.keySet()){
            if(treeMap1.containsKey(path)){
                if(treeMap1.get(path).equals(treeMap2.get(path)))
                    System.out.println("NO:"+path);
                else {
                    System.out.println("<=====Change:"+path+"=====>");
                    diffFile(fileUtil.getFileFromData(treeMap1.get(path)),fileUtil.getFileFromData(treeMap2.get(path)));
                }
            }
        }
    }
}
