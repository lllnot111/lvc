package option;

import Base.Data;
import Constant.DirParam;
import Utils.FileUtil;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.nio.file.Files;

public class Commit extends AbstractOption{

    private FileUtil fileUtil = new FileUtil();
    String s = System.getProperty(DirParam.ROOT_DIR);
    //String REPO = DirParam.ROOT_DIR+File.separator+DirParam.REPO;
    String REPO = s+File.separator+DirParam.REPO;
    @Override
    public void option(String[] args) {
        commit();
    }

    /**
     * desc:提交当前的版本
     * 1.根据INDEX中的内容提交,根据index生成tree
     * 2.提交时生成tree对象跟commit对象，保存到版本库中tree对象记录目录结构，commit记录tree以及版本的主要信息
     * 3.将Branch中管理该分支的文件更新
     */
    public void commit(){
       Data status = fileUtil.getFileByPath(REPO+File.separator+DirParam.STATUS);
       if(status.content.size()!=0){
           String treeMd5 =  createTree();
           String statusMd5 = createStatus();
           createCommit(treeMd5,statusMd5);
           for(String s:status.content){
               String[] ss = s.split("\t");
               System.out.println(ss[0]+":"+ss[2]);
           }
           System.out.println("成功提交，共有"+status.content.size()+"个文件被提交！");
       }else{
           System.out.println("无新内容需要提交！");
       }
       status.file.delete();
    }

    private String createTree(){
        String md5 = "";
        try {
            File index = new File(REPO+File.separator+DirParam.INDEX);
            FileInputStream fis = new FileInputStream(index);
            md5= DigestUtils.md5Hex(fis);
            File newDir = new File(REPO+File.separator+DirParam.DATA+File.separator+ md5.substring(0, 2));
            if (!newDir.exists()) {
                newDir.mkdirs();
            }
            File newFile = new File(REPO+File.separator+DirParam.DATA +File.separator+newDir.getName() + File.separator + md5.substring(2));
            if (!newFile.exists()) {
                Files.copy(index.toPath(), newFile.toPath());
            }
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return md5;
    }

    private String createStatus(){
        String md5 = "";
        try {
            File status = new File(REPO+File.separator+DirParam.STATUS);
            FileInputStream fis = new FileInputStream(status);
            md5= DigestUtils.md5Hex(fis);
            File newDir = new File(REPO+File.separator+DirParam.DATA+File.separator+ md5.substring(0, 2));
            if (!newDir.exists()) {
                newDir.mkdirs();
            }
            File newFile = new File(REPO+File.separator+DirParam.DATA +File.separator+newDir.getName() + File.separator + md5.substring(2));
            if (!newFile.exists()) {
                Files.copy(status.toPath(), newFile.toPath());
            }
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return md5;
    }

    /**
     * 1.获取用户信息 x
     * 2.获取父节点信息 HEAD->Branch获取父节点信息 o
     * 3.获取tree信息 o
     * 4.生成commit，添加进仓库中 o
     * @param treeMd5
     * @return commitMd5
     */
    private String createCommit(String treeMd5,String statusMd5){
        if(treeMd5==null){
            System.out.println("提交失败！");
        }
        if(statusMd5==null){
            System.out.println("未更新项目或者未添加最近的更新！请使用add命令！");
        }
        String md5 = "";
        String parent = "parent"+"\t";
        String tree = "tree"+"\t"+treeMd5;
        String status = "status"+"\t"+statusMd5;
        Data head = fileUtil.getFileByPath(REPO+File.separator+DirParam.HEAD);
        if(head.content.size()==0){
            firstHead(head);
            parent+="null";
        }else{
            //如果head不是空文件，把branch的信息拿出来，并设置parent commit的信息
            Data branch = fileUtil.getFileByPath(REPO+File.separator+head.content.get(0));
            parent+=branch.content.get(0);
        }
        try {
            //commit中的信息
            String commitContent = tree+"\n"+parent+"\n"+status;
            //commit的md5值
            md5 = DigestUtils.md5Hex(commitContent);

            //将commit放进data中
            File newDir = new File(REPO+File.separator+DirParam.DATA+File.separator + md5.substring(0, 2));
            if (!newDir.exists()) {
                newDir.mkdirs();
            }
            File commit = new File(REPO+File.separator+DirParam.DATA+File.separator + md5.substring(0, 2)+File.separator+md5.substring(2));
            if(!commit.exists()){
                commit.createNewFile();
            }
            //将信息写进commit
            fileUtil.writeFile(commit,commitContent);
            //更新branch
            fileUtil.writeFile(new File(REPO+File.separator+head.content.get(0)),md5);//head.content.get(0)是branch的路径
        } catch (IOException e) {
            e.printStackTrace();
        }
        return md5;
    }

    private void firstHead(Data head){
        String masterPath = DirParam.BRANCH+File.separator+DirParam.MASTER;
        fileUtil.writeFile(head.file,masterPath);
        head.content.add(masterPath);
        File master = new File(REPO+File.separator+masterPath);
        if(!master.exists()){
            try {
                master.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void updateHead(String commitMd5){
    }
    private void updateBranch(String commitMd5){

        File head  = new File(REPO+File.separator+DirParam.HEAD);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(head)));
            String s = br.readLine();
            FileOutputStream headStream = new FileOutputStream(head);
            if(s == null){
                headStream.write((DirParam.BRANCH+"\t"+REPO+File.separator+DirParam.BRANCH+File.separator+DirParam.MASTER).getBytes());
            }
            headStream.flush();
            headStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //File branch  = new File(DirParam.ROOT_DIR+File.separator+DirParam.REPO+File.separator+DirParam.BRANCH+File.separator+branchName);
       // fileUtil.writeFile(branch,commitMd5);
    }

}
