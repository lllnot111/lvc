package httpParam;

public class PushModel {

    private String path;
    private String branch;
    private String commitMd5;
    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    public String getCommitMd5() {
        return commitMd5;
    }

    public void setCommitMd5(String commitMd5) {
        this.commitMd5 = commitMd5;
    }
}
