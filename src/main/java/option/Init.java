package option;

import Constant.DirParam;
import org.apache.commons.codec.digest.DigestUtils;

import java.awt.geom.QuadCurve2D;
import java.io.*;
import java.nio.file.Files;

public class Init extends AbstractOption{
    String s = System.getProperty(DirParam.ROOT_DIR);
    String REPO = DirParam.ROOT_DIR+File.separator+DirParam.REPO;

    @Override
    public void option(String[] args) {
        initRepo();
    }
    /**
     * desc:初始化版本库
     * 1.生成Branch
     * 2.生成Data
     * 3.生成HEAD
     * 4.生成INDEX
     * 5.生成Info
     */
    public void initRepo() {
        File repo = new File(REPO);
        if (!repo.exists())
            repo.mkdirs();
        //需要创建的目录
        createDir(repo.getPath()+File.separator+DirParam.BRANCH);
        createDir(repo.getPath()+File.separator+DirParam.DATA);
        createDir(repo.getPath()+File.separator+DirParam.INFO);
        //需要创建的文件
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

    }
    private void createDir(String dirName) {
        File newDir = new File(dirName);
        if (!newDir.exists()) {
            newDir.mkdirs();
        }
    }

}
