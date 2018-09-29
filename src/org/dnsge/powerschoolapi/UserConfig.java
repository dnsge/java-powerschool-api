package org.dnsge.powerschoolapi;

import org.jsoup.nodes.Document;

import java.util.Map;

public class UserConfig {

    final PowerschoolClient client;
    final String username;
    final String password;
    final Document constructionDocument;
    final Map<String, String> authData;
    User user;

    UserConfig(PowerschoolClient client, String username, String password, Document constructionDocument, Map<String, String> authData) {
        this.client = client;
        this.username = username;
        this.password = password;
        this.constructionDocument = constructionDocument;
        this.authData = authData;
    }

    void setUser(User user) {
        this.user = user;
    }

}
