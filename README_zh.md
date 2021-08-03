**[English](https://github.com/welineio/WelineSDKAndroidDemo/blob/master/README.md) | 简体中文**<br>









**Weline Android SDK开发手册**














---


![图片](https://github.com/welineio/WelineSDKAndroidDemo/blob/master/img/welinelogo.png)


---

**密级：秘密**<br>
Weline.io

# **关于本手册**

## **版权声明**

Weline.io©2021版权所有，保留一切权力。本文件中出现的任何文字叙述、文档格式、插图、照片、方法、过程等内容，除另有特别注明，版权均属Weline.io所有，受到有关产权及版权法保护。未经Weline.io书面许可不得擅自拷贝、传播、复制、泄露或复写本文档的全部或部分内容。

## **信息更新**

本文档仅用于为NDA用户提供信息，并且随时可由Weline.io更改或撤回。

## **免责条款**

根据适用法律的许可范围，Weline.io按“原样”提供本文档而不承担任何形式的担保，包括（但不限于）任何隐含的适销性、特殊目的适用性或无侵害性。在任何情况下，Weline.io都不会对最终用户或任何第三方因根据说明文档使用SDK/后台接口造成的任何直接或间接损失或损坏负责，即使Weline.io明确得知这些损失或损坏，这些损坏包括（但不限于）利润损失、业务中断、信誉或数据丢失。

## **阅读对象**

本文的读者对象为和Weline.io签署了NDA（保密协议）的第三方厂商的研发人员。

## **密级**

本文的密级为秘密，禁止向非NDA公司或个人开放此文件。

## **修订记录**

|日期|修订版本|描述|
|:----|:----|:----|
|2017/11/01|1.0.0|初稿完成|
|2017/11/03|1.0.1|补充状态信息|
|2018/03/21|1.0.6|SDK整合成安装包|
|2019/03/25|2.1.6|重新修订|
|2019/04/04|2.1.7|新增根据当前连接状态执行监听操作的接口|
|2019/04/10|2.1.8|新增FAQ|
|2019/04/12|2.1.9|新增测试验证场景|
|2019/04/22|2.1.10|排版优化,FAQ补充了关于重连时发送通知的说明|
|2019/06/21|2.2.0|解决了华为网需等待的问题|
|2021/05/14|3.0.0|调整文档格式和描述|



## **1. 概要**

此文档描述了第三方厂商集成虚拟网时，在Android平台上所使用的Weline.io SDK，其目的是让第三方厂商的研发人员能快速将虚拟网功能嵌入到第三方应用程序中。

此文档适合公司内部和签署了NDA的第三方研发人员阅读。

## **2. 系统需求**

运行的目标系统要求Android 5.0以上，Android 4.4及以下的系统不提供技术支持。

## **3. 快速接入**
## **第一步：获取AppID**

请联系 Weline.io Inc. 申请SDK使用授权信息，申请成功后，我们将会提供访问平台的一组唯一标识，包括有AppID、PartnerId以及DeviceClassNum，并提供相对应SDK文（cmapi_ShieldAPI_vX_X_X_cmg.aar）和Demo项目。

## **第二步：配置应用**

## **配置环境**

### **导入Weline SDK**

1. 将 cmapi_ShieldAPI_vX_X_X_cmg.aar 包放在您的应用工程的 libs 目录下（图片中文件名仅做示例，请以实际 SDK 文件名为准）：

![图片](https://github.com/welineio/WelineSDKAndroidDemo/blob/master/img/zh-image%20(1).png)

2. 在主项目的 build.gradle 中，添加下面的内容，将 libs 目录作为依赖仓库：

![图片](https://github.com/welineio/WelineSDKAndroidDemo/blob/master/img/zh-image%20(2).png)

```plain
allprojects {
    repositories {
        // 添加下面的内容
        flatDir {
            dirs 'libs'
        }
        // ... jcenter() 等其他仓库
    }
}
```
3. 在您 App Module 的 build.gradle 中，添加下面的内容，将Weline SDK作为项目依赖：

![图片](https://github.com/welineio/WelineSDKAndroidDemo/blob/master/img/zh-image%20(3).png)

```plain
dependencies {
    // 添加下面的内容
    implementation files('libs/cmapi_ShieldAPI_v3.0.2701.aar')
    ....
}
```
最后，同步下配置文件Sync project with Gradle Files：
![图片](https://github.com/welineio/WelineSDKAndroidDemo/blob/master/img/zh-image%20(4).png)

至此，Weline SDK 开发资源导入完成。

### **注册AppId**

调用SDK中的API前，需要先在您的Application中向平台注册您的AppId，代码如下：

```plain
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //将app注册到平台
        CMAPI.getInstance().init(this, "你的 appId", "你的 partnerId", 你的 devicesClassNum);
        ....
    }
}
```
### **配置PAC域名参数［可选］**

如果您需要使用节点DNS解析特定域名，可以配置PAC参数，配置的域名可以是多个。

★ 配置规则：'?'表示一个任意字符，'*'表示零个或多个任意字符，如*.cmhk.com，表示以.cmhk.com结尾的域名使用节点DNS解析。

配置代码如下：

```plain
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //将app注册到平台
        CMAPI.getInstance().registerApp(this, "你的 appId", "你的 partnerId", 你的 devicesClassNum);
        //配置PAC域名过滤，如*.cmhk.com
        CMAPI.getInstance().setPacOptionEnable(new String[]{"xxx.xxx.xxx"});
        ....
    }
}
```
关于PAC的详细配置，具体可网上查阅相关资料。

### **配置登录认证服务器**

调用登录API前，请配置您的登录认证服务器地址，配置代码如下：

```plain
public class LoginActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //配置登录认证服务器地址，如 net.cmhk.com
        CMAPI.getInstance().setLoginAsHost("xxx.xxx.xxx");
    }
}
```
## **第三步：登录**

### **登录**

★ 注意：移动盾SDK暂未支持用户注册，所有能在系统进行登录的帐号信息，均由第三方厂商提供对接到Weline.io后台系统。

您可根据用户输入的用户名和密码，调用CMAPI.getInstance().login方法进行用户名密码登录。

```plain
public class LoginActivity extends Activity {
    ...
    public void login(View view){
        //用户点击了登录按钮
        if(view.getId() == R.id.btnLogin){
            //获取用户输入的手机号码
            String account = etAccount.getText().toString();
            //获取用户输入的密码
            String password = etPassword.getText().toString();
            //调用平台登录API
           CMAPI.getInstance().login(account, password, new ResultListener(){
                @Override
                public void onError(int errorCode) {
                    if(errorCode == Constants.DR_INVALID_PASS||errorCode == Constants.DR_INVALID_USER) {
                        Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_LONG).show();
                    }else if(errorCode == Constants.DR_VPN_TUNNEL_IS_OCCUPIED){
                        Toast.makeText(this, "VPN通道被占用", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
```
首次登录SDK须要请求VPNService的权限，调用登录接口后会弹出系统的授权窗口。
### **自动登录**

已登录的用户，为方便用户下次进入APP无需重复登录，可调用API

CMAPI.getInstance().loginByTicket方法进行自动登录：

```plain
CMAPI.getInstance().loginByTicket(lastLoginedAccount, new ResultListener() {
                    @Override
                    public void onError(int errorCode) {
                        if (errorCode == Constants.DR_VPN_TUNNEL_IS_OCCUPIED) {
                            Toast.makeText(this, "VPN通道被占用", Toast.LENGTH_LONG).show();
                        }
                   ...//其它错误码处理
                    }
                });
```
关于错误码errorCode定义详情请查阅 －【错误码】
★ 说明：用户一旦登录成功，移动盾底层系统将会保留当前用户登录信息，客户端可调用API自动登录。保存的登录信息有时效性，在一定时间后将会自动失效。

在调用登录接口前, 必须保证当前状态是未连接状态。

### **取消登录过程**

在login() 调用成功后，状态转换成CONNECTED之前，都可以调用cancelLogin 终止登录过程。

```plain
CMAPI.getInstance().cancelLogin()
```
## **第四步：监听登录状态**

★ 注意：调用登录接口为异步请求，判断是否登录成功，需根据监听器回调。ConnectionStatusListener回调onEstablished()方法，表示登录成功。

### **添加监听器**

我们必须通过添加登录监听器，才能监听登录状态的回调，并根据不同状态进行事件处理或页面更新。

创建监听器代码如下：

```plain
public class LoginActivity extends Activity {
    //创建登录状态监听器
    private ConnectStatusListenerPlus listener = new ConnectStatusListenerPlus() {
        @Override
        public void onConnecting() {//正在登录
            //显示加载进度条
            showLoadingProgress("正在登录...");
        }
        ...//其它状态
        @Override
        public void onEstablished() {//登录成功
            //关闭加载进度条
            dissmissLoadingProgress();
            //如登录页面，跳转到主页
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    };
    .....
```
关于登录回调方法详细请查阅 －【登录回调流程】
Activity中进行监听（如LoginActivity），推荐在onStart方法中添加监听器：

```plain
@Override
protected void onStart() {
    super.onStart();
    //添加监听器
    CMAPI.getInstance().addConnectionStatusListener(listener);
}
```
并在onStop方法中移除监听器：

```plain
@Override
protected void onStop() {
    super.onStop();
    //移除监听器
    CMAPI.getInstance().removeConnectionStatusListener(listener);
}
```
当然，也可以在其他地方进行监听，如Fragment或Application中，可添加到任何您需要监听的地方。并在不需要监听时（如生命周期完成），移除监听器。
★ 注意：登录状态会因网络情况随时发生变化，如网络断开时，已登录的情况下，连接会断开，系统会自动尝试重连，并回调onConnecting()方法，直到网络再次恢复，连接成功回调onEstablished()方法。

### **登录回调流程**

![图片](https://github.com/welineio/WelineSDKAndroidDemo/blob/master/img/zh-image%20(6).png)

### **登录状态值**

★ 说明：可通过添加监听器，实时监听登录状态，也可通过以下代码获取当前登录连接的最新状态：

```plain
int status = CMAPI.getInstance().getRealtimeInfo().getCurrentStatus();
```
注意：CMAPI.getInstance().getRealtimeInfo()可能为null。
关于各状态值定义如下：

|状态名(Constants中的常量)|状态值status|说明|
|:----|:----|:----|
|CS_UNKNOWN|0|未初始化|
|CS_PREPARE|1|准备就绪|
|CS_CONNECTING|2|正在连接|
|CS_CONNECTED|3|已连接成功|
|CS_DISCONNECTED|4|已断开|
|CS_AUTHENTICATED|5|已认证成功|
|CS_DISCONNECTING|8|正在断开|
|CS_ESTABLISHED|200|虚拟网连接成功，登录成功|

## **第五步：退出登录**

调用退出登录API后，退出登录的用户，将不再保存登录Ticket，无法自动登录。

```plain
public class SettingActivity extends Activity {
    @Override
    protected void onStart() {
        super.onStart();
        //添加登录状态监听器
        CMAPI.getInstance().addConnectionStatusListener(statusListener);
        ...
    }
    public void loginOut(View view){//退出登录功能
        //用户点击了退出登录按钮
        if(view.getId() == R.id.btnLoginOut){
            //调用退出登录API
            CMAPI.getInstance().disconnect();
            //移除帐号信息
            CMAPI.getInstance().removeUser(CMAPI.getInstance().getBaseInfo().getUserId());
            if(networkNotAvailable){
              //无网络情况下(无法连接外网)，会自动断开，无需等待回调
              //不再重连
              CMAPI.getInstance().cancelLogin();
                //退出登录成功，跳转到登录页面
              startActivity(new
              Intent(SettingActivity.this,LoginActivity.class));
              finish()
            }
        }
    }
    protected ConnectStatusListenerPlus statusListener = new ConnectStatusListenerPlus() {
        @Override
        public void onDisconnected(int reason) {//已断开连接
            //退出登录成功，跳转到登录页面
            startActivity(new Intent(SettingActivity.this,LoginActivity.class));
            finish();
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        //移除状态监听器
        CMAPI.getInstance().removeConnectionStatusListener(statusListener);
    }
    ...
```
## **其它事件监听**

如果您需要监听VPN隧道占用情况、实时监听在线时长或时延等信息，可通过CMAPI.getInstance().subscribe方法进行监听，

关于其它事件监听详细请查阅【相关API - EventObserver】。

```plain
CMAPI.getInstance().subscribe(observer);
private EventObserver observer = new EventObserver() {
    @Override
    public void onRealTimeInfoChanged(RealtimeInfo info) {
        //实时信息变更, 该方法订阅后每秒回调一次,用于刷新在线时长、时延等实时信息
    }
    @Override
    public void onTunnelRevoke(boolean isRevoked) {
        // isRevoked: VPN隧道是否被占用，仅在其值改变时回调该方法
    }
};
```
移除事件监听：
```plain
CMAPI.getInstance().unsubscribe(observer);
```
## **4. 错误码**

登录回调错误码errorCode以及断开连接onDisconnected中reason的错误码定义（共用）：

import net.sdvn.cmapi.global.Constants;

|状态名(Constants中的常量)|状态值status|说明|
|:----|:----|:----|
|CS_UNKNOWN|0|未知|
|DR_BY_USER|1|用户主动断开|
|DR_MISVERSION<br>|2|版本太低(被限制登录)|
|DR_NETWORK|3|与后台服务器的网络连接中断(如后台升级)|
|DR_MISSING_INFO|4|未配置过任何账号信息或者设备账号信息丢失|
|DR_INVALID_USER|5|无效的用户名|
|DR_INVALID_PASS|6|无效的密码，或者口令|
|DR_DEVICE_DISABLED|9|设备被限制登入|
|DR_MAX_DEVICE|10|设备数量超限|
|DR_NO_NETWORK|11|无可用的虚拟网络（一般不会发生）|
|DR_TUN_DEVICE|15|系统无可用tun设备，或者tun0设备其他程序占用|
|DR_AUX_AUTH_DISMATCH|20|认证模式不匹配(验证码错误或认证模式错误)|
|DR_INVALID_AUTHORIZATION|21|认证无效|
|DR_CALL_THIRD_API_FAIL|29|与第三方接口对接失败|
|CE_APP_RUNTIME_ERROR|2017|app运行错误|
|DR_VPN_PERMISSION_DENIED|2018|VPN权限被拒绝，请设置允许该应用使用VPN权限|
|DR_CONNECT_TIMEOUT|2019|连接超时|
|DR_VPN_TUNNEL_IS_OCCUPIED|2020|VPN通道被占用，请关闭其他VPN|

## **5. 相关API**
## **CMAPI**

|方法|描述|
|:----|:----|
|BaseInfogetBaseInfo()|获取基本信息 包含登录的用户基本信息、用于校验的票据。|
|RealtimeInfo getRealtimeInfo()|获取实时信息 包含当前连接的实时信息,如:在线时长、连接时延、连接状态等。|
|boolean removeUser(String id)|移除保存的账号。|
|void addConnectionStatusListener(ConnectStatusListenerPlus<br>statusListener)|订阅状态事件。|
|void removeConnectionStatusListener(ConnectStatusListenerPlus statusListener)|取消状态事件的订阅。|
|void subscribe(EventObserver)|订阅其他事件。|
|void unsubscribe (EventObserver)|取消其他事件订阅。|
|List<Device> getDevices ()|获取当前网络中的设备列表。|
|List<Network> getNetworkList()|获取用户的网络列表|

## **BaseInfo**

|方法|描述|
|:----|:----|
|String getVersion()|包含当前基本信息|
|String getAccount()|当前缓存的账号(最近一次登录过的账号)|
|List<String> getUserList()|登录成功并保存的账号集合|
|String getDomain()|域名|
|String getVip()|虚拟IP|
|String getVmask()|虚拟掩码|
|String getTicket()|登录认证的票据|
|String getSnid()|当前SmartNode的ID|
|boolean getDlt()|DLT是否可用|

## **RealtimeInfo**

|方法|描述|
|:----|:----|
|int getCurrentStatus()|当前状态的标识|
|int getNetLatency()|网络时延|
|long getOnlineTime()|在线时长|

## **Device**

|方法|描述|
|:----|:----|
|String getID()|设备ID|
|String getName()|设备名|
|String getOwner()|设备所有者|
|String getUserId()|设备所有者的用户 ID|
|String getDomain()|设备域名|
|String getVip()|设备虚拟 IP|
|String getPriIp()|设备所在局域网分配的 IP|

## **Network**

|方法|描述|
|:----|:----|
|String getID()|网络ID|
|String getName()|网络名|
|String getOwner()|网络所有者|
|String getUId()|网络所有者 ID|
|booleanisCurrent()|是否为当前网络|

## **ConnectStatusListenerPlus**

|方法|描述|
|:----|:----|
|void onConecting()|开始连接时回调。<br>也可视作连接成功后（由于网络等因素造成的）的自动重连。一般而言，登录成功后除非调用退出登录的接口，SDK不会主动断开并停止连接，异常断连后会尝试自动重连，并回调此方法，此时可自行判断是否终止重连(调用cancelLogin())。|
|void onAuthenticated()|认证完成后回调，表示账号完成了认证。|
|void onConnected()|登录成功后回调，表示数据加载完成，已具备连接虚拟网的资质。|
|void onEstablished()|成功连接虚拟网后回调，此时可正常访问虚拟网。|
|void onDisconnecting()|连接正在断开时回调|
|void onDisconnected(int reason)|连接断开后回调，也对应首次登录前的状态，在登录失败后同样会回调此方法。一般而言，登录成功后除非调用退出登录的接口，SDK不会主动断开并停止连接，异常断连后会尝试自动重连。<br>reason： 断开原因标识，详细请查看【错误码】。|

## **EventObserver**


|方法|描述|
|:----|:----|
|void onRealTimeInfoChanged(RealtimeInfo info)|实时信息变更, 该方法订阅后每秒回调一次，可根据需要实时更新UI|
|void onTunnelRevoke (boolean isRevoked)|当VPN隧道在占用和空闲之间变化时回调。<br>isRevoked为true：<br>1.当检测出VPN隧道被占用时回调;<br>2.隧道被抢占时直接回调;<br>3.登录前检测是否有其他使用VPN的应用，有则回调。<br><br>isRevoked为false：<br>在VPN隧道被占用后，每隔一定时间sdk会自动检测一次当前是否有其他应用正在使用VPN隧道，没有则回调，每次占用仅回调一次(在该回调返回true后的5秒内不会返回false，保护期)。<br>|

6. **FAQ**
* **登录不上，提示2018­——VPN权限被拒绝**

1.请注意，Weline.io服务是依托于VPN构建的，要使用Weline.io服务请先为应用授权VPN。首次调用登录时会弹出系统的权限询问窗口，如若拒绝，则需要进入系统设置界面进行设置。

2.VPN通道被其他应用占用，请关闭其他VPN应用后再尝试登录。如果已经登录后被其他VPN应用抢占通道，在关闭其他VPN应用后，SDK会自动重连。

* **关于网络切换和掉线重连**

SDK保持的VPN连接状态会在断连时自动重连，常见的异常断连情况如下：

1.  网络波动

2.  切换了当前使用的网络，如更换WIFI，4G、WIFI之间的相互切换

3.  VPN隧道被占用（为避免不停争抢隧道，将会在隧道不被占用时重连）

在自动重连时，状态事件监听将会回调onConnecting(),可在回调中写入等待动画的逻辑

* **后台运行时连接中断**

部分安卓系统（EMUI）默认不支持长时间后台运行，需手动设置自启动权限。此外，还有部分安卓系统（MIUI）后台运行正常，但在连接中断后无法自动重连，需要手动设置省电策略不再限制本应用。

部分系统可以使用如下方式打开当前应用设置进行自启动权限、省电策略的修改，建议使用弹窗提示的方式引导用户点击跳转:

```plain
Intent intent = new Intent();
intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
intent.setData(uri);
startActivity(intent);
```


