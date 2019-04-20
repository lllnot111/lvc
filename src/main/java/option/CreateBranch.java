package option;

import Base.Data;
import Constant.DirParam;
import Utils.FileUtil;

import java.io.File;

public class CreateBranch extends AbstractOption{
    FileUtil fileUtil = new FileUtil();
    String s = System.getProperty(DirParam.ROOT_DIR);
    String REPOPARENT = DirParam.ROOT_DIR;//+File.separator+DirParam.REPO;
    String REPO = DirParam.ROOT_DIR+ File.separator+DirParam.REPO;

    @Override
    public void option(String[] args) {
        if(args.length>2)
            createBranch(args[1],args[2]);
        else {
            System.out.println("参数错误！");
        }
    }

    public void createBranch(String newBranch,String oldBranch){
        Data head = fileUtil.getFileByPath(REPO+File.separator+DirParam.HEAD);
        Data oldBran = fileUtil.getFileByPath(REPO+File.separator+DirParam.BRANCH+File.separator+oldBranch);
        if(!oldBran.file.exists()){
            System.out.println("该分支不存在！");
            return;
        }
        fileUtil.createFile(REPO+File.separator+DirParam.BRANCH+File.separator+newBranch,oldBran.content.get(0));
        fileUtil.writeFile(head.file,DirParam.BRANCH+File.separator+newBranch);
    }
}
