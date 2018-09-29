package org.dnsge.powerschoolapi.user;

import org.dnsge.powerschoolapi.client.PowerschoolClient;
import org.jsoup.nodes.Document;

import java.util.Map;

public class UserConfig {

    final PowerschoolClient client;
    final String username;
    final String password;
    final Document constructionDocument;
    final Map<String, String> authData;
    User user;

    public UserConfig(PowerschoolClient client, String username, String password, Document constructionDocument, Map<String, String> authData) {
        this.client = client;
        this.username = username;
        this.password = password;
        this.constructionDocument = constructionDocument;
        this.authData = authData;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public String getUsername() {
        return username;
    }

    public Map<String, String> getAuthData() {
        return authData;
    }

    public PowerschoolClient getClient() {
        return client;
    }
}
