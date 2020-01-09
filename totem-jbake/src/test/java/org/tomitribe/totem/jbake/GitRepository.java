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

import lombok.Data;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;
import org.tomitribe.util.Archive;
import org.tomitribe.util.Files;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Creates a temporary git repository for easy testing.
 *
 * The primary job of this tool is to aggregate content from several git repositories
 * into one large jbake layout so one single site can be produced including everyone's work.
 *
 * To do any truly good testing we need actual git repositories to test.
 */
@Data
public class GitRepository {
    private final Git git;
    private final File directory;

    public GitRepository commit(final Consumer<File> changes, final String message) throws GitAPIException {
        changes.accept(directory);
        return commit(message);
    }

    public GitRepository commit(final Archive changes, final String message) throws IOException, GitAPIException {
        changes.toDir(directory);
        return commit(message);
    }

    public GitRepository commit(final String message) throws GitAPIException {
        { // add any updated files
            final Status status = git.status().call();
            final AddCommand add = git.add();

            Stream.concat(
                    status.getUntracked().stream(),
                    status.getUncommittedChanges().stream()
            ).forEach(add::addFilepattern);

            add.call();
        }

        { // commit the files
            final CommitCommand commitCmd = git.commit();
            commitCmd.setAll(true);
            commitCmd.setMessage(message);
            final PersonIdent author = new PersonIdent(
                    "Joe Cool",
                    "jcool@example.com",
                    System.currentTimeMillis() - TimeUnit.DAYS.toMillis(360),
                    0);
            commitCmd.setAuthor(author);
            commitCmd.call();
        }

        return this;
    }

    public static GitRepository init(final String name) throws GitAPIException {
        final File directory = new File(Files.tmpdir(), name);
        final Git git = Git.init().setDirectory(directory).call();
        return new GitRepository(git, directory);
    }
}
