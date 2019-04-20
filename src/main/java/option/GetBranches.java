package option;

import Base.Data;
import Constant.DirParam;
import Utils.FileUtil;
import com.sun.org.apache.regexp.internal.RE;

import java.io.File;

public class GetBranches extends AbstractOption{

    FileUtil fileUtil = new FileUtil();
    String s = System.getProperty(DirParam.ROOT_DIR);
    String REPOPARENT = DirParam.ROOT_DIR;//+File.separator+DirParam.REPO;
    String REPO = DirParam.ROOT_DIR+ File.separator+DirParam.REPO;
    @Override
    public void option(String[] args) {
        if(args.length<=1){
            Data head = fileUtil.getFileByPath(REPO+File.separator+DirParam.HEAD);
            File branches = new File(REPO+File.separator+DirParam.BRANCH);
            if(branches.exists()) {
                if (branches.isDirectory()) {
                    File[] files = branches.listFiles();
                    for(File f:files){
                        String branch = DirParam.BRANCH+File.separator+f.getName();
                        if(branch.equals(head.content.get(0)))
                            System.out.println("*"+f.getName());
                        else
                            System.out.println(" "+f.getName());
                    }
                }
            }
        }
    }
}
