# NetSuite ERP App Overview

This is an integration demo application based on the NetSuite OAuth 2.0 Authentication + REST API. The application only supports login with the grandstream account and inventory transfer and inventory transfer record query because of limited by the functions and permissions opened. You can refer to the application example to modify the relevant configuration of the company and expand the function.

## NetSuite Platform's Important Configuration
[`Official documentation`](https://pub.dev/packages/image_picker_for_web#limitations-on-the-web-platform)

You must login to the NetSuite platform, enable the corresponding functions, and create and setting the integration corresponding to the configuration application before developing a NetSuite ERP application.

#### Step 1: Enable  Features
[`Official documentation`](https://docs.oracle.com/en/cloud/saas/netsuite/ns-online-help/section_157771482304.html#To-enable-OAuth-2.0-feature%3A)

Enter NetSuite platform -> Setup -> Company -> Enable Features page, then enter the Tab "SuiteCloud" and enable "OAuth 2.0" under the "Manager Authentication". Please refer to the official documentation above for detailed configuration instructions.

#### Step 2: Create the Integration for App
[`Official documentation`](https://docs.oracle.com/en/cloud/saas/netsuite/ns-online-help/section_157771733782.html#procedure_157838925981)

Enter Setup -> Integration -> Manager Integrations, create the integration for App, and modify the configuration for OAuth 2.0. Please refer to the official documentation above for detailed configuration instructions.

## Setup NetSuite in Android Project

Setup the integration's configuration of NetSuite platform.

#### Step 1: Setting BuildConfig in build.gradle.

```groovy
android {
    android.buildFeatures.buildConfig = true

    buildTypes {
        // The CONSUMER_KEY and CONSUMER_SECRET of the integration (They only appear when you create the integration)
        val NETSUITE_CONSUMER_KEY = "XXXXXX" // Replace with your consumer key
        val NETSUITE_CONSUMER_SECRET = "XXXXXX" // Replace with your account ID
        // The company's Account ID: Such as "11111_DEMO1"
        val NETSUITE_ACCOUNT_ID = "XXXXXX" // Replace with your account ID
        // The company's Account ID in the URL of NetSuite API: Such as "11111-demo1", "https://11111-demo1.suitetalk.api.netsuite.com
        val NETSUITE_ACCOUNT_ID_IN_URL = "XXXXXX" // Replace with your account ID
        // The callback URI for the login authorization with OAuth 2.0: 
        // 1. The authorization sign-in result is returned to the app via this URI.
        // 2. The app needs to add the corresponding intent-filter in the AndroidManifest.xml for the Activity that receives the login authorization result:
        //    Such as, "xxx://xxx/xxx" (grandstream://com.exaple/login/callback)
        val NETSUITE_REDIRECT_URI = "XXXXXX" // Replace with your account ID

        debug {
            buildConfigField("String", "NETSUITE_CONSUMER_KEY", "\"$NETSUITE_CONSUMER_KEY\"")
            buildConfigField("String", "NETSUITE_CONSUMER_SECRET", "\"$NETSUITE_CONSUMER_SECRET\"")
            buildConfigField("String", "NETSUITE_ACCOUNT_ID", "\"$NETSUITE_ACCOUNT_ID\"")
            buildConfigField("String", "NETSUITE_ACCOUNT_ID_IN_URL", "\"$NETSUITE_ACCOUNT_ID_IN_URL\"")
            buildConfigField("String", "NETSUITE_REDIRECT_URI", "\"$NETSUITE_REDIRECT_URI\"")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")

            buildConfigField("String", "NETSUITE_CONSUMER_KEY", "\"$NETSUITE_CONSUMER_KEY\"")
            buildConfigField("String", "NETSUITE_CONSUMER_SECRET", "\"$NETSUITE_CONSUMER_SECRET\"")
            buildConfigField("String", "NETSUITE_ACCOUNT_ID", "\"$NETSUITE_ACCOUNT_ID\"")
            buildConfigField("String", "NETSUITE_ACCOUNT_ID_IN_URL", "\"$NETSUITE_ACCOUNT_ID_IN_URL\"")
            buildConfigField("String", "NETSUITE_REDIRECT_URI", "\"$NETSUITE_REDIRECT_URI\"")
        }
    }
}
```

#### Step 2: Register the Activity's intent-filter in the AndroidManifest.xml for redirect uri.
The callback URI for the login authorization with OAuth 2.0: 
1. The authorization sign-in result is returned to the app via this URI.
2. The app needs to add the corresponding intent-filter in the AndroidManifest.xml for the Activity that receives the login authorization result: Such as, "xxx://xxx/xxx" (grandstream://com.exaple/login/callback)
```xml
<intent-filter>
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.BROWSABLE" />
    <data
        android:host="com.exaple"
        android:path="/login/callback"
        android:scheme="grandstream" />
</intent-filter>
```

## Network Requests

The app is designed to use a single RetrofitClient which is configured when the app is started. The following is a simple example of a network request:

#### Register the `RetrofitClient`

```kotlin
interface NetsuiteServiceApi {
    // Get the access token
    @FormUrlEncoded
    @POST(NetworkConfig.OAUTH2_STEP2_TOKEN_PATH)
    fun getToken(
        @Header(NetworkConfig.HEADER_AUTHORIZATION) authorization: String,
        @Field(NetworkConfig.OAUTH2_STEP2_CODE) code: String,
        @Field(NetworkConfig.OAUTH2_STEP2_REDIRECT_URI_KEY) redirectUri: String,
        @Field(NetworkConfig.OAUTH2_STEP2_GRANT_TYPE_KEY) grantType: String,
        @Field(NetworkConfig.OAUTH2_STEP2_CODE_VERIFIER_KEY) codeVerifier: String
    ): Call<OAuth2Token>
}
```

```kotlin
object RetrofitClient {
    // network timeout
    private const val TIME_OUT = 20

    // gson
    private val gson = Gson().newBuilder().setLenient().serializeNulls().create()

    // network api: Setting base url by BuildConfig
    val api by lazy { getService(NetsuiteServiceApi::class.java, String.format(NetworkConfig.BASE_REST_URL, BuildConfig.NETSUITE_ACCOUNT_ID_IN_URL)) }

    private val client: OkHttpClient
        get() {
            val builder = OkHttpClient.Builder()
            builder
                .sslSocketFactory(SSLSocketManager.sslSocketFactory, SSLSocketManager.x509TrustManager!!)
                .hostnameVerifier(SSLSocketManager.hostnameVerifier)
                .connectTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
                .writeTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
                .readTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
            return builder.build()
        }

    // 动态代理构建Retrofit的服务
    private fun <S> getService(serviceClass: Class<S>, baseUrl: String): S {
        return Retrofit.Builder()
            .callFactory(TrackCallFactory(client))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(baseUrl)
            .build()
            .create(serviceClass)
    }
}
```

#### Make a network request by the `RetrofitClient`
```kotlin
    fun getToken(authorize: OAuth2Authorize, codeVerify: String): Call<OAuth2Token> {
    return RetrofitClient.api.getToken(
        Credentials.basic(BuildConfig.NETSUITE_CONSUMER_KEY, BuildConfig.NETSUITE_CONSUMER_SECRET),
        authorize.code,
        BuildConfig.NETSUITE_REDIRECT_URI,
        NetworkConfig.OAUTH2_STEP2_GRANT_TYPE_VALUE,
        codeVerify
    )
}
```