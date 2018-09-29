package org.dnsge.powerschoolapi;

import java.util.HashMap;

public class ClientStorage {
    final HashMap<String, UserConfig> storage;

    ClientStorage() {
        storage = new HashMap<>();
    }

    void register(UserConfig userConfig) {
        storage.put(userConfig.username, userConfig);
    }

    UserConfig get(String username) {
        return storage.getOrDefault(username, null);
    }

    UserConfig get(User user) {
        return storage.getOrDefault(user.username, null);
    }
}
