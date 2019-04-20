package option;

import Base.Data;
import Constant.DirParam;
import Utils.BranchUtil;
import Utils.FileUtil;

import java.io.File;

public class Log extends AbstractOption{
    FileUtil fileUtil = new FileUtil();
    BranchUtil branchUtil = new BranchUtil();
    String s = System.getProperty(DirParam.ROOT_DIR);
    String PROJECT = DirParam.ROOT_DIR;//+File.separator+DirParam.REPO;
    String REPO = DirParam.ROOT_DIR+ File.separator+DirParam.REPO;

    @Override
    public void option(String[] args) {
        log();
    }
    public void log(){
        Data head = fileUtil.getFileByPath(REPO+File.separator+DirParam.HEAD);
        Data branch = fileUtil.getFileByPath(REPO+File.separator+head.content.get(0));
        Data commit = fileUtil.getFileFromData(branch.content.get(0));
        while(!commit.name.equals("null")){
            System.out.println("commit:"+commit.name);
            /**
             * 3的原因是因为，commit的前3行存储的是tree、parent、status，
             * 是不需要展示给用户的信息，剩下的信息，如果存在的话，就展示给用户
             **/
            if(commit.content.size()>3){
                for(String s:commit.content) {
                    String [] ss = s.split("\t");
                    System.out.println("\t"+ss[0]+":"+ss[1]);
                }
            }
            /**
             * 所有的信息显示完后，获取下一个commit，直到拉完整个分支
             * 分支会在parent=null时到达最后一个
             * 这时虽然会加载commit，但是commit中不存在数据
             */
            commit = fileUtil.getFileFromData(commit.content.get(1).split("\t")[1]);
        }
    }

}
