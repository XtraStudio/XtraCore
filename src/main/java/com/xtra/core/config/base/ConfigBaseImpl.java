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

package com.xtra.core.config.base;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.xtra.api.config.annotation.RegisterConfig;
import com.xtra.api.config.base.ConfigBase;
import com.xtra.api.plugin.XtraCorePluginContainer;
import com.xtra.api.util.config.ConfigExecutor;
import com.xtra.core.CoreImpl;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

/**
 * A base class for a config implementation.
 */
public class ConfigBaseImpl implements ConfigExecutor {

    private XtraCorePluginContainer entry;
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private CommentedConfigurationNode rootNode;
    private ConfigBase base;

    @Override
    public void init(ConfigBase base) {
        this.base = base;
        this.entry = CoreImpl.instance.getConfigRegistry().getEntry(base.getClass()).get().getValue();
        RegisterConfig rc = this.base.getClass().getAnnotation(RegisterConfig.class);

        this.entry.getLogger().log("Initializing configuration for '" + rc.configName() + ".conf'.");

        HoconConfigurationLoader.Builder loaderBuilder = HoconConfigurationLoader.builder();
        Path dir;
        // The file is created automatically, however we need to know if we need
        // to populate it or not
        boolean exists;
        if (rc.sharedRoot()) {
            dir = Paths.get(System.getProperty("user.dir"), "/config/");
            this.checkExists(dir);
        } else {
            dir = Paths.get(System.getProperty("user.dir"), "/config/" + this.entry.getPluginContainer().getId());
            this.checkExists(dir);
        }
        Path configPath = dir.resolve(rc.configName() + ".conf");
        exists = Files.exists(configPath);
        loaderBuilder.setPath(configPath);
        this.loader = loaderBuilder.build();
        if (!exists) {
            this.entry.getLogger().log("Configuration currently does not exist. Creating...");
            this.rootNode = loader.createEmptyNode();
            this.base.populate();
            this.save();
        }
        this.load();
    }

    private void checkExists(Path dir) {
        if (!Files.exists(dir)) {
            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void load() {
        try {
            rootNode = loader.load();
        } catch (IOException e) {
            this.entry.getLogger().log(e);
        }
    }

    @Override
    public void save() {
        try {
            loader.save(rootNode);
        } catch (IOException e) {
            this.entry.getLogger().log(e);
        }
    }

    @Override
    public ConfigurationLoader<CommentedConfigurationNode> loader() {
        return this.loader;
    }

    @Override
    public CommentedConfigurationNode rootNode() {
        return this.rootNode;
    }
}