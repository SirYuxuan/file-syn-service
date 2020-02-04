# FileSynService

#### 介绍
文件同步服务
可以将本地的文件通过配置实时同步到FTP,SFTP等

##### 当前版本0.0.1
##### 仅能实现从Window同步至FTP或SFTP
##### 从Linux同步至FTP或SFTP
##### 使用场景
##### 1.两台服务器目录需要实时同步的
##### 2.开发环境的页面需要实时同步至测试环境
##### 3.文件备份...等更多应用等你发掘



#### 软件架构
#### 第三方类库说明
##### 1.Hutool  
##### 2.Jsch
##### 3.commons-io
##### 4.commons-net
##### 5.lombok
##### 6.slf4j
#### 安装教程
 
1.  下载项目
```shell script
git clone https://gitee.com/siryuxuan/FileSynService.git
```
2.  进入目录并编译
```shell script
cd FileSynService
mvn clean package
```
3.  运行程序
```shell script
java -jar fileSynService-jar-with-dependencies.jar -c D:/simple.setting
```
4. 后台执行
```shell script
Linux
nohup java -jar fileSynService-jar-with-dependencies.jar -c D:/simple.setting > cataline.log 2>&1 &
Window
start javaw -jar fileSynService-jar-with-dependencies.jar -c D:/simple.setting
```
#### 使用说明

##### 参考resource下的simple.setting文件 文件内有详细注释


#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request

