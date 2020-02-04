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
package com.yuxuan66.filesynservice.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 每个程序保留唯一实例
 */
public class Config {

    /**
     * 间隔时间
     */
    private long interval;

    /**
     * ftp配置
     */
    private Map<String, FTPConfig> ftpConfigMap = new HashMap<String, FTPConfig>();

    /**
     * 文件夹配置
     */
    private Map<String, FolderConfig> folderConfigMap = new HashMap<String, FolderConfig>();


    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public int ftpConfigCount() {
        return ftpConfigMap.size();
    }


    public int folderConfigCount() {
        return folderConfigMap.size();
    }

    public void addFtpConfig(String id, FTPConfig ftpConfig) {
        ftpConfigMap.put(id, ftpConfig);
    }

    public void addFolderConfig(String id, FolderConfig folderConfig) {
        folderConfigMap.put(id, folderConfig);
    }

    public List<FolderConfig> folderConfigList() {
        return new ArrayList<>(folderConfigMap.values());
    }

    public FTPConfig getFtpConfig(String id) {
        return ftpConfigMap.get(id);
    }


}
