package org.dnsge.powerschoolapi.user;

import org.dnsge.powerschoolapi.client.PowerschoolClient;
import org.jsoup.nodes.Document;

import java.util.Map;

/**
 * Class that represents a User's configuration
 *
 * @author Daniel Sage
 * @version 0.1
 * @see User
 */
public class UserConfig {

    final PowerschoolClient client;
    final String username;
    final String password;
    private final Document constructionDocument;
    final Map<String, String> authData;
    User user;

    /**
     * Basic Constructor for a new UserConfig
     *
     * @param client PowerschoolClient that is used by this config
     * @param username User username
     * @param password User password
     * @param constructionDocument JSoup Document to construct course information from
     * @param authData Authentication cookies for User
     */
    public UserConfig(PowerschoolClient client, String username, String password, Document constructionDocument, Map<String, String> authData) {
        this.client = client;
        this.username = username;
        this.password = password;
        this.constructionDocument = constructionDocument;
        this.authData = authData;
    }

    /**
     * @param user {@code User} that will own this {@code UserConfig}
     * @see User
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return {@code User} that owns this {@code UserConfig}
     * @see User
     */
    public User getUser() {
        return user;
    }

    /**
     * @return {@code UserConfig} stored username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return Authentication Cookies for this {@code UserConfig}
     */
    public Map<String, String> getAuthData() {
        return authData;
    }

    /**
     * @return PowerschoolClient of this {@code UserConfig}
     * @see PowerschoolClient
     */
    public PowerschoolClient getClient() {
        return client;
    }

    /**
     * @return {@code Document} that was used to generate this {@code UserConfig}
     * @see org.jsoup.nodes.Document
     */
    public Document getConstructionDocument() {
        return constructionDocument;
    }
}
