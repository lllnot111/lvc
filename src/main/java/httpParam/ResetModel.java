package httpParam;

public class ResetModel {
    private String commitMd5;
    private String branchName;
    private String path;

    public String getCommitMd5() {
        return commitMd5;
    }

    public void setCommitMd5(String commitMd5) {
        this.commitMd5 = commitMd5;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }
}
