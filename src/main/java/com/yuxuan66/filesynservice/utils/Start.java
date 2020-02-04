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
package com.yuxuan66.filesynservice.utils;

import cn.hutool.core.convert.Convert;
import cn.hutool.setting.Setting;
import com.yuxuan66.filesynservice.config.Config;
import com.yuxuan66.filesynservice.config.FTPConfig;
import com.yuxuan66.filesynservice.config.FolderConfig;
import com.yuxuan66.filesynservice.listener.SynFileListener;
import com.yuxuan66.filesynservice.listener.SynFileMonitor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Map;

@Slf4j
public class Start {

    public void run(String... args) {

        long startTime = System.currentTimeMillis();
        log.info("FileSynService Start...");

        //基础配置解析
        log.info("Config Load...");
        if (args.length != 2 || !"-c".equals(args[0])) {
            log.error("please SpecifyA Profile.");
            System.exit(0);
        }
        File settingFile = new File(args[1]);

        if(!settingFile.exists()){
            log.error("profileDoesNotExist please SpecifyA Profile.");
            System.exit(0);
        }
        Setting setting = new Setting(settingFile.getAbsolutePath());

        Config config = new Config();

        Map<String, String> ftpConfigMap = setting.getMap("ftp");
        ftpConfigMap.forEach((k, v) -> {
            if (k.contains("id")) {
                String tags = k.replace("id", "");
                FTPConfig ftpConfig = new FTPConfig();
                ftpConfig.setId(v);
                ftpConfig.setIp(ftpConfigMap.get("ip" + tags));
                ftpConfig.setName(ftpConfigMap.get("name" + tags));
                ftpConfig.setPassword(ftpConfigMap.get("password" + tags));
                ftpConfig.setFtp(Convert.toBool(ftpConfigMap.get("isFtp" + tags), true));
                ftpConfig.setPort(Convert.toInt(ftpConfigMap.get("port" + tags), 21));
                config.addFtpConfig(v, ftpConfig);
            }
        });
        log.info("FtpConfig Load Success Count {}", config.ftpConfigCount());

        Map<String, String> folderConfigMap = setting.getMap("folder");
        folderConfigMap.forEach((k, v) -> {
            if (k.contains("id")) {
                String tags = k.replace("id", "");
                FolderConfig folderConfig = new FolderConfig();
                folderConfig.setId(v);
                folderConfig.setFtpConfigId(folderConfigMap.get("ftpId" + tags));
                folderConfig.setFtpConfig(config.getFtpConfig(folderConfig.getFtpConfigId()));
                folderConfig.setFtpPath(folderConfigMap.get("ftpPath" + tags));
                folderConfig.setLocalPath(folderConfigMap.get("localPath" + tags));
                folderConfig.setStartSyn(Convert.toBool(folderConfigMap.get("startSyn" + tags), false));

                if (folderConfig.getFtpConfig() == null) {
                    log.warn("FolderConfig ID {} FTPConfig Is Null.", v);
                } else {
                    config.addFolderConfig(v, folderConfig);
                }

            }
        });
        log.info("FolderConfig Load Success Count {}", config.folderConfigCount());

        config.setInterval(Convert.toLong(setting.get("system", "interval"), 3000l));

        log.info("File listening interval {}ms", config.getInterval());


        if (config.folderConfigList().isEmpty()) {
            log.warn("No valid configuration terminates");
            System.exit(0);
        }

        SynFileMonitor synFileMonitor = new SynFileMonitor(config.getInterval());
        //启动文件夹监听
        config.folderConfigList().forEach(item -> {
            SynFileListener synFileListener = new SynFileListener(item);
            synFileMonitor.monitor(item.getLocalPath(), synFileListener);
        });


        try {
            synFileMonitor.start();
        } catch (Exception e) {
            log.error("File Listener Error", e);
        }
        log.info("FileSynService Start Success Total Time {}ms", System.currentTimeMillis() - startTime);

    }
}
