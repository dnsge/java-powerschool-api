package org.dnsge.powerschoolapi.client;

import org.dnsge.powerschoolapi.user.User;
import org.dnsge.powerschoolapi.user.UserConfig;

import java.util.HashMap;

/**
 * Cookie / User information storage object for {@code PowerschoolClient}
 *
 * @author Daniel Sage
 * @see PowerschoolClient
 */
public class ClientStorage {
    private final HashMap<String, UserConfig> storage;

    ClientStorage() {
        storage = new HashMap<>();
    }

    /**
     * Stores a UserConfig
     *
     * @param userConfig UserConfig to store
     * @see UserConfig
     */
    void register(UserConfig userConfig) {
        storage.put(userConfig.getUsername(), userConfig);
    }

    /**
     * Gets the UserConfig of a user with a certain username
     *
     * @param username Username to get
     * @return UserConfig of user
     * @see UserConfig
     */
    UserConfig get(String username) {
        return storage.getOrDefault(username, null);
    }

    /**
     * Gets the UserConfig of a certain {@code User}
     *
     * @param user {@code User} object to get config for
     * @return UserConfig of User
     * @see UserConfig
     * @see User
     */
    UserConfig get(User user) {
        return storage.getOrDefault(user.getUsername(), null);
    }
}
