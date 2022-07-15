package org.testar.settingsdialog.vcs;

public class GitCredentials {
    private String username;
    private String password;

    public GitCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public GitCredentials() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
