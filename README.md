**English | [简体中文](https://github.com/welineio/WelineSDKAndroidDemo/blob/master/README_zh.md)**<br>










**Weline Android SDK Access Guides**














---


![图片](https://github.com/welineio/WelineSDKAndroidDemo/blob/master/img/welinelogo.png)


---
**Secret Level**：**Secret**

Weline.io

# **About Guides**

## **Copyright Notice**

Weline.io © 2021 All rights reserved. Unless otherwise specified, the copyright of any text description, document format, illustration, photo, method, process, etc. appearing in this document belongs to Weline.io and is protected by relevant property rights and copyright laws. Without the written permission of Weline.io, it is not allowed to copy, distribute, reproduce, disclose or rewrite all or part of this document.

## **Information Update**

This document is only used to provide information for NDA users, and it can be changed or withdrawn by Weline.io at any time.

## **Disclaimer**

In accordance with the scope of the applicable law, Weline.io provides this document "as is" and does not assume any form of guarantee, including (but not limited to) any implied marketability, suitability for special purposes, or non-infringement. Under any circumstances, Weline.io will not be liable for any direct or indirect loss or damage caused by the end user or any third party due to the use of the SDK/backend interface according to the instructions, even if Weline.io is clearly aware of these losses or damages, These damages include (but are not limited to) loss of profits, business interruption, reputation or loss of data.

## **Who should read this Guides**

The readers of this guides are developers of third-party vendors who have signed an NDA (non-disclosure agreement) with Weline.io.

## **Secret Level**

The confidentiality of this guides is secret, and it is forbidden to open this file to non-NDA companies or individuals.

## **History Record**
|Date|Version|Description|
|:----|:----|:----|
|2017/11/01|1.0.0|First draft completed|
|2017/11/03|1.0.1|Supplementary status information|
|2018/03/21|1.0.6|SDK integrated into installation package|
|2019/03/25|2.1.6|Revised|
|2019/04/04|2.1.7|Added an interface to perform monitoring operations based on the current connection status|
|2019/04/10|2.1.8|Add FAQ|
|2019/04/12|2.1.9|Added test and verification scenarios|
|2019/04/22|2.1.10|The layout is optimized, and the FAQ has added instructions on sending notifications when reconnecting|
|2019/06/21|2.2.0|Solved the problem of waiting for Huawei Network|
|2021/05/14|3.0.0|Revised|

## **1. Summary**

This document describes the Weline.io SDK used on the Android platform when third-party vendors integrate virtual networks. Its purpose is to allow third-party developers’ developers to quickly embed virtual network functions into third-party applications.

This document is suitable for the company's internal and third-party R&D personnel who have signed the NDA to read.

## **2. System Requirements**

The target system to run requires Android 5.0 or higher, and Android 4.4 and lower systems do not provide technical support.

## **3. Quick Access**
## **Step 1: Get AppID**

Please contact Weline.io Inc. to apply for SDK authorization information. After the application is successful, we will provide a unique identifier for accessing the platform, including AppID, PartnerId and DeviceClassNum, and provide the corresponding SDK text (cmapi_ShieldAPI_vX_X_X_cmg.aar) and Demo project.

## **Step 2: Configure the application**

## **Configuration environment**

### **Import Weline SDK**

1. Put the cmapi_ShieldAPI_vX_X_X_cmg.aar package in the libs directory of your application project (the file name in the picture is only an example, please refer to the actual SDK file name):

![图片](https://github.com/welineio/WelineSDKAndroidDemo/blob/master/img/image%20(1).png)

2. In the build.gradle of the main project, add the following content and use the libs directory as a dependency repository:

![图片](https://github.com/welineio/WelineSDKAndroidDemo/blob/master/img/image%20(2).png)

```plain
allprojects {
    repositories {
        // Add the following content
        flatDir {
            dirs 'libs'
        }
        // ... jcenter() 
    }
}
```
3. In the build.gradle of your App Module, add the following content to make the Weline SDK a project dependency:

![图片](https://github.com/welineio/WelineSDKAndroidDemo/blob/master/img/image%20(3).png)

```plain
dependencies {
    // Add the following content
    implementation files('libs/cmapi_ShieldAPI_v3.0.2701.aar')
    ....
}
```
Finally, synchronize the configuration file Sync project with Gradle Files:
![图片](https://github.com/welineio/WelineSDKAndroidDemo/blob/master/img/image%20(4).jpeg)

So far, the import of We line SDK development resources is complete.

### **Configure FileProvider**

★ Note: Because the We line SDK needs to read and write files, if you want to be compatible with Android N or above devices, you must configure FileProvider in the AndroidManifest.xml file to access files in the shared path.

Create an xml resource folder under your resource file, and create a filepaths.xml file:

![图片](https://github.com/welineio/WelineSDKAndroidDemo/blob/master/img/image%20(5).png)

Specify the file path allowed by FileProvider:

```plain
<?xml version="1.0" encoding="utf-8"?>
<resource xmlns:android="http://schemas.android.com/apk/res/android">
    <paths>
        <!--The following is just a sample, you can customize the name-->
        <external-path
            name="demo"
            path="forenet/demo_log/" />
        <external-path
            path=""
            name="download"/>
    </paths>
</resource>
```
Register in the manifest file AndroidManifest.xml:
```plain
<application
   ....
  >
    <!--Add the following content-->
    <provider
        android:name="androidx.core.content.FileProvider"
        android:authorities="${applicationId}.fileprovider"
        android:exported="false"
        android:grantUriPermissions="true">
        <meta-data
            android:name="android.support.FILE_PROVIDER_PATHS"
            android:resource="@xml/filepaths" />
    </provider>
 ...
</application>
```
### **Adapt to Android Q read and write permission issues**

★ Note: If the targetSdkVersion configured by your application is 29 and above (this configuration can be ignored if it is lower than 29), after dynamically obtaining the user's read and write permissions for memory, when the android Q mobile phone accesses the external storage of the non-private directory, it will still be The system denies access. To access Weline SDK normally, you need to configure the following content.

Configure android:requestLegacyExternalStorage to true in the application of AndroidManifest.xml:

```plain
<application
    ...
    android:requestLegacyExternalStorage="true"
>
...
</application>
```
### **Register AppId**

Before calling the API in the SDK, you need to register your AppId with the platform in your Application. The code is as follows:

```plain
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //Register the app to the platform
        CMAPI.getInstance().init(this, "Your appId", "Your partnerId", Your devicesClassNum);
        ....
    }
}
```
### **Configure PAC domain name parameters [optional]**

If you need to use the node DNS to resolve a specific domain name, you can configure the PAC parameters, and the configured domain name can be multiple.

★ Configuration rules:'?' means an arbitrary character,'*' means zero or more arbitrary characters, such as *.cmhk.com, means that the domain name ending with .cmhk.com is resolved by node DNS.

The configuration code is as follows:

```plain
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //Register the app to the platform
        CMAPI.getInstance().registerApp(this, "Your appId", "Your partnerId", Your devicesClassNum);
        //Configure PAC domain name filtering, such as *.cmhk.com
        CMAPI.getInstance().setPacOptionEnable(new String[]{"xxx.xxx.xxx"});
        ....
    }
}
```
Regarding the detailed configuration of PAC, you can check relevant information online.

### **Configure login authentication server**

Before calling the login API, please configure your login authentication server address, the configuration code is as follows:

```plain
public class LoginActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Configure the login authentication server address, such as net.cmhk.com
        CMAPI.getInstance().setLoginAsHost("xxx.xxx.xxx");
    }
}
```
## **Step 3: Log in**

### **Log in**

★ Note: The Mobile Shield SDK does not currently support user registration. All account information that can be logged in to the system is provided by a third-party manufacturer to connect to the Weline.io back-end system.

You can call the CMAPI.getInstance().login() method to log in with the user name and password according to the user name and password entered by the user.

```plain
public class LoginActivity extends Activity {
    ...
    public void login(View view){
        //The user clicked the login button
        if(view.getId() == R.id.btnLogin){
            //Get the mobile phone number entered by the user
            String account = etAccount.getText().toString();
            //Get the password entered by the user
            String password = etPassword.getText().toString();
            //Call platform login API
           CMAPI.getInstance().login(account, password, new ResultListener(){
                @Override
                public void onError(int errorCode) {
                    if(errorCode == Constants.DR_INVALID_PASS||errorCode == Constants.DR_INVALID_USER) {
                        Toast.makeText(this, "wrong user name or password", Toast.LENGTH_LONG).show();
                    }else if(errorCode == Constants.DR_VPN_TUNNEL_IS_OCCUPIED){
                        Toast.makeText(this, "VPN tunnel is occupied", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
```
To log in to the SDK for the first time, you need to request the permission of VPNService. After calling the login interface, the system authorization window will pop up.
### **Automatic log-in**

The logged-in user, in order to facilitate the user to enter the APP next time without having to log in repeatedly, the API can be called

CMAPI.getInstance().loginByTicket method for automatic login:

```plain
CMAPI.getInstance().loginByTicket(lastLoginedAccount, new ResultListener() {
                    @Override
                    public void onError(int errorCode) {
                        if (errorCode == Constants.DR_VPN_TUNNEL_IS_OCCUPIED) {
                            Toast.makeText(this, "VPN tunnel is occupied", Toast.LENGTH_LONG).show();
                        }
                   ...//Other error code handling
                    }
                });
```
For the definition of errorCode, please refer to －【Error Code】
★ Note: Once the user logs in successfully, the mobile shield underlying system will retain the current user login information, and the client can call the API to automatically log in. The saved login information is time-sensitive and will automatically become invalid after a certain period of time.

Before calling the login interface, you must ensure that the current state is disconnected.

### **Cancel the login process**

After the login() call is successful, before the status changes to CONNECTED, cancelLogin can be called to terminate the login process.

CMAPI.getInstance().cancelLogin()

## **Step 4: Monitor the login status**

★ Note: Calling the login interface is an asynchronous request. To determine whether the login is successful, you need to call back based on the listener. ConnectionStatusListener calls back the onEstablished() method, indicating that the login is successful.

### **Add listener**

We must add a login listener to monitor the callback of the login state, and perform event processing or page update according to different states.

Create the listener code as follows:

```plain
public class LoginActivity extends Activity {
    //Create a login status listener
    private ConnectStatusListenerPlus listener = new ConnectStatusListenerPlus() {
        @Override
        public void onConnecting() {//logging in
            //Show loading progress bar
            showLoadingProgress("logging in...");
        }
        ...//Other status
        @Override
        public void onEstablished() {//login successful
            //Close loading progress bar
            dissmissLoadingProgress();
            //Such as login page, jump to the homepage
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    };
    .....
```
For details about the login callback method, please refer to －【Login callback process】
For monitoring in Activity (such as LoginActivity), it is recommended to add a listener in the onStart method:

```plain
@Override
protected void onStart() {
    super.onStart();
    //Add listener
    CMAPI.getInstance().addConnectionStatusListener(listener);
}
```
And remove the listener in the onStop method:

```plain
@Override
protected void onStop() {
    super.onStop();
    //remove listener    CMAPI.getInstance().removeConnectionStatusListener(listener);
}
```
Of course, you can also monitor in other places, such as Fragment or Application, which can be added to any place you need to monitor. And when you do not need to monitor (such as the life cycle is completed), remove the listener.
★ Note: The login status will change at any time due to network conditions. For example, when the network is disconnected, the connection will be disconnected when logged in, and the system will automatically try to reconnect and call back the onConnecting() method until the network is restored again and the connection is successful Call back the onEstablished() method.

### **Login callback process**

![图片](https://github.com/welineio/WelineSDKAndroidDemo/blob/master/img/image%20(6).png)

### **Login status value**

★ Note: You can monitor the login status in real time by adding a listener, or you can use the following code to get the latest status of the current login connection:

int status = CMAPI.getInstance().getRealtimeInfo().getCurrentStatus();

Note: CMAPI.getInstance().getRealtimeInfo() may be null.

The definition of each status value is as follows:

|State name (constant in Constants)|Status value|Description|
|:----|:----|:----|:----|:----|:----|
|CS_UNKNOWN|0|Uninitialized|
|CS_PREPARE|1|Ready|
|CS_CONNECTING|2|Connecting|
|CS_CONNECTED|3|Successfully connected|
|CS_DISCONNECTED|4|Disconnected|
|CS_AUTHENTICATED|5|Successfully authenticated|
|CS_DISCONNECTING|8|Disconnecting|
|CS_ESTABLISHED|200|The virtual network connection is successful, and the login is successful|

## **Step 5: Log out**

After calling the logout API, the logged out user will no longer save the login ticket and cannot automatically log in.

```plain
public class SettingActivity extends Activity {
    @Override
    protected void onStart() {
        super.onStart();
        //Add login status listener
        CMAPI.getInstance().addConnectionStatusListener(statusListener);
        ...
    }
    public void loginOut(View view){//Logout
        //The user clicked the logout button
        if(view.getId() == R.id.btnLoginOut){
            //Call the logout API
            CMAPI.getInstance().disconnect();
            //Remove account information
            CMAPI.getInstance().removeUser(CMAPI.getInstance().getBaseInfo().getUserId());
            if(networkNotAvailable){
              //When there is no network (cannot connect to the external network), it will automatically disconnect without waiting for the callback
              //Never reconnect
              CMAPI.getInstance().cancelLogin();
                //Log out successfully, jump to the login page
              startActivity(new
              Intent(SettingActivity.this,LoginActivity.class));
              finish()
            }
        }
    }
    protected ConnectStatusListenerPlus statusListener = new ConnectStatusListenerPlus() {
        @Override
        public void onDisconnected(int reason) {
            //Log out successfully, jump to the login page
            startActivity(new Intent(SettingActivity.this,LoginActivity.class));
            finish();
        }
    };
    @Override
    protected void onStop() {
        super.onStop();
        //Remove status listener
        CMAPI.getInstance().removeConnectionStatusListener(statusListener);
    }
    ...
```
## **Other event monitoring**

If you need to monitor the occupancy status of the VPN tunnel, real-time online duration or delay, etc., you can monitor it through the CMAPI.getInstance().subscribe method.

For details on other event monitoring, please refer to [Related API-EventObserver].

```plain
CMAPI.getInstance().subscribe(observer);
private EventObserver observer = new EventObserver() {
    @Override
    public void onRealTimeInfoChanged(RealtimeInfo info) {
        //Real-time information changes, this method calls back once per second after subscribing to refresh real-time information such as online duration and delay
    }
    @Override
    public void onTunnelRevoke(boolean isRevoked) {
        // isRevoked: Whether the VPN tunnel is occupied, call back this method only when its value changes
    }
};
```
Remove event listener:
```plain
CMAPI.getInstance().unsubscribe(observer);
```
##**4. Error code**

The login callback error code errorCode and the definition of the

reason error code in onDisconnected (shared):

import net.sdvn.cmapi.global.Constants;

|State name (constant in Constants)|Status value|Description|
| :-----| :-----| :-----|
|CS_UNKNOWN|0|Unknown|
|DR_BY_USER|1|Disconnected by user|
|DR_MISVERSION|2|The version is too low (login restricted)|
|DR_NETWORK|3|The network connection with the background server is interrupted (such as background upgrade)|
|DR_MISSING_INFO|4|No account information has been configured or device account information is missing|
|DR_INVALID_USER|5|Invalid username|
|DR_INVALID_PASS|6|Invalid password, or password|
|DR_DEVICE_DISABLED|9|Device is restricted from logging in|
|DR_MAX_DEVICE|10|The number of devices exceeds the limit|
|DR_NO_NETWORK|11|No virtual network available (generally does not happen)|
|DR_TUN_DEVICE|15|There is no available tun device in the system, or other programs occupy the tun0 device|
|DR_AUX_AUTH_DISMATCH|20|The authentication mode does not match (the verification code is wrong or the authentication mode is wrong)|
|DR_INVALID_AUTHORIZATION|21|Invalid certification|
|DR_CALL_THIRD_API_FAIL|29|Failed to connect with third-party interface|
|CE_APP_RUNTIME_ERROR|2017|App running error|
|DR_VPN_PERMISSION_DENIED|2018|VPN permission denied, please set to allow this application to use VPN permission|
|DR_CONNECT_TIMEOUT|2019|Connection timed out|
|DR_VPN_TUNNEL_IS_OCCUPIED|2020|The VPN tunnel is occupied, please turn off other VPNs|

## **5. Reference API**
## **CMAPI**

|method|Description|
|:----|:----|
|BaseInfogetBaseInfo()|Get basic information Contains the basic information of the logged-in user and the ticket used for verification.|
|RealtimeInfo getRealtimeInfo()|Get real-time information Contains real-time information about the current connection, such as: online duration, connection delay, connection status, etc.|
|boolean removeUser(String id)|Remove the saved account.|
|void addConnectionStatusListener(ConnectStatusListenerPlus<br>statusListener)|Subscription status events.|
|void removeConnectionStatusListener(ConnectStatusListenerPlus statusListener)|Cancel subscription of status event.|
|void subscribe(EventObserver)|Subscribe to other events.|
|void unsubscribe (EventObserver)|Cancel other event subscriptions.|
|List<Device> getDevices ()|Get a list of devices in the current network.|
|List<Network> getNetworkList()|Get the user's network list|

## **BaseInfo**

|method|Description|
|:----|:----|
|String getVersion()|Contains current basic information|
|String getAccount()|The currently cached account (the account you have logged in most recently)|
|List<String> getUserList()|A collection of accounts that are successfully logged in and saved|
|String getDomain()|domain name|
|String getVip()|Virtual IP|
|String getVmask()|Virtual mask|
|String getTicket()|Login authentication ticket|
|String getSnid()|ID of the current SmartNode|
|boolean getDlt()|Is DLT available|

## **RealtimeInfo**

|method|Description|
|:----|:----|
|int getCurrentStatus()|Current state|
|int getNetLatency()|Network delay|
|long getOnlineTime()|Online Time|

## **Device**

|method|Description|
|:----|:----|
|String getID()|DeviceID|
|String getName()|Device name|
|String getOwner()|Device Owner|
|String getUserId()|User ID of the device owner|
|String getDomain()|Device domain name|
|String getVip()|Device virtual IP|
|String getPriIp()|IP assigned by the LAN where the device is located|

## **Network**

|method|Description|
|:----|:----|
|String getID()|Network ID|
|String getName()|Network name|
|String getOwner()|Network owner|
|String getUId()|Network owner ID|
|booleanisCurrent()|Whether it is the current network|

## **ConnectStatusListenerPlus**

|method|Description|
|:----|:----|
|void onConecting()|Call back when the connection is started.<br>It can also be regarded as an automatic reconnection after a successful connection (due to factors such as the network). Generally speaking, unless the logout interface is called after a successful login, the SDK will not actively disconnect and stop the connection. After an abnormal disconnection, it will try to automatically reconnect and call back this method. At this time, you can determine whether to terminate the reconnection (call cancelLogin()).|
|void onAuthenticated()|Call back after authentication is completed, indicating that the account has been authenticated.|
|void onConnected()|Call back after a successful login, indicating that the data has been loaded and has the qualification to connect to the virtual network.|
|void onEstablished()|Call back after successfully connecting to the virtual network, and then you can access the virtual network normally.|
|void onDisconnecting()|Call back when the connection is disconnecting|
|void onDisconnected(int reason)|The callback after disconnection also corresponds to the state before the first login. This method will also be called back after the login fails. Generally speaking, unless the logout interface is called after a successful login, the SDK will not actively disconnect and stop the connection. After an abnormal disconnection, it will try to automatically reconnect.<br>reason: Identifies the reason for disconnection, please refer to [error code] for details.|

## **EventObserver**


|method|Description|
|:----|:----|
|void onRealTimeInfoChanged(RealtimeInfo info)|Real-time information changes, this method calls back once per second after subscribing, and the UI can be updated in real time as needed|
|void onTunnelRevoke (boolean isRevoked)|Call back when the VPN tunnel changes between occupied and idle.<br>isRevoked is true:<br>1. Call back when it is detected that the VPN tunnel is occupied;<br>2. Call back directly when the tunnel is preempted;<br>3. Check if there are other applications using VPN before logging in, and call back if there are.<br><br>isRevoked is false:<br>After the VPN tunnel is occupied, the sdk will automatically detect whether there are other applications currently using the VPN tunnel at a certain interval. If not, it will call back. Each time it is occupied, it will only call back once (it will not return within 5 seconds after the callback returns true. false, protection period).|

## **6. FAQ**
* **Cannot log in, prompt 2018——VPN permission denied**
1. Please note that the Weline.io service is built on the basis of a VPN. To use the Weline.io service, please authorize the VPN for the application first. When the login is called for the first time, a system permission inquiry window will pop up. If you refuse, you need to enter the system setting interface to set it.

2.VThe VPN tunnel is occupied by other applications. Please close other VPN applications and try to log in again. If the channel is preempted by other VPN applications after logging in, the SDK will automatically reconnect after closing other VPN applications.

* **About network switching and disconnected reconnection**

The VPN connection status maintained by the SDK will automatically reconnect when disconnected. Common abnormal disconnections are as follows:

1. Network fluctuations

2. Switched the currently used network, such as changing WIFI, switching between 4G and WIFI

3. The VPN tunnel is occupied (in order to avoid continuous competition for the tunnel, the connection will be reconnected when the tunnel is not occupied)

When automatically reconnecting, the status event listener will call back onConnecting(), and the logic of waiting for the animation can be written in the callback

* **The connection is interrupted when running in the background**

Some Android systems (EMUI) do not support long-term background operation by default, and need to manually set the self-start permission. In addition, some Android systems (MIUI) run normally in the background, but cannot automatically reconnect after the connection is interrupted. You need to manually set the power saving policy to no longer restrict this application.

Some systems can use the following methods to open the current application settings to modify self-starting permissions and power saving strategies. It is recommended to use pop-up prompts to guide users to click to jump:

```plain
Intent intent = new Intent();
intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
intent.setData(uri);
startActivity(intent);
```


