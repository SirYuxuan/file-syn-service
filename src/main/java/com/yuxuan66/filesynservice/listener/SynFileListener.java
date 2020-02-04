/*
 * Copyright (C) 2020 projectName:FileSynService,author:yuxuan
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.yuxuan66.filesynservice.listener;

import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.ftp.AbstractFtp;
import cn.hutool.extra.ftp.Ftp;
import cn.hutool.extra.ssh.Sftp;
import com.yuxuan66.filesynservice.config.FTPConfig;
import com.yuxuan66.filesynservice.config.FolderConfig;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;

@Slf4j
@Data
public class SynFileListener implements FileAlterationListener {




    private FolderConfig folderConfig;

    private AbstractFtp ftp;
    private String localPath;
    private String ftpPath;
    //时间范围内多次触发执行最后一次
    private long time = 500;



    public SynFileListener(FolderConfig folderConfig) {
        this.folderConfig = folderConfig;
        FTPConfig ftpConfig = folderConfig.getFtpConfig();
        try {
            if (ftpConfig.isFtp()) {
                ftp = new Ftp(ftpConfig.getIp(), ftpConfig.getPort(), ftpConfig.getName(), ftpConfig.getPassword());
            } else {
                ftp = new Sftp(ftpConfig.getIp(), ftpConfig.getPort(), ftpConfig.getName(), ftpConfig.getPassword());
            }
            ftp.cd(folderConfig.getFtpPath());
            log.info("FolderID {} Start Listener", folderConfig.getId());

            localPath = folderConfig.getLocalPath().replaceAll("\\\\", "/");
            if (!localPath.endsWith("/"))
                localPath += "/";

            ftpPath = folderConfig.getFtpPath().replaceAll("\\\\", "/");
            if (!ftpPath.endsWith("/"))
                ftpPath += "/";

            if(folderConfig.isStartSyn()){
                log.info("FolderID {} Start Syn.",folderConfig.getId());
                ftp.delDir(ftpPath);
                ftp.mkdir(ftpPath);
                FileUtil.loopFiles(localPath).forEach(file->{
                    if(file.exists()){
                        if(file.isDirectory()){
                            onDirectoryCreate(file);
                        }else if(file.isFile()){
                            onFileCreate(file);
                        }
                    }
                });
            }

        } catch (RuntimeException e) {
            log.error("FolderID {} {} Connect Error.", folderConfig.getId(), ftpConfig.isFtp() ? "FTP" : "SFTP", e);
        }


    }

    @Override
    public void onStart(FileAlterationObserver fileAlterationObserver) {

    }

    @Override
    public void onDirectoryCreate(File file) {
        if (ftp == null) return;
        log.info("Folder Create Start Syn. FolderName {}", file.getName());
        String folderName = file.getAbsolutePath().replaceAll("\\\\", "/");
        ftp.mkDirs(ftpPath + folderName.replaceAll(localPath, ""));
        log.info("FolderName {} Syn Success.", file.getName());
    }

    @Override
    public void onDirectoryChange(File file) {
    }

    @Override
    public void onDirectoryDelete(File file) {
        if (ftp == null) return;
        log.info("Folder Delete Start Syn. FolderName {}", file.getName());
        String folderName = file.getAbsolutePath().replaceAll("\\\\", "/");
        ftp.delDir(ftpPath + folderName.replaceAll(localPath, ""));
        log.info("FolderName {} Syn Success.", file.getName());
    }

    @Override
    public void onFileCreate(File file) {
        if (ftp == null) return;
        log.info("File Create Start Syn. FileName {}", file.getName());
        String folderName = file.getAbsolutePath().replaceAll("\\\\", "/").replaceAll(localPath, "");
        ftp.upload(ftpPath +  folderName.replaceAll(file.getName(), ""), file);
        log.info("FileName {} Syn Success.", file.getName());

    }

    @Override
    public void onFileChange(File file) {
        if (ftp == null) return;
        log.info("File Change Start Syn. FileName {}", file.getName());
        String folderName = file.getAbsolutePath().replaceAll("\\\\", "/").replaceAll(localPath, "");
        ftp.upload(ftpPath + "/" + folderName.replaceAll(file.getName(), ""), file);
        log.info("FileName {} Syn Success.", file.getName());
    }

    @Override
    public void onFileDelete(File file) {
        if (ftp == null) return;
        log.info("File Delete Start Syn. FileName {}", file.getName());
        String folderName = file.getAbsolutePath().replaceAll("\\\\", "/").replaceAll(localPath, "");
        if (ftp.exist(ftpPath + "/" + folderName)) {
            ftp.delFile(ftpPath + "/" + folderName);
            log.info("FileName {} Syn Success.", file.getName());
        } else {
            log.warn("Server Not Fount FileName {}", file.getName());
        }
    }

    @Override
    public void onStop(FileAlterationObserver fileAlterationObserver) {

    }
}
