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

import org.tomitribe.totem.jbake.TestResources;
import org.tomitribe.util.Archive;

import java.io.File;

/**
 * Very simple utility to create an Archive from a specific directory inside src/test/resources/
 *
 * Think of an Archive like a Jar that lives in memory.  When you make an Archive from a specific
 * directory you end up with a jar-like collection of files that are all relative paths.
 *
 * The archive can be turned into an actual jar or simply copied into a directory.  If output to
 * a directory, it works like unzipping a jar and any files in that already exist in the directory
 * are simply overwritten.
 *
 * Unlike a Jar, which is a collection of bytes, an Archive is a collection of lambdas that produce
 * bytes.  This means they take very little memory as any files added are still sitting on disk and
 * are not actually read till the archive is turned into a jar or output to a directory.
 */
public class Archives {
    public static Archive testResources(final String name) {
        final File directory = TestResources.get(name);
        final Archive archive = new Archive();
        archive.addDir(directory);
        return archive;
    }

}
