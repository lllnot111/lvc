package option;

import Base.Data;
import Constant.DirParam;
import Utils.BranchUtil;
import Utils.FileUtil;
import Utils.HttpUtils;

import java.io.File;

public class Reset extends AbstractOption{

    FileUtil fileUtil = new FileUtil();
    HttpUtils httpUtils = new HttpUtils();
    BranchUtil branchUtil = new BranchUtil();
    String s = System.getProperty(DirParam.ROOT_DIR);
    String REPO = DirParam.ROOT_DIR+ File.separator+DirParam.REPO;
    @Override
    public void option(String[] args) {
        if(args.length<2){
            System.out.println("请选择需要回滚的版本！");
            return;
        }
        //目标commit
        String targetCommitMd5 = args[1];

        Data head = fileUtil.getFileByPath(REPO+File.separator+DirParam.HEAD);
        Data branch = fileUtil.getFileByPath(REPO+File.separator+head.content.get(0));
        //当前commit
        String nowCommitMd5 = branch.content.get(0);

        //通过commit，查找是否目标commit是否存在于该branch
        Data commitNow = fileUtil.getFileFromData(nowCommitMd5);
        boolean exists = false;
        while (commitNow.file.exists()) {
            if(commitNow.name.equals(targetCommitMd5)){
                System.out.println("ok");
                exists = true;
                break;
            }
            String parentMd5 = commitNow.content.get(1).split("\t")[1];
            commitNow = fileUtil.getFileFromData(parentMd5);
        }
        if(exists) {
            Data commit = fileUtil.getFileFromData(targetCommitMd5);
            fileUtil.writeFile(branch.file,targetCommitMd5);
            branchUtil.changeCommit(nowCommitMd5,targetCommitMd5);
        }else{
            System.out.println("该分支不存在！");
        }
    }
}
