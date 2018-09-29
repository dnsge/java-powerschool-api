package org.dnsge.powerschoolapi.client;

import org.dnsge.powerschoolapi.user.User;
import org.dnsge.powerschoolapi.user.UserConfig;

import java.util.HashMap;

public class ClientStorage {
    final HashMap<String, UserConfig> storage;

    ClientStorage() {
        storage = new HashMap<>();
    }

    void register(UserConfig userConfig) {
        storage.put(userConfig.getUsername(), userConfig);
    }

    UserConfig get(String username) {
        return storage.getOrDefault(username, null);
    }

    UserConfig get(User user) {
        return storage.getOrDefault(user.getUsername(), null);
    }
}
