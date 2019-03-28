package option;

import Base.Data;
import Constant.DirParam;
import Utils.FileUtil;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.nio.file.Files;

public class Commit {

    private FileUtil fileUtil = new FileUtil();
    /**
     * desc:提交当前的版本
     * 1.根据INDEX中的内容提交,根据index生成tree
     * 2.提交时生成tree对象跟commit对象，保存到版本库中tree对象记录目录结构，commit记录tree以及版本的主要信息
     * 3.将Branch中管理该分支的文件更新
     */
    public void commit(){
       String treeMd5 =  createTree();
       String commitMd5 = createCommit(treeMd5);
       updateHead(commitMd5);
    }

    private String createTree(){
        String md5 = "";
        try {
            File index = new File(DirParam.REPO+File.separator+DirParam.INDEX);
            FileInputStream fis = new FileInputStream(index);
            md5= DigestUtils.md5Hex(fis);
            File newDir = new File(DirParam.REPO+File.separator+DirParam.DATA+File.separator+ md5.substring(0, 2));
            if (!newDir.exists()) {
                newDir.mkdirs();
            }
            File newFile = new File(DirParam.REPO+File.separator+DirParam.DATA +File.separator+newDir.getName() + File.separator + md5.substring(2));
            if (!newFile.exists()) {
                Files.copy(index.toPath(), newFile.toPath());
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
    private String createCommit(String treeMd5){
        if(treeMd5==null){
            System.out.println("提交失败！");
        }
        String md5 = "";
        String parent = "parent"+"\t";
        String tree = "tree"+"\t"+treeMd5;
        Data head = fileUtil.getFileByPath(DirParam.REPO+File.separator+DirParam.HEAD);
        if(head.content.size()==0){
            firstHead(head);
            parent+="null";
        }else{
            Data branch = fileUtil.getFileByPath(head.content.get(0));
            parent+=branch.content.get(0);
        }
        File xf = new File(DirParam.REPO+File.separator+DirParam.DATA+File.separator+"commit");
        fileUtil.writeFile(xf,tree+"\n"+parent);
        try {
            String commitContent = tree+"\n"+parent;
            md5 = DigestUtils.md5Hex(commitContent);

            String md52 = DigestUtils.md5Hex(new FileInputStream(xf));
            boolean x = md5.equals(md52);

            File newDir = new File(DirParam.REPO+File.separator+DirParam.DATA+File.separator + md5.substring(0, 2));
            if (!newDir.exists()) {
                newDir.mkdirs();
            }
            File commit = new File(DirParam.REPO+File.separator+DirParam.DATA+File.separator + md5.substring(0, 2)+File.separator+md5.substring(2));
            if(!commit.exists()){
                commit.createNewFile();
            }
            fileUtil.writeFile(commit,commitContent);
            fileUtil.writeFile(new File(head.content.get(0)),md5);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return md5;
    }

    private void firstHead(Data head){
        String masterPath = DirParam.REPO+File.separator+DirParam.BRANCH+File.separator+DirParam.MASTER;
        fileUtil.writeFile(head.file,masterPath);
        head.content.add(masterPath);
        File master = new File(masterPath);
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

        File head  = new File(DirParam.REPO+File.separator+DirParam.HEAD);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(head)));
            String s = br.readLine();
            FileOutputStream headStream = new FileOutputStream(head);
            if(s == null){
                headStream.write((DirParam.BRANCH+"\t"+DirParam.REPO+File.separator+DirParam.BRANCH+File.separator+DirParam.MASTER).getBytes());
            }
            headStream.flush();
            headStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //File branch  = new File(DirParam.REPO+File.separator+DirParam.BRANCH+File.separator+branchName);
       // fileUtil.writeFile(branch,commitMd5);
    }
}
