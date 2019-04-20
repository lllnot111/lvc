
import Base.Data;
import Utils.BranchUtil;
import Utils.FileUtil;
import difflib.DiffUtils;
import option.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Main {
    public static void main(String[] args) {
//        new Init().initRepo();
//          new Add().add();new Commit().commit();
//        new Pull().pull();
//        new Push().push();
//        new BranchUtil().countLine(new File(System.getProperty("user.dir") + File.separator + "src"), 0);
//        optionsCN(args);
//        Data now = new FileUtil().getFileByPath("F:\\new.txt");
//        Data old = new FileUtil().getFileByPath("F:\\old.txt");
//        new Diff().diffFile(now,old);
//        new Diff().option(args);

        OptionFactory optionFactory = new OptionFactory();
        if(args.length<1){
            System.out.println("请输入操作内容！");
        }else{
            AbstractOption option = optionFactory.option(args[0]);
            HashSet<String> set = new HashSet<>();
            set.add("提交");
            set.add("commit");
            set.add("上传");
            set.add("push");
            set.add("回滚");
            set.add("reset");
            if(set.contains(args[0])){
              // // new Pull().pull();
            }
            if(option!=null){
                option.option(args);
            }else{
                System.out.println("不存在的操作！");
            }
        }
        //new BranchUtil().countLine(new File("F:\\mygit\\lvc\\src"),0);
    }


}
