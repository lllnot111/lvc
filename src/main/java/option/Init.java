package option;

import Constant.DirParam;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.nio.file.Files;

public class Init {
    /**
     * desc:初始化版本库
     * 1.生成Branch
     * 2.生成Data
     * 3.生成HEAD
     * 4.生成INDEX
     * 5.生成Info
     */
    public void initRepo() {
        String s = System.getProperty(DirParam.ROOT_DIR);
        File repo = new File(DirParam.REPO);
        if (!repo.exists())
            repo.mkdirs();
        createDir(repo.getPath()+File.separator+DirParam.BRANCH);
        createDir(repo.getPath()+File.separator+DirParam.DATA);
        createDir(repo.getPath()+File.separator+DirParam.INFO);
        createFile(repo.getPath()+File.separator+DirParam.HEAD);
        createFile(repo.getPath()+File.separator+DirParam.INDEX);
    }

    private void createFile(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        try {
//            FileWriter fw = new FileWriter(file);
//            BufferedWriter bw = new BufferedWriter(fw);
//            bw.write("Branch:Master\tUser:Root\tCreateTime:"+System.currentTimeMillis()+"\n");
//            bw.flush();
//            bw.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }
    private void createDir(String dirName) {
        File newDir = new File(dirName);
        if (!newDir.exists()) {
            newDir.mkdirs();
        }
    }
}
