Traffic
===

v1.0
===

###实现的功能

##### 记录流量：

记录每天使用的流量，按天保存。
可以处理的情况：
1. 一天内多次开关机（已测试）
2. 连续几天不关己（已测试）
3. 流量wifi切换时只记录流量（已测试）
4. 记录每月使用的流量
    
注意事项：
1. 流量记录只从应用安装后开始记录
2. 流量记录只记录Traffic服务运行期间的流量

##### 流量记录处理频率

实现模式 普通（6秒记录一次），省点模式（12秒记录一次）可在
[SettingData.java](./app/src/main/java/com/hinsty/traffic/service/object/SettingData.java) 文件51、52行修改更改
其中打开主界面更新频率提高到1秒一次

##### 记录每个应用流量

**只记录流量部分**
（没有测试，90%可能存在bug）

##### 提醒

实现每天流量、每月流量和流量超过套餐时提醒，对应的提示现只会提醒一次
没有实现提醒时关闭流量（可使用的方法查找对应API或者提升APP root权限）
如需添加断网功能请修改[TrafficHelper.java](./app/src/main/java/com/hinsty/traffic/service/TrafficHelper.java) stopNetConnect 函数

##### 开机启动

已实现

##### 报表

实现月报表，日报表(未测试)

##### 折线图

能展示本月每日使用的流量（没有测试，很大可能存在BUG）

##### about界面

实现

##### 启动欢迎界面

已实现，每天只显示一次，可以在 [MainActivity.java](./app/src/main/java/com/hinsty/traffic/MainActivity.java)中修改

##### 主界面展示内容

展示今日流量，本月流量，套餐，套餐剩余日期，使用百分比

如果修改，需修改  [MainActivity.java](./app/src/main/java/com/hinsty/traffic/MainActivity.java) [TrafficService.java](./app/src/main/java/com/hinsty/traffic/service/TrafficService.java) **[AIDL文件](https://coding.net/u/av2015/p/Traffic/git/tree/master/app/src/main/aidl/com/hinsty/traffic)**(注意:数据保存在不同进程中，请使用跨进程通信)

##### 设置界面

提供对各项参数的修改，修改完即使生效(注意:数据保存在不同进程中，请使用跨进程通信)
