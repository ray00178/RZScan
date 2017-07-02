RZScan ![](http://arminray.ga/image/rzscan/rzscan_planform.svg) ![](http://arminray.ga/image/rzscan/rzscan_version.svg) ![](http://arminray.ga/image/rzscan/rzscan_license.svg)
====
RZScan用於Android，在掃描各種條碼或是製作一維/二維條碼，相關功能如下：<br/>
* 支援掃描各種的條碼格式 
* 對於__ 6.0以上版本 __，已將權限做很好的處理，故無需擔心要自行處理
* 依照你的專案配色，可自訂StatusBarColor、ToolBarColor
* 無論是在Activity、Frangment，都可支持使用
* 你可以自行定義掃描框架的大小、顏色或是描述的文字.<br/>

Screenshots <br/><br/>
![](https://github.com/ray00178/RayZhangScan/blob/master/Screenshot_Scan.png)
![](https://github.com/ray00178/RayZhangScan/blob/master/Screenshot_QRcode.png)
![](https://github.com/ray00178/RayZhangScan/blob/master/Screenshot_Code-128.png)
![](https://github.com/ray00178/RayZhangScan/blob/master/Screenshot_EAN-8.png)
<img src="https://github.com/ray00178/RayZhangScan/blob/master/Screenshot_1.gif" alt="Demo_gif" title="Demo_gif" width="300" height="500" />
<img src="https://github.com/ray00178/RayZhangScan/blob/master/Screenshot_2.gif" alt="Demo_gif" title="Demo_gif" width="300" height="500" /><br/><br/>
Gradle
====
```java
compile 'com.rayzhang.android:rzscan:1.0.0'
```
Maven
====
```java
<dependency>
  <groupId>com.rayzhang.android</groupId>
  <artifactId>rzscan</artifactId>
  <version>1.0.0</version>
  <type>pom</type>
</dependency>
```
Usage
====
  1.在Androidmanifest.xml加入以下程式碼
  ```xml
  <!-- android:theme = Set according to your style
  <style name="AppTheme" parent="Theme.AppCompat.Light.DarkActionBar">
      <item name="colorPrimary">@color/colorPrimary</item>
      <item name="colorPrimaryDark">@color/colorPrimaryDark</item>
      <item name="colorAccent">@color/colorAccent</item>
  </style>

  <style name="AppNoActionBar" parent="AppTheme">
      <item name="windowActionBar">false</item>
      <item name="windowNoTitle">true</item>
  </style> -->
  <activity
      android:name="com.rayzhang.android.rzscan.CaptureActivity"
      android:configChanges="orientation|keyboardHidden|screenSize"
      android:screenOrientation="portrait"
      android:theme="@style/AppNoActionBar"
      android:windowSoftInputMode="stateAlwaysHidden|stateHidden" />
  ```
  2.在Androidmanifest.xml加入以下權限
  ```xml
    <uses-permission android:name="android.permission.CAMERA" />
  ```
  3.調用RZAlbum，有多種使用方法
  ```java
  /**
    * @param ofAppName : (必要)
    * @param showStatusBar : (選擇性) (預設:true)     
    * @param setStatusBarColor : (選擇性) (預設:#000000) 
    * @param setToolBarColor : (選擇性) (預設:transparent)
    * @param setToolBarTitleColor : (選擇性)  (預設:#ffffff)
    * @param setToolBarTitle : (選擇性)  (預設:RZZxingScan)
    * @param setDialogIcon : (選擇性)
    * @param setScanFrameSize : (選擇性)  (預設:250dp,250dp)
    * @param setScanBorderColor : (選擇性)  (預設:#ffff00)
    * @param setScanBorderWidth : (選擇性)  (預設:15px)
    * @param setScanBorderLength : (選擇性)  (預設:65px)
    * @param setDescriptionColor : (選擇性)  (預設:#808080)
    * @param setDescriptionOffSetTop : (選擇性)  (預設:10dp)
    * @param setDescriptionSize : (選擇性)  (預設:10sp)
    * @param start : (必要)
    */
    RZScan.ofAppName("RZScan")
            .start(this, REQUEST_RZSCAN);
    /**
      * Or Like this
      */
    RZScan.ofAppName("RZScan")
            .showStatusBar(false)
            .setStatusBarColor(Color.argb(255, 255, 255, 0))
            .setToolBarColor(Color.argb(255, 255, 255, 0))
            .setToolBarTitleColor(Color.argb(255, 255, 255, 255))
            .setToolBarTitle("QRCode Scan")
            .setDialogIcon(R.drawable.ic_bar_code_30dp)
            .setScanFrameSize(new int[]{215, 215})
            .setScanBorderColor(Color.argb(255, 0, 153, 51))
            .setScanBorderWidth(5)
            .setScanBorderLength(40)
            .setDescriptionColor(Color.argb(255, 38, 38, 38))
            .setDescriptionOffSetTop(20f)
            .setDescriptionSize(16f)
            .start(this, REQUEST_RZSCAN);
  ```
  4.Override Activity/Fragment的onActivityResult方法
  ```java
  @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_RZSCAN:
                    String result = RZScan.getResult(data);
                    Log.d("RZScan", "Result:" + result);
                    break;
            }
        }
    }
  ```
  5.如果想要製作一維/二維條碼，可以這樣做
  ```java
    /*
     * 支援製作的條碼格式 : 
     * AZTEC, CODABAR, CODE_39, CODE_93, CODE_128, DATA_MATRIX, EAN_8, EAN_13,
     * ITF, PDF_417, QR_CODE, UPC_A, UPC_E
     */
    // 無logo
    mImgCode1.setImageBitmap(RZScan.ENCODE("Hello I'm Ray.", 400, 200, null, BarcodeFormat.QR_CODE));
    // 有logo
    Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.ic_bar_code_30dp);
    mImgCode2.setImageBitmap(RZScan.ENCODE("Hello I'm Ray.", 400, 200, logo, BarcodeFormat.QR_CODE));
  ```
  6.如果想要解析一維/二維條碼，可以這樣做
  ```java
    RZScan.DECODE(filePath, new AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            Log.d(TAG, "Result：" + result);
        }
        @Override
        public void onAnalyzeFailed() {
            Log.d(TAG, "Result：Sorry! can' decode.");
        }
    });
  ```
  7.如果想自訂掃描時的描述或是Dialog標題、內容描述及按鈕名稱，請在strings.xml覆蓋下列的名稱，即可
  ```xml
   <string name="rzscan_description">將行動條碼對準畫面即可讀取</string>
   <string name="rzscan_dia_title">啟用相機權限</string>
   <string name="rzscan_dia_description">開放使用權限後，你就可以開始掃描任何的條碼。</string>
   <string name="rzscan_dia_ok">開啟</string>
   <string name="rzscan_dia_cancel">我不要</string>
  ```
Notice
====
  該庫有引用下列類別庫
  ```xml
  // Google ZXing 
  compile 'com.google.zxing:core:3.3.0'
  ```
License
====
  ```
Copyright 2017 RayZhang

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
  ```
