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

import lombok.Data;

@Data
public class FolderConfig {

    private String id;
    //文件夹对应的ftp配置
    private String ftpConfigId;

    private FTPConfig ftpConfig;
    private String localPath;
    private String ftpPath;
    //启动时是否进行同步
    private boolean startSyn;

}
