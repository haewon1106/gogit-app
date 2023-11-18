package com.gogit.gogit_app.model.github.org;

public class Organization {
    private String login;
    private String avatar_url;

    public Organization(String login, String avater_url) {
        this.login = login;
        this.avatar_url = avater_url;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }


}
