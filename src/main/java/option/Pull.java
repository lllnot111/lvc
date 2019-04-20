package option;

import Base.Data;
import Constant.DirParam;
import Utils.FileUtil;
import Utils.HttpUtils;
import Utils.JSONUtils;
import httpParam.*;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class Pull extends AbstractOption{
    FileUtil fileUtil = new FileUtil();
    HttpUtils httpUtils = new HttpUtils();
    String s = System.getProperty(DirParam.ROOT_DIR);
    String PROJECT = DirParam.ROOT_DIR;//+File.separator+DirParam.REPO;
    String REPO = DirParam.ROOT_DIR+File.separator+DirParam.REPO;

    @Override
    public void option(String[] args) {
        pull();
    }
    /**
     * desc:拉取最近更新的内容
     * 1.获取所有的branch信息
     * 2.获取需要拉去的所有文件的md5
     * 3.更新branch
     * 4.拉取文件
     * 5.更新项目中的文件
     */
    public void pull(){
        String s = System.getProperty(DirParam.ROOT_DIR);
        File repo = new File("F:/test/aaaaaa.txt");
        String[] branches = getBranches("f:\\test_serve");
        for(String bran:branches){
            Data branch = fileUtil.getFileByPath(REPO+File.separator+DirParam.BRANCH+File.separator+bran);
            String[] set = getDataMd5(branch);
            if(set==null){
                System.out.println("出错了！");
                return;
            }
            for(String md5:set) {
                System.out.println(md5);
                getData(md5);
            }
        }
        updateProject();
    }

    private String[] getBranches(String path){
        PullBranchModel pullBranchModel = new PullBranchModel();
        pullBranchModel.setPath(path);
        String result = httpUtils.post("http://localhost:8080/getBranches", JSONUtils.serialize(pullBranchModel));
        System.out.println(result);
        return JSONUtils.deserialize(result,String[].class);
    }

    private String[] getDataMd5(Data branch){
        PullModel pullModel = new PullModel();
        pullModel.setBranch(branch.name);
        if(branch.content.size()>0)
            pullModel.setCommitMd5(branch.content.get(0));
        else if(!branch.file.exists()){
            try {
                branch.file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        pullModel.setPath("f:\\test_serve");
        String result = httpUtils.post("http://localhost:8080/pull", JSONUtils.serialize(pullModel));
        if(result.equals("")){
            return null;
        }
        String[] ss = JSONUtils.deserialize(result,String[].class);

        GetBranchModel gbm = new GetBranchModel();
        gbm.setPath("f:\\test_serve");
        gbm.setBranch(branch.name);
        String newBranchMd5 = httpUtils.post("http://localhost:8080/getBranchMd5", JSONUtils.serialize(gbm));
        fileUtil.writeFile(branch.file,newBranchMd5);
        System.out.println(branch.name+":"+newBranchMd5);

        return ss;
    }

    private void getData(String md5){
        PullFileModel pullFileModel = new PullFileModel();
        pullFileModel.setPath("f:\\test_serve");
        pullFileModel.setDataMd5(md5);
        String result = httpUtils.post("http://localhost:8080/pullFile", JSONUtils.serialize(pullFileModel));
        fileUtil.createData(DigestUtils.md5Hex(result),result);
    }

    private void updateProject(){
        Data head = fileUtil.getFileByPath(REPO+File.separator+DirParam.HEAD);
        Data branch = fileUtil.getFileByPath(REPO+File.separator+head.content.get(0));
        Data commit = fileUtil.getFileFromData(branch.content.get(0));
        Data tree = fileUtil.getFileFromData(commit.content.get(0).split("\t")[1]);
        for(String s:tree.content){
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
            fileUtil.writeFile(f,dat);;
        }

    }

}
