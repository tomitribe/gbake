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

import org.junit.Test;
import org.tomitribe.util.IO;
import org.tomitribe.util.Join;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class JbakeTest {

    @Test
    public void testCreateAndCloneRepository() throws Exception {

        final GitRepository templatesRepository = GitRepository.init("templates")
                .commit(Archives.testResources("jbake-init"), "Jbake groovy templates")
                .commit(this::modifyJbakeProperties, "Set site location");


        final File directory = templatesRepository.getDirectory().getAbsoluteFile();

        final JbakeProject project = JbakeProject.from(directory);

        project.bake();


        final File actualOutput = project.output().dir();
        final File expectedOutput = TestResources.get("expected");

        final List<String> actualPaths = paths(actualOutput);
        final List<String> expectedPaths = paths(expectedOutput);
        
        assertEquals(Join.join("\n", expectedPaths), Join.join("\n", actualPaths));

        for (final String path : expectedPaths) {
            final String actual = normalize(new File(actualOutput, path));
            final String expected = normalize(new File(expectedOutput, path));
            assertEquals(path, expected, actual);
        }
    }

    public static String normalize(final File file) throws IOException {
        return IO.slurp(file)
                .replaceAll("(<[a-zA-Z]+Date>)[^<>]+</", "$1</")
                ;
    }

    private static List<String> paths(final File dir) throws IOException {
        final int i = dir.getAbsolutePath().length() + 1;
        return Files.list(dir.toPath())
                .map(Path::toFile)
                .filter(File::isFile)
                .map(File::getAbsolutePath)
                .map(s -> s.substring(i))
                .collect(Collectors.toList());
    }

    static String childPath(final File parent, final File file) {
        final String parentPath = parent.getAbsolutePath();
        final String childPath = file.getAbsolutePath();

        if (childPath.startsWith(parentPath)) {
            final int base = parentPath.length();
            return childPath.substring(base + 1);
        } else {
            return childPath;
        }
    }

    private void modifyJbakeProperties(final File file) {
        try {
            final JbakeProject project = JbakeProject.from(file);
            final File properties = project.jbakeProperties();
            final String content = IO.slurp(properties)
                    .replace("site.host=http://jbake.org", "site.host=http://example.org");

            IO.copy(IO.read(content), properties);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
