/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.tomitribe.totem.jbake;

import com.orientechnologies.orient.core.Orient;
import org.apache.commons.configuration.ConfigurationException;
import org.jbake.app.Oven;
import org.jbake.app.configuration.ConfigUtil;
import org.jbake.app.configuration.JBakeConfiguration;
import org.tomitribe.util.dir.Dir;
import org.tomitribe.util.dir.Name;

import java.io.File;

public interface JbakeProject extends Dir {

    default void bake() {
        final Orient orient = Orient.instance();
        try {
            orient.startup();
            final Oven oven = new Oven(loadConfiguration());
            oven.bake();
        } catch (final Exception e) {
            // TODO better exception handling
            e.printStackTrace();
        } finally {
            orient.shutdown();
        }
    }

    /**
     * The directory where all markdown and asciidoc files
     * are aggregated prior to rendering
     */
    Content content();

    /**
     * The directory where all final html files are placed
     * after generation.
     */
    Output output();

    @Name("jbake.properties")
    File jbakeProperties();

    default JBakeConfiguration loadConfiguration() throws ConfigurationException {
        final ConfigUtil configUtil = new ConfigUtil();
        return configUtil.loadConfig(dir());
    }

    static JbakeProject from(final String path) {
        return from(new File(path));
    }

    static JbakeProject from(final File file) {
        return Dir.of(JbakeProject.class, file);
    }

    interface Content extends Dir {
    }

    interface Output extends Dir {
    }
}
