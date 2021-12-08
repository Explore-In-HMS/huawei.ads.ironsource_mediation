# Huawei-IronSource Mediation Github Documentation

![Latest Version](https://img.shields.io/badge/latestVersion-1.0.4-yellow) ![Kotlin](https://img.shields.io/badge/language-kotlin-blue)
<br>
![Supported Platforms](https://img.shields.io/badge/Supported_Platforms:-Native_Android_-orange)


# Introduction

This is a project to demonstrate how to use IronSource’s mediation feature with Huawei Ads Kit.

# Compatibility

|   |  | Interstitial Ad |
| --- | --- | --- |
| Native (Java/Kotlin) |  | ✅ |


# How to start?
  
## Create an ad unit on Huawei Publisher Service

1. Sign in to [Huawei Developer Console](https://developer.huawei.com/consumer/en/console) and create an AdUnit

## Create an app & Add Custom network on IronSource Platform:

Make sure to check the article on **[How to use Huawei Ads with IronSource mediation ?](https://medium.com/huawei-developers/how-to-use-huawei-ads-with-ironsource-mediation-61fe6efd5f7)**

1. Sign in to your [Platform Ironsource](https://platform.ironsrc.com/) and add an App
2. Go to **SDK Networks** and click Manage Networks add Custom Adapter 
( **Important Note:** Your account must be activated to be able to see Custom Adapter section)
3. Enter the Network Key ( Network Key is : 15b993999)
4. Enter the [ClientID and SecretKey](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/reporting-api-client-id-and-key-0000001050933698) that obtained from Huawei Console
5. Click the edit button and add AdUnitID
6. Add the adapter and its dependencies into your project
7. Configuration is done

**Important Note:** To be able to do mediation, do not forget to disable test mode on IronSource platform.

## Huawei Ads Custom Adapter 
| Network Key        | Network Name           |
| ------------- |:-------------:|
| 15b993999     | HuaweiAds |


### Parameters Description
| Key | Description | Possible value |
| --- | --- | --- |
| adUnitID  | Huawei Ads Unit ID | String |
| appID | Huawei Ads App ID | String |
| tagForChildProtection | Sets the tag for child-directed content, to comply with the Children's Online Privacy Protection Act (COPPA). | <ul><li>`true`: You want your ad content to be COPPA-compliant (interest-based ads and remarketing ads will be disabled for the ad request). </li><li>`false`: You do not want your ad content to be COPPA-compliant. </li></ul> |
| tagUnderAgeOfPromise | Sets the tag for users in the European Economic Area (EEA) under the age of consent, to comply with the General Data Protection Regulation (GDPR). Ad requests with this tag enabled will be unable to obtain personalized ads. | <ul><li> `true`: You want the ad request to meet the ad standard for users under the age of consent. </li><li> `false`: You do not want the ad request to meet the ad standard for users under the age of consent. </li></ul>  |

> Note: All values ​​must be String format.


<h1 id="integrate-huawei-sdk">
Integrate the Huawei Mediation SDK
</h1>

In the **project-level build.gradle**, include Huawei’s maven repository

```groovy
repositories {
    google()
    jcenter() // Also, make sure jcenter() is included
    maven { url 'https://developer.huawei.com/repo/' } // Add this line
    maven {url "https://jitpack.io"} // Add this line
}

...

allprojects {
    repositories {
        google()
        jcenter() // Also, make sure jcenter() is included
        maven { url 'https://developer.huawei.com/repo/' } //Add this line
        maven {url "https://jitpack.io"} // Add this line
    }
}
```
<h1 id="app-level">
</h1>

In the **app-level build.gradle**, include Huawei Ads dependency (required by the adapter) and the Huawei mediation adapter

```groovy
dependencies {
    implementation 'com.huawei.hms:ads:3.4.49.301'
    implementation 'com.github.Explore-In-HMS:huawei.ads.ironsource_mediation:<latest_version>'
}
```
[Check the latest Huawei Ads SDK here](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/publisher-service-version-change-history-0000001050066909)

[Check the latest version of adapter here](#version-change-history)


## **Permissions**
The HUAWEI Ads SDK (com.huawei.hms:ads) has integrated the required permissions. Therefore, you do not need to apply for these permissions. <br />

**android.permission.ACCESS_NETWORK_STATE:** Checks whether the current network is available.   <br/>

**android.permission.ACCESS_WIFI_STATE:** Obtains the current Wi-Fi connection status and the information about WLAN hotspots. <br />

**android.permission.BLUETOOTH:** Obtains the statuses of paired Bluetooth devices. (The permission can be removed if not necessary.) <br />

**android.permission.CAMERA:** Displays AR ads in the Camera app. (The permission can be removed if not necessary.) <br />

**android.permission.READ_CALENDAR:** Reads calendar events and their subscription statuses. (The permission can be removed if not necessary.) <br />

**android.permission.WRITE_CALENDAR:** Creates a calendar event when a user clicks the subscription button in an ad. (The permission can be removed if not necessary.) <br />

# Version Change History

## 1.0.4

Initial release

# Platforms

## Native

This section demonstrates how to use IronSource mediation feature with Huawei Ads Kit on Native android app.

Firstly, integrate the IronSource SDK for Android

[IronSource Android SDK](https://developers.is.com/ironsource-mobile/android/android-sdk/#step-1) can be used for all ad types.

**Note** : Developers can find app level build.gradle in their project from __**"app-folder/app/build.gradle"**__


### **Interstitial Ad**

To use Interstitial ads in Native android apps, please check the IronSource SDK. Click [here](https://developers.is.com/ironsource-mobile/android/interstitial-integration-android-6-4/#step-5) to get more information about IronSource SDKs Interstitial Ad development.

## **Sample Codes Based on Ad Types**

### **Interstitial Ad**

```jsx
        IronSource.setInterstitialListener(this)
        // add impression data listener
        IronSource.addImpressionDataListener(this)
        // init the IronSource SDKAPP_KEY
        IronSource.init(this, APP_KEY,IronSource.AD_UNIT.INTERSTITIAL)
        IronSource.loadInterstitial()
        
            if (IronSource.isInterstitialReady()) {
                //show the interstitial
                IronSource.showInterstitial()
            }
        
```


# Screenshots

## IronSource Ads
<table>
<tr>

<td>
<img src="https://user-images.githubusercontent.com/19581388/144861246-62110f27-c0a5-451b-a28d-0cb4087c2c48.jpg" width="200">

Interstitial Ad
</td>

</tr>
</tr>
</table>

## Huawei Ads
<table>
<tr>

<td>
  <img src="https://user-images.githubusercontent.com/19581388/144861185-0991ebf4-7814-4642-bd2d-f83ad5a241bb.jpg" width="200">

Interstitial Ad
</td>

</tr>
</tr>
</table>



