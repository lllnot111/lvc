package option;

import Base.Data;
import Constant.DirParam;
import Utils.BranchUtil;
import Utils.FileUtil;
import Utils.HttpUtils;

import java.io.File;
import java.io.IOException;

public class ChangeBranch extends AbstractOption {
    FileUtil fileUtil = new FileUtil();
    BranchUtil branchUtil = new BranchUtil();
    String s = System.getProperty(DirParam.ROOT_DIR);
    String PROJECT = DirParam.ROOT_DIR;//+File.separator+DirParam.REPO;
    String REPO = DirParam.ROOT_DIR + File.separator + DirParam.REPO;

    @Override
    public void option(String[] args) {
        if (args.length > 1)
            changeBranch(args[1]);
        else{
            System.out.println("参数错误！");
        }
    }

    public void changeBranch(String branchName) {
        Data branch = fileUtil.getFileByPath(REPO + File.separator + DirParam.BRANCH + File.separator + branchName);
        if (!branch.file.exists()) {
            System.out.println("该分支不存在！");
            return;
        }
        branchUtil.changeBranch(branchName);
        Data head = fileUtil.getFileByPath(REPO + File.separator + DirParam.HEAD);
        fileUtil.writeFile(head.file, DirParam.BRANCH + File.separator + branchName);
    }

}
