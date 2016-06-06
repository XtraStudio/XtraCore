/**
 * This file is part of XtraCore, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2016 - 2016 XtraStudio <https://github.com/XtraStudio>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.xtra.core.config;

import java.util.Set;

import com.xtra.core.util.ReflectionScanner;

public class ConfigHandler {

    private Set<Config> configs;

    private ConfigHandler() {
    }

    /**
     * Creates and initializes a {@link ConfigHandler}.
     * 
     * @return The new config handler
     */
    public static ConfigHandler create() {
        return new ConfigHandler().init();
    }

    private ConfigHandler init() {
        this.configs = ReflectionScanner.getConfigs();
        for (Config config : configs) {
            config.init();
            // TODO: is this what we want ?
            // config.populate();
        }
        return this;
    }

    /**
     * Gets the specified config object.
     * 
     * @param clazz The class of the config
     * @return The config object
     */
    public Config getConfig(Class<? extends Config> clazz) {
        for (Config config : configs) {
            if (clazz.isInstance(config)) {
                return config;
            }
        }
        return null;
    }
}