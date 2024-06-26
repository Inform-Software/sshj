/*
 * Copyright (C)2009 - SSHJ Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.schmizz.sshj.sftp;

import com.hierynomus.sshj.sftp.RemoteResourceSelector;
import net.schmizz.sshj.connection.channel.direct.SessionFactory;
import net.schmizz.sshj.xfer.LocalDestFile;
import net.schmizz.sshj.xfer.LocalSourceFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static com.hierynomus.sshj.sftp.RemoteResourceFilterConverter.selectorFrom;

public class StatefulSFTPClient
        extends SFTPClient {

    private String cwd;

    public StatefulSFTPClient(SFTPEngine engine)
            throws IOException {
        super(engine);
        this.cwd = getSFTPEngine().canonicalize(".");
        log.debug("Start dir = {}", cwd);
    }

    public StatefulSFTPClient(SessionFactory sessionFactory) throws IOException {
        super(sessionFactory);
        this.cwd = getSFTPEngine().canonicalize(".");
        log.debug("Start dir = {}", cwd);
    }

    private synchronized String cwdify(String path) {
        return engine.getPathHelper().adjustForParent(cwd, path);
    }

    public synchronized void cd(String dirname)
            throws IOException {
        final String targetCwd = cwdify(dirname);
        if (statExistence(targetCwd) == null) {
            throw new SFTPException(targetCwd + ": does not exist");
        }
        cwd = targetCwd;
        log.debug("CWD = {}", cwd);
    }

    public synchronized List<RemoteResourceInfo> ls()
            throws IOException {
        return ls(cwd, RemoteResourceSelector.ALL);
    }

    public synchronized List<RemoteResourceInfo> ls(RemoteResourceFilter filter)
            throws IOException {
        return ls(cwd, filter);
    }

    public synchronized String pwd()
            throws IOException {
        return super.canonicalize(cwd);
    }

    public List<RemoteResourceInfo> ls(String path)
            throws IOException {
        return ls(path, RemoteResourceSelector.ALL);
    }

    public List<RemoteResourceInfo> ls(String path, RemoteResourceFilter filter)
            throws IOException {
        return ls(path, selectorFrom(filter));
    }

    @Override
    public List<RemoteResourceInfo> ls(String path, RemoteResourceSelector selector)
            throws IOException {
        try (RemoteDirectory dir = getSFTPEngine().openDir(cwdify(path))) {
            return dir.scan(selector == null ? RemoteResourceSelector.ALL : selector);
        }
    }

    @Override
    public RemoteFile open(String filename, Set<OpenMode> mode, FileAttributes attrs)
            throws IOException {
        return super.open(cwdify(filename), mode, attrs);
    }

    @Override
    public RemoteFile open(String filename, Set<OpenMode> mode)
            throws IOException {
        return super.open(cwdify(filename), mode);
    }

    @Override
    public RemoteFile open(String filename)
            throws IOException {
        return super.open(cwdify(filename));
    }

    @Override
    public void mkdir(String dirname)
            throws IOException {
        super.mkdir(cwdify(dirname));
    }

    @Override
    public void mkdirs(String path)
            throws IOException {
        super.mkdirs(cwdify(path));
    }

    @Override
    public FileAttributes statExistence(String path)
            throws IOException {
        return super.statExistence(cwdify(path));
    }

    @Override
    public void rename(String oldpath, String newpath, Set<RenameFlags> renameFlags)
            throws IOException {
        super.rename(cwdify(oldpath), cwdify(newpath), renameFlags);
    }

    @Override
    public void rm(String filename)
            throws IOException {
        super.rm(cwdify(filename));
    }

    @Override
    public void rmdir(String dirname)
            throws IOException {
        super.rmdir(cwdify(dirname));
    }

    @Override
    public void symlink(String linkpath, String targetpath)
            throws IOException {
        super.symlink(cwdify(linkpath), cwdify(targetpath));
    }

    @Override
    public void setattr(String path, FileAttributes attrs)
            throws IOException {
        super.setattr(cwdify(path), attrs);
    }

    @Override
    public String readlink(String path)
            throws IOException {
        return super.readlink(cwdify(path));
    }

    @Override
    public FileAttributes stat(String path)
            throws IOException {
        return super.stat(cwdify(path));
    }

    @Override
    public FileAttributes lstat(String path)
            throws IOException {
        return super.lstat(cwdify(path));
    }

    @Override
    public void truncate(String path, long size)
            throws IOException {
        super.truncate(cwdify(path), size);
    }

    @Override
    public String canonicalize(String path)
            throws IOException {
        return super.canonicalize(cwdify(path));
    }

    @Override
    public void get(String source, String dest)
            throws IOException {
        super.get(cwdify(source), dest);
    }

    @Override
    public void get(String source, LocalDestFile dest)
            throws IOException {
        super.get(cwdify(source), dest);
    }

    @Override
    public void put(String source, String dest)
            throws IOException {
        super.put(source, cwdify(dest));
    }

    @Override
    public void put(LocalSourceFile source, String dest)
            throws IOException {
        super.put(source, cwdify(dest));
    }

}
