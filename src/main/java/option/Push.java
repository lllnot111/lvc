package option;

import Base.Data;
import Constant.DirParam;
import Constant.TestPush;
import Utils.FileUtil;
import Utils.HttpUtils;
import Utils.JSONUtils;
import httpParam.PushModel;
import httpParam.SuccessResp;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class Push extends AbstractOption{
    FileUtil fileUtil = new FileUtil();
    HttpUtils httpUtils = new HttpUtils();
    String s = System.getProperty(DirParam.ROOT_DIR);
    //String REPO = DirParam.ROOT_DIR+File.separator+DirParam.REPO;
    String REPO = s+File.separator+DirParam.REPO;

    @Override
    public void option(String[] args) {
        push();
    }
    /**
     * desc:上传最近commit的代码
     * 1.检查是否符合上传条件
     * 2.更新服务端branch
     * 3.获取需要上传的文件的md5
     * 4.上传文件
     */
    public void push(){
        Data head = fileUtil.getFileByPath(REPO+File.separator+DirParam.HEAD);
        Data branch = fileUtil.getFileByPath(REPO+File.separator+head.content.get(0));

        if(check(branch)==1){
            HashSet<String> set = getDataMd5(branch);
            Iterator<String> iterable = set.iterator();
            while(iterable.hasNext()){
                upData(iterable.next());
            }
        }
    }
    private int check(Data branch){
        PushModel push = new PushModel();

        push.setBranch(branch.file.getName());
        push.setCommitMd5(branch.content.get(0));
        push.setPath("f:\\test_serve");
        String x = JSONUtils.serialize(push);
        String result = httpUtils.post("http://localhost:8080/push", JSONUtils.serialize(push));
        SuccessResp successResp = JSONUtils.deserialize(result,SuccessResp.class);
        return successResp.isSuccess;
    }

    private HashSet<String> getDataMd5(Data branch){
        HashSet<String> set = new HashSet<String>();
        Data commit = fileUtil.getFileFromData(branch.content.get(0));
        //需要上传的文件都在status里
        Data status = fileUtil.getFileFromData(commit.content.get(2).split("\t")[1]);
        for(String s:status.content){
            set.add(s.split("\t")[1]);
        }
        //commit也在data里，放进去
        set.add(commit.name);
        //当然，同样，tree也放进去
        set.add(commit.content.get(0).split("\t")[1]);
        set.add(status.name);
        //最后，还有status，也得加进去
        return set;
    }
    private int upData(String md5){
        Data data = fileUtil.getFileFromData(md5);
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        entityBuilder.setContentType(ContentType.MULTIPART_FORM_DATA);
        entityBuilder.addBinaryBody("md5",md5.getBytes());
        entityBuilder.addBinaryBody("file",data.file);
        entityBuilder.addBinaryBody("lvcRepoPath","F:\\test_serve\\".getBytes());
        try {
            httpUtils.postByForm("http://localhost:8080/upData",entityBuilder);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }
}
