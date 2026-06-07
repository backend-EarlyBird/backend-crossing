package io.rapa.backendcrossing.security.domain;

import lombok.Data;

@Data
public class OauthHolderSingleton {
    private static OauthHolderSingleton oauthHolderSingleton;
    private String redirect_uri;
    private String state;
    private OauthHolderSingleton(){
        this.redirect_uri = "";
        this.state = "";
    }
    public static OauthHolderSingleton getInstance(){
        if(oauthHolderSingleton == null) oauthHolderSingleton = new OauthHolderSingleton();
        return oauthHolderSingleton;
    }
}
