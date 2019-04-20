import option.*;

import java.util.HashMap;

public class OptionFactory {
    HashMap<String, AbstractOption> optionMap = new HashMap<>();

    public OptionFactory(){
        optionMap.put("初始化",new Init());
        optionMap.put("init",new Init());
        optionMap.put("添加", new Add());
        optionMap.put("add",new Add());
        optionMap.put("提交",new Commit());
        optionMap.put("commit",new Commit());
        optionMap.put("上传",new Push());
        optionMap.put("push",new Push());
        optionMap.put("拉取",new Pull());
        optionMap.put("pull",new Pull());
        optionMap.put("记录",new Log());
        optionMap.put("log",new Log());
        optionMap.put("创建分支",new CreateBranch());
        optionMap.put("createBranch",new CreateBranch());
        optionMap.put("查看分支",new GetBranches());
        optionMap.put("getBranches",new GetBranches());
        optionMap.put("切换分支",new ChangeBranch());
        optionMap.put("changeBranch",new ChangeBranch());
        optionMap.put("回滚",new Reset());
        optionMap.put("reset",new Reset());
        optionMap.put("合并",new Merge());
        optionMap.put("merge",new Merge());
        optionMap.put("比较",new Diff());
        optionMap.put("diff",new Diff());

    }
    public AbstractOption option(String option){
        if (optionMap.containsKey(option)) {
            return optionMap.get(option);
        }else{
            return null;
        }
    }

}
