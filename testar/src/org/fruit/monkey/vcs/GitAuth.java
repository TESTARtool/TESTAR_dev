package org.fruit.monkey.vcs;

public class GitAuth {
    private String username;
    private String password;

    public GitAuth(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public GitAuth() {
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
