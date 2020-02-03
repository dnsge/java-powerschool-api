/*
 * MIT License
 *
 * Copyright (c) 2020 Daniel Sage
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.dnsge.powerschoolapi.client;

import org.dnsge.powerschoolapi.user.User;
import org.dnsge.powerschoolapi.user.UserConfig;

import java.util.HashMap;

/**
 * Cookie / User information storage object for {@code PowerschoolClient}
 *
 * @author Daniel Sage
 * @version 1.0
 * @see DefaultPowerschoolClient
 */
public final class ClientStorage {

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
