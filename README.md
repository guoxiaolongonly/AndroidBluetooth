[![CSDN](https://img.shields.io/badge/CSDN-@xiaolongonly-blue.svg?style=flat)](http://blog.csdn.net/guoxiaolongonly)
[![PersonBlog](https://img.shields.io/badge/PersonBlog-@xiaolongonly-blue.svg?style=flat)](http://xiaolongonly.cn/)

![界面展示](https://github.com/guoxiaolongonly/AndroidBluetooth/blob/master/screen/666.gif)
## Android蓝牙通讯封装

基于简单配对模式RFCOMM连接方式写的一个蓝牙通讯模块。

### 使用方式：

1.初始化

	推荐使用一个全局的Activity做初始化，因为在整个生命周期其实蓝牙只需要连接一次。
	
```java
BtManager.init(xxActivity);
```
2.获取BtManager对象 
``` java
btManager= BtManager.getInstance();
```
3.设置扫描回调

<iframe src="https://carbon.now.sh/embed/?bg=rgba(52%2C77%2C98%2C1)&t=base16-dark&wt=none&l=text%2Fx-java&ds=true&dsyoff=20px&dsblur=68px&wc=true&wa=true&pv=48px&ph=32px&ln=false&fm=Hack&fs=14px&lh=133%25&si=false&code=btManager.setIScanCallback(new%2520IScanCallback()%2520%257B%250A%2520%2520%2520%2520%2520%2520%2520%2520%2520%2520%2520%2520%2540Override%250A%2520%2520%2520%2520%2520%2520%2520%2520%2520%2520%2520%2520public%2520void%2520discoverDevice(BluetoothDevice%2520bluetoothDevice%252C%2520short%2520rssi)%2520%2509%2509%2509%257B%250A%2520%2520%2520%2520%2520%2520%2520%2520%2520%2520%2520%2520%2520%2520%2520%2520mSurroundBluetoothAdapter.addItem(bluetoothDevice)%253B%250A%2520%2520%2520%2520%2520%2520%2520%2520%2520%2520%2520%2520%257D%250A%250A%2520%2520%2520%2520%2520%2520%2520%2520%2520%2520%2520%2520%2540Override%250A%2520%2520%2520%2520%2520%2520%2520%2520%2520%2520%2520%2520public%2520void%2520scanTimeout()%2520%257B%250A%2520%2520%2520%2520%2520%2520%2520%2520%2520%2520%2520%2520%2520%2520%2520%2520Toast.makeText(BlueToothActivity.this%252C%2520%2522%25E6%2589%25AB%25E6%258F%258F%25E8%25B6%2585%25E6%2597%25B6%25EF%25BC%2581%2522%252C%2520Toast.LENGTH_LONG).show()%253B%250A%2520%2520%2520%2520%2520%2520%2520%2520%2520%2520%2520%2520%257D%250A%250A%2520%2520%2520%2520%2520%2520%2520%2520%2520%2520%2520%2520%2540Override%250A%2520%2520%2520%2520%2520%2520%2520%2520%2520%2520%2520%2520public%2520void%2520scanFinish(List%253CBluetoothDevice%253E%2520bluetoothList)%2520%257B%250A%250A%2520%2520%2520%2520%2520%2520%2520%2520%2520%2520%2520%2520%257D%250A%2520%2520%2520%2520%2520%2520%2520%2520%257D)%250A&es=2x&wm=false&ts=false" style="transform:scale(0.7); width:1024px; height:473px; border:0; overflow:hidden;" sandbox="allow-scripts allow-same-origin"></iframe>

4.设置连接状态回调

<iframe src="https://carbon.now.sh/embed/?bg=rgba(52,77,98,1)&t=base16-dark&wt=none&l=text/x-java&ds=true&dsyoff=20px&dsblur=68px&wc=true&wa=true&pv=48px&ph=32px&ln=false&fm=Hack&fs=14px&lh=133%&si=false&code=btManager.setConnectStateCallback(new IConnectStateCallBack() {
            @Override
            public void connecting() {

            }

            @Override
            public void connected() {

            }

            @Override
            public void disConnect() {
                currentConnectDevice = null;
            }

            @Override
            public void waitForConnect() {

            }

            @Override
            public void connectedToDeviceName(BluetoothDevice device) {
                currentConnectDevice = device;
                launchActivity(device);
            }
        });&es=2x&wm=false&ts=false"
  style="transform:scale(0.7); width:1024px; height:473px; border:0; overflow:hidden;"sandbox="allow-scripts allow-same-origin">
</iframe>


4.扫描设备
```java
  btManager.scanBluetooth();
```

5.连接到设备

```java
btManager.connectToDevice(bluetoothDevice);
```
### 协议处理

找到DataProcessHandler这个类，修改需要读取的数据规则，如果无规则处理可直接Write到ByteDataReadProcess。（后续会将这块抽成基类，由用户扩展实现。。）

这样就算完成了。
大家可以参照一下我的小demo。目前还在完善中
