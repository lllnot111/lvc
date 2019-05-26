package option;

import Base.Data;
import Constant.DirParam;
import Constant.FixType;
import Utils.FileUtil;
import difflib.Chunk;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Merge extends AbstractOption {

    FileUtil fileUtil = new FileUtil();
    String s = System.getProperty(DirParam.ROOT_DIR);
    String PROJECT = DirParam.ROOT_DIR;//+File.separator+DirParam.REPO;
    //String REPO = DirParam.ROOT_DIR + File.separator + DirParam.REPO;
    String REPO = s + File.separator + DirParam.REPO;

    @Override
    public void option(String[] args) {
        if (args.length >= 3)
            merge(args[1], args[2]);
        else{
            System.out.println("参数错误！");
        }
    }

    private void merge(String branchFromName, String branchToName) {
        Data branchFrom = fileUtil.getFileByPath(REPO + File.separator+DirParam.BRANCH+File.separator+ branchFromName);
        Data branchTo = fileUtil.getFileByPath(REPO + File.separator +DirParam.BRANCH+File.separator+ branchToName);
        Data commitFrom = fileUtil.getFileFromData(branchFrom.content.get(0));
        Data comnitTo = fileUtil.getFileFromData(branchTo.content.get(0));
        Data commiTemp = commitFrom;
        String parentMd5 = commiTemp.content.get(1).split("\t")[1];
        boolean needMerge = true;
        while (!parentMd5.equals("null")) {
            if (parentMd5.equals(comnitTo.content.get(1).split("\t")[1])) {
                needMerge = false;
                break;
            }
            commiTemp = fileUtil.getFileFromData(parentMd5);
            parentMd5 = commiTemp.content.get(0).split("\t")[1];
        }
        if (!needMerge) {
            Data head = fileUtil.getFileByPath(REPO + File.separator + DirParam.HEAD);
            fileUtil.writeFile(head.file, DirParam.BRANCH + File.separator + branchToName);
            fileUtil.writeFile(branchTo.file, commitFrom.name);
        } else {
            String md5 = conflict(commitFrom, comnitTo);
            Data head = fileUtil.getFileByPath(REPO + File.separator + DirParam.HEAD);
            if (md5 != null) {
                fileUtil.writeFile(head.file, DirParam.BRANCH + File.separator + branchToName);
                fileUtil.writeFile(branchTo.file, md5);
            }
        }

    }

    private String conflict(Data commit1, Data commit2) {

        HashSet<String> commitSet = new HashSet<>();
        String commitMd5 = commit2.content.get(1).split("\t")[1];
        while (!commitMd5.equals("null")) {
            commitSet.add(commitMd5);
            Data commitBas = fileUtil.getFileFromData(commitMd5);
            commitMd5 = commitBas.content.get(1).split("\t")[1];

        }

        commitMd5 = commit1.content.get(1).split("\t")[1];
        Data commitBase = null;
        while (!commitMd5.equals("null")) {
            if (commitSet.contains(commitMd5)) {
                commitBase = fileUtil.getFileFromData(commitMd5);
                break;
            }
            Data commitBas = fileUtil.getFileFromData(commitMd5);
            commitMd5 = commitBas.content.get(1).split("\t")[1];


        }
        if (commitBase == null) {
            System.out.println("数据出错！无法合并！");
            return null;
        }

        HashMap<String, String> treeMap1 = new HashMap<String, String>();
        Data tree1 = fileUtil.getFileFromData(commit1.content.get(0).split("\t")[1]);

        HashMap<String, String> treeMap2 = new HashMap<String, String>();
        Data tree2 = fileUtil.getFileFromData(commit2.content.get(0).split("\t")[1]);


        HashMap<String, String> treeMapBase = new HashMap<String, String>();
        Data treeBase = fileUtil.getFileFromData(commitBase.content.get(0).split("\t")[1]);
        for (String s : tree1.content) {
            String[] line = s.split("\t");
            treeMap1.put(line[1], line[0]);
        }
        for (String s : tree2.content) {
            String[] line = s.split("\t");
            treeMap2.put(line[1], line[0]);
        }

        for (String s : treeBase.content) {
            String[] line = s.split("\t");
            treeMapBase.put(line[1], line[0]);
        }


        for (String s : treeMapBase.keySet()) {
            if (!treeMap1.containsKey(s) && !treeMap2.containsKey(s)) {
                treeMapBase.remove(s);
            }
        }
        HashMap<String, String> mergeMap = new HashMap<>();
        for (String s : treeMap1.keySet()) {
            if (!treeMap2.containsKey(s)) {
                mergeMap.put(s, treeMap1.get(s));
                treeMap1.remove(s);
            }
        }
        for (String s : treeMap2.keySet()) {
            if (!treeMap1.containsKey(s)) {
                mergeMap.put(s, treeMap2.get(s));
                treeMap2.remove(s);
            }
        }
        HashMap<String, String[]> conflictMap = new HashMap<>();
        for (String s : treeMap2.keySet()) {
            if (!treeMapBase.containsKey(s)) {
                conflictMap.put(s, new String[]{treeMap1.get(s), treeMap2.get(s)});
            }
            if (treeMapBase.containsKey(s)) {
                if (treeMap1.get(s).equals(treeMapBase.get(s))) {
                    mergeMap.put(s, treeMap2.get(s));
                } else if (treeMap2.get(s).equals(treeMapBase.get(s))) {
                    mergeMap.put(s, treeMap1.get(s));
                } else {
                    conflictMap.put(s, new String[]{treeMap1.get(s), treeMap2.get(s)});
                }
            }
        }
        if (conflictMap.size() == 0) {
            Data index = fileUtil.getFileByPath(REPO + File.separator + DirParam.INDEX);
            fileUtil.cleanFile(index.file);
            for (String s : mergeMap.keySet()) {
                fileUtil.appendFile(index.file, mergeMap.get(s) + "\t" + s);
            }

            File status = new File(REPO + File.separator + DirParam.STATUS);
            setStatus(status, treeMapBase, mergeMap);
            updateProject(mergeMap);
        }

        if (conflictMap.size() > 0) {
            System.out.println("有冲突！请解决冲突后再提交一次！");

            diff(conflictMap);

            Data index = fileUtil.getFileByPath(REPO + File.separator + DirParam.INDEX);
            fileUtil.cleanFile(index.file);
            for (String s : mergeMap.keySet()) {
                fileUtil.appendFile(index.file, mergeMap.get(s) + "\t" + s);
            }

            File status = new File(REPO + File.separator + DirParam.STATUS);
            setStatus(status, treeMapBase, mergeMap);
        }

        String content = "";
        String md5 = "";
        try {
            FileInputStream fis = new FileInputStream(new File(REPO + File.separator + DirParam.INDEX));
            content += "tree\t" + DigestUtils.md5Hex(fis) + "\n";
            content += "parent\t" + commit2.name + "\n";
            fis = new FileInputStream(new File(REPO + File.separator + DirParam.STATUS));
            content += "status\t" + DigestUtils.md5Hex(fis) + "\n";
            content += "merge\t" + commit1.name + "\n";
            md5 = DigestUtils.md2Hex(content.getBytes());
            fileUtil.createFile(REPO + File.separator + DirParam.DATA + File.separator + md5.substring(0, 2) + File.separator + md5.substring(2), content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return md5;
    }

    public void diff(HashMap<String, String[]> conflictMap) {
        for(String s:conflictMap.keySet()){
            System.out.println("<====="+s+"=====>");
            String[] ss = conflictMap.get(s);
            Data from = fileUtil.getFileFromData(ss[0]);
            Data to = fileUtil.getFileFromData(ss[1]);
            diffFile(PROJECT+File.separator+s,from,to);
        }

    }

    public void diffFile(String path,Data now, Data old) {
        Patch<String> p = DiffUtils.diff(old.content, now.content);
        List<Delta<String>> deltas = p.getDeltas();
        File file = new File(path);
        fileUtil.createFile(path,"");
        int l = 0;
        for (Delta<String> delta : deltas) {
            Chunk<String> oldChunk = delta.getOriginal();
            Chunk<String> nowChunk = delta.getRevised();
            for (; l < old.content.size() && oldChunk.getPosition() != l; l++) {
                System.out.println("|" + old.content.get(l));
                fileUtil.appendFile(file,old.content.get(l));
            }
            List<String> oldLines = oldChunk.getLines();
            if (oldLines.size() > 0) {
                for (String s : oldLines) {
                    System.out.println("|-" + s);
                    fileUtil.appendFile(file,"-" + s);
                }
            }
            List<String> nowLines = nowChunk.getLines();
            if (nowLines.size() > 0) {
                for (String s : nowLines) {
                    System.out.println("|+" + s);
                    fileUtil.appendFile(file,"+" + s);
                }
            }
        }
    }

    private void setStatus(File status, HashMap<String, String> index1, HashMap<String, String> index2) {
        if (!status.exists()) {
            try {
                status.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        fileUtil.cleanFile(status);
        for (String path : index2.keySet()) {
            if (index1.containsKey(path)) {
                if (!index1.get(path).equals(index2.get(path))) {
                    writeStatus(status, FixType.UPDATE, path, index2.get(path));
                }
            } else {
                writeStatus(status, FixType.ADD, path, index2.get(path));
            }
        }
    }

    private void writeStatus(File f, String stats, String path, String md5) {
        String status = stats + "\t" + md5 + "\t" + path + "\n";
        fileUtil.appendFile(f, status);
    }

    private void updateProject(HashMap<String, String> mergeMap) {

        for (String s : mergeMap.keySet()) {
            File f = new File(PROJECT + File.separator + s);
            if (!f.exists()) {
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Data data = fileUtil.getFileFromData(mergeMap.get(s));
            String dat = new String(fileUtil.FileString(data.file));
            fileUtil.writeFile(f, dat);
        }

    }
}
