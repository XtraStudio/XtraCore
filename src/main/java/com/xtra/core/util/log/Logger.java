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

package com.xtra.core.util.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import org.spongepowered.api.Sponge;

import com.laxamer.file.FileUtils;
import com.xtra.core.internal.Internals;

public class Logger {

    private File logFile;

    public Logger() {
        File directory = new File(Sponge.getGame().getSavesDirectory() + "/logs/xtra-core");
        this.logFile = new File(directory + "/" + Internals.pluginContainer.getId() + ".log");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileUtils.wipeFile(logFile);
    }

    public void log(String message) {
        this.log(Level.INFO, message);
    }

    public void log(Throwable cause) {
        this.log(Level.ERROR, cause);
    }

    public void log(Level level, String message) {
        FileUtils.writeToFile(logFile, message);
    }

    public void log(Level level, Throwable cause) {
        FileUtils.writeToFile(logFile, cause.toString());
        try {
            // Need a print writer for stack traces
            PrintWriter w = new PrintWriter(logFile);
            cause.printStackTrace(w);
            w.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public enum Level {
        DEBUG, INFO, WARNING, ERROR, CRITICAL;
    }
}