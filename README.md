RZScan ![](http://arminray.ga/image/rzscan/rzscan_planform.svg) ![](http://arminray.ga/image/rzscan/rzscan_version.svg) ![](http://arminray.ga/image/rzscan/rzscan_license.svg)
====
The RZScan for android to scan code or encode/decode library. And usage：<br/>
* Support for scanning most barcodes. 
* For __6.0 or later__, The permissions have been handled very well，So don't worry about their own.
* According to your project color, Setting ur StatusBarColor、ToolBarColor.
* In Activity or Frangment, Can support the use.
* You can customize the size of the scan frame、color or describe the text.<br/>

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
  1.Androidmanifest.xml, Add the following code.
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
  2.Androidmanifest.xml, Add the following permissions.
  ```xml
    <uses-permission android:name="android.permission.CAMERA" />
  ```
  3.Use RZScan. There are many ways to call.
  ```java
  /**
    * @param ofAppName : (required)
    * @param showStatusBar : (choose) (default:true)     
    * @param setStatusBarColor : (choose) (default:#000000) 
    * @param setToolBarColor : (choose) (default:transparent)
    * @param setToolBarTitleColor : (choose)  (default:#ffffff)
    * @param setToolBarTitle : (choose)  (default:RZZxingScan)
    * @param setDialogIcon : (choose)
    * @param setScanFrameSize : (choose)  (default:250dp,250dp)
    * @param setScanBorderColor : (choose)  (default:#ffff00)
    * @param setScanBorderWidth : (choose)  (default:15px)
    * @param setScanBorderLength : (choose)  (default:65px)
    * @param setDescriptionColor : (choose)  (default:#808080)
    * @param setDescriptionOffSetTop : (choose)  (default:10dp)
    * @param setDescriptionSize : (choose)  (default:10sp)
    * @param start : (required)
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
  4.Override Activity's/Fragment's onActivityResult method.
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
  5.If you want to make a barcode, you can do that.
  ```java
    /*
     * Barcode format : 
     * AZTEC, CODABAR, CODE_39, CODE_93, CODE_128, DATA_MATRIX, EAN_8, EAN_13,
     * ITF, PDF_417, QR_CODE, UPC_A, UPC_E
     */
    // No logo
    mImgCode1.setImageBitmap(RZScan.ENCODE("Hello I'm Ray.", 400, 200, null, BarcodeFormat.QR_CODE));
    // Have logo
    Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.ic_bar_code_30dp);
    mImgCode2.setImageBitmap(RZScan.ENCODE("Hello I'm Ray.", 400, 200, logo, BarcodeFormat.QR_CODE));
  ```
  6.If you want to decode barcode, you can do that.
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
  7.If you want to customize the description text or dialog title、description, please overwrite the following names in strings.xml.
  ```xml
   <string name="rzscan_description">Enter something that u want</string>
   <string name="rzscan_dia_title">Enter something that u want</string>
   <string name="rzscan_dia_description">Enter something that u want</string>
   <string name="rzscan_dia_ok">Enter something that u want</string>
   <string name="rzscan_dia_cancel">Enter something that u want</string>
  ```
Notice
====
  This library references the following categories library.
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
Chinese description
====
[中文說明](https://github.com/ray00178/RayZhangScan/blob/master/README_zh.md)
