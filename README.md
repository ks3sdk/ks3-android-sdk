#KS3 SDK for Android使用指南
---
## V.1.4.2 改动日志
* 修改SDK和demo里AuthUtils计算token的方法，增加contentType为空判断

## V.1.4.2 改动日志
* 修改统计日志数据库版本，修复跟视频云播放端和推流端数据统计SDK冲突问题

## V.1.4.1 改动日志
* Ks3HttpRequest中添加setContentType接口
* 修改android-async-http的包名，防止客户端引用不同版本的库时冲突

## V.1.4.0 改动日志
* 新增ResponseHandler中StringBuffer类型回调参数，将SDK每个请求的流程日志及错误信息回调给客户端，方便排查问题
* 修改429错误之后，断点下载后文件不正确问题
* 修改文件下载逻辑，文件下载时先写到临时文件(原文件名.temp)，下载完成时重命名为目标文件
* 新增删除临时文件方法 deleteTempFile（）及获取缓存文件方法getTempFile()

## V.1.3.0 改动日志
* 新增断点续下接口setRange
* 新增下载中断后，自动判断源文件大小进行续传逻辑

## V.1.2.0 改动日志
* 优化日志字段，减小数据量
* 存储方式变更，转为本地数据库存储
* 日志发送方式变更，改为WIFI环境下，每小时触发一次发送，单次最多120条，发完为止。
* 发送日志时，采用gzip压缩
* 提供日志工具类LogClient，提供日志写入方法LogClient.getInstance(mContext).put(mLogRecord);

## V.1.1.0 改动日志

* 规范错误处理，添加Ks3Error
* API精简化，只保留上传下载部分
* 修正了AsyncHttpClient中流未关闭、多线程引起的BUG及特殊字符处理问题
* SDK中加入改动后的AsyncHttpClient源代码
* 添加请求打点及日志上报功能
* 添加当DNS解析失败时，切换保底IP重试请求功能

##开发前准备
###依赖库
本SDK使用了loopj/android-async-http请求库，使用前请先下载对应的jar包，并导入工程

* [android-async-http:1.4.6](https://github.com/loopj/android-async-http)

> 注意： V.1.1.0版后，android-async-http.jar已经被集成进SDK中，不再需要单独添加

###SDK使用准备

- 申请AccessKeyID、AccessKeySecret
- Android权限申明

```

 	<uses-permission android:name="android.permission.INTERNET" />
 	<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
 	<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
 	<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

```

###SDK配置
SDK以jar包形式呈现。将releases文件夹下*ks3-android-sdk-1.0.3.jar*，以及依赖库文件，放入工程libs文件下。

也可以下载[源码:](https://github.com/ks3sdk/ks3-android-sdk/tree/master/Ks3SdkAndroid/ks3-android-sdk)，以Library库形式添加。

> 更多KS3介绍文档，请参考[文档中心:](http://ks3.ksyun.com/doc/index.html)

###运行环境
支持Android 2.2及以上版本

###补充说明
**线程安全:** 考虑到Android 4.0之后不再允许主线程内进行网络请求，以及UI操作必须在主线程中进行。ks3-android-sdk所提供的API，默认让开发者在主线程进行调用，且会以异步方式进行请求，请求回调方法仍将执行在主线程。

如果开发者需要用同步方式进行API调用（即在自己开的线程内,调用同步API请求），需要调用以下方法，以确保API以同步方式进行。

~~~
	
	ArrayList<Bucket> bucketList = client.syncListBuckets();// 同步API调用示例，需要在用户非主线程中执行，失败时会抛出异常
~~~

>同步API接口文档，请见 [SDK-Javadoc:](https://github.com/ks3sdk/ks3-android-sdk/tree/master/releases/doc) 

##安全性

###使用场景
由于在App端明文存储AccessKeyID、AccessKeySecret是极不安全的，因此推荐的使用场景如下图所示：

![](http://androidsdktest21.kssws.ks-cdn.com/ks3-android-sdk-authlistener.png)

如开发者需要在SDK请求完成后，向特定的URL发起一个回调请求，请参考以下使用**Callback**的场景：

![](http://990aa.kssws.ks-cdn.com/calllback.png)

使用Callback回调功能，开发者必须在对应的request中传入**callBackUrl**以及**callBackBody**。 如需自定义参数，要以键值对形式将其传入。

**注：setCallBack()方法仅在PutObejctRequest,CompleteMultipartUploadRequest两个类中提供)**

**方法名**

public void setCallBack(String callBackUrl, String callBackBody, Map<String, String> customParams){};


**参数说明**

**callBackUrl**: 回调url地址

**callBackBody**: 回调参数，参数支持魔法变量、自定义参数以及常量形式，指明了回调方需要用到的参数：eg:String callBackBody = "objectKey=${key}&etag=${etag}&location=${kss-location}&name=${kss-name}";

**customParams**:自定义参数，必须以前缀**kss-**开头


**魔法变量说明：**是一组预先定义的变量，使用${key}形式作为CallBackBody的内容。

目前可用的魔法变量如下:

<table>
  <tr>
    <th>参数</th>
    <th>说明</th>
    <th>备注</th>
  </tr>
  <tr>
    <td>bucket</td>
    <td>文件上传的Bucket</td>
    <td>Utf-8编码</td>
  </tr>
  <tr>
    <td>key</td>
    <td>文件的名称</td>
    <td>Utf-8编码</td>
  </tr>
  <tr>
    <td>etag</td>
    <td>文件Md5值经过base64处理</td>
  </tr>
 <tr>
    <td>objectSize</td>
    <td>文件大小</td>
    <td>以字节标识</td>
  </tr>
 <tr>
    <td>mimeType</td>
    <td>文件类型</td>
  </tr>
 <tr>
    <td>createTime</td>
    <td>文件创建时间</td>
    <td>Unix时间戳表示，1420629372，精确到秒</td>
  </tr>
</table>

**Callback使用范例**：

```

		Map<String,String> customParams = new HashMap<String, String>();
		//自定义参数必须以kss-开头
		params.put("kss-location", "user_input_location");
		params.put("kss-name", "user_input_name");
		request.setCallBack("http://127.0.0.1:19091/kss/call_back", "objectKey=${key}&etag=${etag}&location=${kss-location}&name=${kss-name}", customParams);
		client.putObject(request, new PutObjectResponseHandler() {

			@Override
			public void onTaskProgress(double progress) {
			}

			@Override
			public void onTaskSuccess(int statesCode, Header[] responceHeaders) {
			}

			@Override
			public void onTaskStart() {
			}

			@Override
			public void onTaskFinish() {
			}

			@Override
			public void onTaskFailure(int statesCode, Header[] responceHeaders,
					String response, Throwable paramThrowable) {
			}

			@Override
			public void onTaskCancel() {
			}
		});

```

###Ks3Client初始化
Ks3Client初始化包含以下两种：

- 直接利用AccessKeyID、AccessKeySecret初始化（***不安全,仅建议测试时使用***）
- 实现授权回调（AuthListener）获取Token（签名），即由客户app端向客户业务服务器发送带签名参数的请求，业务服务器实现签名算法并返回Token（签名），SDK及对应Demo中的**AuthUtils**类提供了该算法的Java实现。之后SDK会将onCalculateAuth（）方法返回的Token（签名）带入所有请求，用户正常调用SDK提供的API即可（***推荐使用***）

###请求签名
方法: 在请求中加入名为 Authorization 的 Header，值为签名值。形如：
Authorization: KSS P3UPCMORAFON76Q6RTNQ:vU9XqPLcXd3nWdlfLWIhruZrLAM=

*签名生成规则*
```

		Authorization = “KSS YourAccessKeyID:Signature”

 		Signature = Base64(HMAC-SHA1(YourAccessKeyIDSecret, UTF-8-Encoding-Of( StringToSign ) ) );

 		StringToSign = HTTP-Verb + "\n" +
               Content-MD5 + "\n" +
               Content-Type + "\n" +
               Date + "\n" +
               CanonicalizedKssHeaders +
               CanonicalizedResource;

```

**关于签名的必要说明：**


对于使用AuthListener以Token方式初始化SDK的用户，需要注意onCalculateAuth（）回调方法中的参数，即为计算StringToSign的参数，服务器端应根据上述签名生成规则，利用AccessKeyID及AccessKeySecret**计算出签名并正确返回给SDK**。

onCalculateAuth（）回调方法的参数Content-MD5, Content-Type, CanonicalizedKssHeaders参数**可为空**。若为空，则SDK会使用空字符串("")替代, 但Date和CanonicalizedResource不能为空。

为保证请求时间的一致性，需要App客户端及客户业务服务器保证各自的时间正确性，否则用**错误的时间**尝试请求，会返回403Forbidden错误。

onCalculateAuth（）回调方法参数说明：

* Content-MD5 表示请求内容数据的MD5值, 使用Base64编码
* Content-Type 表示请求内容的类型
* Date 表示此次操作的时间,且必须为 HTTP1.1 中支持的 GMT 格式，客户端应**务必**保证本地时间正确性
* CanonicalizedKssHeaders 表示HTTP请求中的以x-kss开头的Header组合
* CanonicalizedResource 表示用户访问的资源

对应的初始化代码如下：

***For AccessKeyID、AccessKeySecret***

```

		/* Directly using ak&sk */
	    client = new Ks3Client(Constants.ACCESS_KEY_ID,Constants.ACCESS_KEY_SECRET, DummyActivity.this);
	    configuration = Ks3ClientConfiguration.getDefaultConfiguration();
		client.setConfiguration(configuration);

```

***For AuthListener***

```

		/* Using authListener,Let your app server saved ak&sk and return token*/
		client = new Ks3Client(new AuthListener() {
			@Override
			public AuthResult onCalculateAuth(String httpMethod,
					String ContentType, String Date, String ContentMD5,
					String Resource, String Headers) {
				// 此处应由APP端向业务服务器发送post请求返回Token及Data，组装成Result对象返回
				// 需要注意该回调方法运行在非主线程
				// 
				AuthResult result = requsetToAppServer(httpMethod, ContentType,
						Date, ContentMD5, Resource, Headers);
				return result;
			}
		}, DummyActivity.this);
	    configuration = Ks3ClientConfiguration.getDefaultConfiguration();
		client.setConfiguration(configuration);

```
##SDK介绍及使用
###核心类介绍
- Ks3Client 封装接入Web Service的一系列操作，提供更加便利的接口以及回调
- Ks3ClientConfiguration 配置Ks3Client参数，包括代理设置，请求超时时长以及重试次数等
- AuthUtils 包含授权算法的工具类

>为方便开发者使用，SDK在REST API接口返回值基础上进行了封装，具体更多封装类详情请见
>[SDK-Javadoc:](https://github.com/ks3sdk/ks3-android-sdk/tree/master/releases/doc)

###资源管理操作
* [List Buckets](#list-buckets) 列出客户所有的Bucket信息
* [Create Bucket](#create-bucket) 创建一个新的Bucket
* [Delete Bucket](#delete-bucket) 删除指定Bucket
* [Get Bucket ACL](#get-bucket-acl) 获取Bucket的ACL
* [Put Bucket ACL](#put-bucket-acl) 设置Bucket的ACL
* [Head Bucket](#head-bucket) 查询是否已经存在指定Bucket
* [Get Object](#get-object) 下载Object数据
* [Head Object](#head-object) 查询是否已经存在指定Object
* [Delete Object](#delete-object) 删除指定Object
* [Get Object ACL](#get-object-acl) 获得Bucket的acl
* [Put Object ACL](#put-object-acl) 上传object的acl
* [List Objects](#list-objects) 列举Bucket内的Object
* [Put Object](#put-object) 上传Object数据
* [Copy Object](#copy-object)复制Object数据
* [Initiate Multipart Upload](#initiate-multipart-upload) 调用这个接口会初始化一个分块上传
* [Upload Part](#upload-part) 上传分块
* [List Parts](#list-parts) 罗列出已经上传的块
* [Abort Multipart Upload](#abort-multipart-upload) 取消分块上传
* [Complete Multipart Upload](#complete-multipart-upload) 组装所有分块上传的文件
* [Multipart Upload Example Code](#multipart-upload-example-code) 分片上传代码示例

###Service操作

####List Buckets：

*列出客户所有的 Bucket 信息*

**方法名：** 

public void listBuckets(ListBucketsResponceHandler resultHandler) throws Ks3ClientException,Ks3ServiceException{}

**参数说明：**  

* resultHandler：回调接口，包含onSuccess以及onFailure两个回调方法，运行在主线程

**回调参数：**

* statesCode：Http请求返回的状态码，200表示请求成功，403表示签名错误或本地日期时间错误
* responceHeader:Http请求响应报头
* responce：失败时返回的响应正文
* throwable：出错时抛出的异常
* resultList：成功时返回的Bucket容器  

**代码示例：**
```

	   client.listBuckets(new ListBucketsResponceHandler() {
				@Override
				public void onSuccess(int statesCode,
						Header[] responceHeader,
						ArrayList<Bucket> resultList) {
				}

				@Override
				public void onFailure(int statesCode,
						Header[] responceHeader, String responce,
						Throwable throwable) {
				}
			}
		);
```

*列出客户所有的 Bucket 信息*

**方法名：** 

public void listBuckets(ListBucketsRequest request,ListBucketsResponceHandler resultHandler) throws Ks3ClientException,Ks3ServiceException{}

**参数说明：**  

* resultHandler：回调接口，包含onSuccess以及onFailure两个回调方法，运行在主线程
* request：ListBucketsRequest对象

**回调参数：**

* statesCode：Http请求返回的状态码，200表示请求成功，403表示签名错误或本地日期时间错误
* responceHeader:Http请求响应报头
* responce：失败时返回的响应正文
* throwable：出错时抛出的异常 
* resultList：成功时返回的Bucket容器  

**代码示例：**
```

	   client.listBuckets(new ListBucketsRequest(),new ListBucketsResponceHandler() {
				@Override
				public void onSuccess(int statesCode,
						Header[] responceHeader,
						ArrayList<Bucket> resultList) {
				}

				@Override
				public void onFailure(int statesCode,
						Header[] responceHeader, String responce,
						Throwable throwable) {
				}
			}
		);
```
###Bucket操作

####Create Bucket： 

*创建一个新的Bucket*

**方法名：** 

public void createBucket(String bucketName,CreateBucketResponceHandler resultHandler) throw Ks3ClientException,Ks3ServiceException{}

**参数说明：**

* resultHandler：回调接口，包含onSuccess以及onFailure两个回调方法，运行在主线程

**回调参数：**

* statesCode：Http请求返回的状态码，200表示请求成功，400表示Bucke创建个数已达上限或客户端请求格式错误，403表示签名错误或本地日期时间错误，409表示Bucket已存在
* responceHeader:Http请求响应报头
* responce：失败时返回的响应正文
* throwable：出错时抛出的异常, Bucket名字不符合需求,抛出IllegalArgumentException

**代码示例：**
```

		client.createBucket(Constants.BucketName,
				new CreateBucketResponceHandler() {
				@Override
				public void onSuccess(int statesCode,
						Header[] responceHeaders) {
				}

				@Override
				public void onFailure(int statesCode,
						Header[] responceHeaders, String response,
						Throwable throwable) {
				}
			}
		);
```

*创建一个新的Bucket，并携带AccessControlList设置权限*

**方法名：** 

public void createBucket(String bucketName,AccessControlList list,CreateBucketResponceHandler resultHandler) throw Ks3ClientException,Ks3ServiceException{}

**参数说明：**

* resultHandler：回调接口，包含onSuccess以及onFailure两个回调方法，运行在主线程
* bucketName：指定的Bucket名称
* list:传入的AccessControlList

**回调参数：**

* statesCode：Http请求返回的状态码，200表示请求成功，400表示客户端请求错误，403表示签名错误或本地日期时间错误，409表示Bucket已存在
* responceHeader:Http请求响应报头
* responce：失败时返回的响应正文
* throwable：出错时抛出的异常, bucketName不符合需求，list不符合规范，均抛出IllegalArgumentException

**代码示例：**

```

		 AccessControlList acl = new AccessControlList();
		 GranteeId grantee = new GranteeId() ;
		 grantee.setIdentifier("12773456");
		 grantee.setDisplayName("guoliTest222");
		 acl.addGrant(grantee, Permission.Read);

		 GranteeId grantee1 = new GranteeId() ;
		 grantee1.setIdentifier("123005789");
		 grantee1.setDisplayName("guoliTest2D2");
		 acl.addGrant(grantee1, Permission.Write);

		 client.createBucket(Constants.BucketName,acl,
				new CreateBucketResponceHandler() {
				@Override
				public void onSuccess(int statesCode,
						Header[] responceHeaders) {
				}

				@Override
				public void onFailure(int statesCode,
						Header[] responceHeaders, String response,
						Throwable throwable) {
				}
			}
		);
```

*创建一个新的Bucket，并携带CannedAccessControlList设置权限*

**方法名：** 

public void createBucket(String bucketName,CannedAccessControlList list,CreateBucketResponceHandler resultHandler) throw Ks3ClientException,Ks3ServiceException{}

**参数说明：**

* resultHandler：回调接口，包含onSuccess以及onFailure两个回调方法，运行在主线程
* bucketName：指定的Bucket名称
* list:传入的CannedAccessControlList

**回调参数：**

* statesCode：Http请求返回的状态码，200表示请求成功，400表示客户端请求错误，403表示签名错误或本地日期时间错误，409表示Bucket已存在
* responceHeader:Http请求响应报头
* responce：失败时返回的响应正文
* throwable：出错时抛出的异常, bucketName不符合需求，list不符合规范，均抛出IllegalArgumentException

**代码示例：**

```

		 CannedAccessControlList cannedAcl = CannedAccessControlList.PublicReadWrite;

		 client.createBucket(Constants.BucketName,cannedAcl,
				new CreateBucketResponceHandler() {
				@Override
				public void onSuccess(int statesCode,
						Header[] responceHeaders) {
				}

				@Override
				public void onFailure(int statesCode,
						Header[] responceHeaders, String response,
						Throwable throwable) {
				}
			}
		);
```

*创建一个新的Bucket*

**方法名：** 

public void createBucket(CreateBucketRequest request,CreateBucketResponceHandler resultHandler) throw Ks3ClientException,Ks3ServiceException{}

**参数说明：**

* resultHandler：回调接口，包含onSuccess以及onFailure两个回调方法，运行在主线程
* request：CreateBucketRequest对象

**回调参数：**

* statesCode：Http请求返回的状态码，200表示请求成功，400表示客户端请求错误，403表示签名错误或本地日期时间错误，409表示Bucket已存在
* responceHeader:Http请求响应报头
* responce：失败时返回的响应正文
* throwable：出错时抛出的异常, Bucket名字不符合需求,抛出IllegalArgumentException

**代码示例：**
```

		client.createBucket(new CreateBucketRequest(Constants.BucketName),
				new CreateBucketResponceHandler() {
				@Override
				public void onSuccess(int statesCode,
						Header[] responceHeaders) {
				}

				@Override
				public void onFailure(int statesCode,
						Header[] responceHeaders, String response,
						Throwable throwable) {
				}
			}
		);
```

####Delete Bucket:

*删除指定Bucket*

**方法名：** 

public void deleteBucket(String bucketname,DeleteBucketResponceHandler handler) throws Ks3ClientException,Ks3ServiceException{}

**参数说明：**

* resultHandler：回调接口，包含onSuccess以及onFailure两个回调方法，运行在主线程
* bucketName ：指定的Bucket名称

**回调参数：**

* statesCode：Http请求返回的状态码，204表示成功但是返回内容为空，400表示客户端请求错误，403表示签名错误或本地日期时间错误，404表示删除一个不存在的Bucket，409表示删除一个不为空的Bucket
* responceHeader:Http请求响应报头
* responce：失败时返回的响应正文
* throwable：出错时抛出的异常

**代码示例：**
```

		client.deleteBucket(Constants.BucketName,
				new DeleteBucketResponceHandler() {

				@Override
				public void onSuccess(int statesCode,
						Header[] responceHeaders) {
				}

				@Override
				public void onFailure(int statesCode,
						Header[] responceHeaders, String response,
						Throwable throwable) {
				}
			}
		);
```

*删除指定Bucket*

**方法名：** 

public void deleteBucket(DeleteBucketRequest request,DeleteBucketResponceHandler handler) throws Ks3ClientException,Ks3ServiceException{}

**参数说明：**

* resultHandler：回调接口，包含onSuccess以及onFailure两个回调方法，运行在主线程
* request：DeleteBucketRequest对象

**回调参数：**

* statesCode：Http请求返回的状态码，200表示请求成功，200表示请求成功，400表示客户端请求错误，403表示签名错误或本地日期时间错误，404表示删除一个不存在的Bucket，409表示删除一个不为空的Bucket，204表示成功但是返回内容为空
* responceHeader:Http请求响应报头
* responce：失败时返回的响应正文
* throwable：出错时抛出的异常

**代码示例：**
```

		client.deleteBucket(new DeleteBucketRequest（Constants.BucketName）,
				new DeleteBucketResponceHandler() {

				@Override
				public void onSuccess(int statesCode,
						Header[] responceHeaders) {
				}

				@Override
				public void onFailure(int statesCode,
						Header[] responceHeaders, String response,
						Throwable throwable) {
				}
			}
		);
```

####Get Bucket ACL:

*获取Bucket的ACL*

**方法名：** 

public void getBucketACL(String bucketName ,GetBucketACLResponceHandler resultHandler) throws Ks3ClientException, Ks3ServiceException{}

**参数说明：**

* resultHandler：回调接口，包含onSuccess以及onFailure两个回调方法，运行在主线程
* bucketName ：指定的Bucket名称

**回调参数：**

* statesCode：Http请求返回的状态码，200表示请求成功，400表示客户端请求错误，403表示签名错误或本地日期时间错误，404表示获取一个不存在Bucket的ACL
* responceHeader:Http请求响应报头
* responce：失败时返回的响应正文
* Throwable：出错时抛出的异常，Bucket名字不符合需求，抛出IllegalArgumentException
* accessControlPolicy ACL政策Model类，包括Owner信息以及ACL容器等

**代码示例：**
```

		client.getBucketACL(Constants.BucketName, new GetBucketACLResponceHandler() {
			@Override
			public void onSuccess(int statesCode, Header[] responceHeaders,
					AccessControlPolicy accessControlPolicy) {
				}

			@Override
			public void onFailure(int statesCode, Header[] responceHeaders,
					String response, Throwable paramThrowable) {
				}
			}
		);
```

*获取Bucket的ACL*

**方法名：** 

public void getBucketACL(GetBucketACLRequest request ,GetBucketACLResponceHandler resultHandler) throws Ks3ClientException, Ks3ServiceException{}

**参数说明：**

* resultHandler：回调接口，包含onSuccess以及onFailure两个回调方法，运行在主线程
* request：GetBucketACLRequest对象

**回调参数：**

* statesCode：Http请求返回的状态码，200表示请求成功，400表示客户端请求错误，403表示签名错误或本地日期时间错误，404表示获取一个不存在Bucket的ACL
* responceHeader:Http请求响应报头
* responce：失败时返回的响应正文
* Throwable：出错时抛出的异常，Bucket名字不符合需求，抛出IllegalArgumentException
* accessControlPolicy ACL政策Model类，包括Owner信息以及ACL容器等

**代码示例：**
```

		client.getBucketACL(new GetBucketACLRequest(Constants.BucketName), new GetBucketACLResponceHandler() {
			@Override
			public void onSuccess(int statesCode, Header[] responceHeaders,
					AccessControlPolicy accessControlPolicy) {
				}

			@Override
			public void onFailure(int statesCode, Header[] responceHeaders,
					String response, Throwable paramThrowable) {
				}
			}
		);
```

####Put Bucket ACL:

*设置Bucket的ACL，以AccessControlList形式*

**方法名：** 

public void putBucketACL(String bucketName, AccessControlList accessControlList, PutBucketACLResponseHandler resultHandler) throws Ks3ClientException,Ks3ServiceException{}

**参数说明：**

* resultHandler：回调接口，包含onSuccess以及onFailure两个回调方法，运行在主线程
* request：请求封装类，需要添加要上传的ACL容器
* accessControlList:传入的AccessControlList对象

**回调参数：**

* statesCode：Http请求返回的状态码，200表示请求成功，但会清空原有ACL权限，只保留当前设置，400表示客户端请求错误，403表示签名错误或本地日期时间错误，404表示给一个不存在的Bucket设置ACL
* responceHeader:Http请求响应报头  
* responce：失败时返回的响应正文
* Throwable：出错时抛出的异常，为存在的Bucket设置空ACL时，参数检查时抛出异常，均抛出IllegalArgumentException

**代码示例：**
```

		AccessControlList acl = new AccessControlList();
	    GranteeId grantee = new GranteeId();
		grantee.setIdentifier("12773456");
		grantee.setDisplayName("guoliTest222");
		acl.addGrant(grantee, Permission.Read);

		client.putBucketACL(bucketName，acl, new PutBucketACLResponseHandler() {
			@Override
			public void onSuccess(int statesCode, Header[] responceHeaders) {
				}

			@Override
			public void onFailure(int statesCode, Header[] responceHeaders,
					String response, Throwable paramThrowable) {
				}
			}
		);

```

*设置Bucket的ACL，以CannedAccessControlList形式*

**方法名：** 

public void putBucketACL(String bucketName,CannedAccessControlList CannedAcl,PutBucketACLResponseHandler resultHandler) throws Ks3ClientException,Ks3ServiceException{}

**参数说明：**

* resultHandler：回调接口，包含onSuccess以及onFailure两个回调方法，运行在主线程
* request ：请求封装类，需要添加要上传的ACL容器

**回调参数：**

* statesCode：Http请求返回的状态码，200表示请求成功，但会清空原有ACL权限，只保留当前设置，400表示客户端请求错误，403表示签名错误或本地日期时间错误，404表示给一个不存在的Bucket设置ACL
* responceHeader:Http请求响应报头  
* responce：失败时返回的响应正文
* Throwable：出错时抛出的异常，为存在的Bucket设置空ACL时，参数检查时抛出异常，均抛出IllegalArgumentException

**代码示例：**
```

		CannedAccessControlList cannedAcl = CannedAccessControlList.PublicReadWrite;
		client.putBucketACL(bucketName, cannedAcl，new PutBucketACLResponseHandler() {

			@Override
			public void onSuccess(int statesCode, Header[] responceHeaders) {
				}

			@Override
			public void onFailure(int statesCode, Header[] responceHeaders,
					String response, Throwable paramThrowable) {
				}
			}
		);

```

*设置Bucket的ACL*

**方法名：** 

public void putBucketACL(PutBucketACLRequest requset,PutBucketACLResponseHandler resultHandler) throws Ks3ClientException,Ks3ServiceException{}

**参数说明：**

* resultHandler：回调接口，包含onSuccess以及onFailure两个回调方法，运行在主线程
* request ：请求封装类，需要添加要上传的ACL容器

**回调参数：**

* statesCode：Http请求返回的状态码，200表示请求成功，但会清空原有ACL权限，只保留当前设置，400表示客户端请求错误，403表示签名错误或本地日期时间错误，404表示给一个不存在的Bucket设置ACL
* responceHeader:Http请求响应报头  
* responce：失败时返回的响应正文
* Throwable：出错时抛出的异常，为存在的Bucket设置空ACL时，参数检查时抛出异常，均抛出IllegalArgumentException

**代码示例：**
```
	
		client.putBucketACL(new PutBucketACLRequest(bucketName,cannedAcl), new PutBucketACLResponseHandler() {
			@Override
			public void onSuccess(int statesCode, Header[] responceHeaders) {
				}

			@Override
			public void onFailure(int statesCode, Header[] responceHeaders,
					String response, Throwable paramThrowable) {
				}
			}
		);

```

####Head Bucket：

*查询是否已经存在指定Bucket*

**方法名：** 

public void headBucket(String bucketname,HeadBucketResponseHandler resultHandler) throws Ks3ClientException,Ks3ServiceException{}

**参数说明：**

* resultHandler：回调接口，包含onSuccess以及onFailure两个回调方法，运行在主线程
* bucketname：指定的Bucket名称

**回调参数：**

* statesCode：Http请求返回的状态码，200表示请求成功，400表示客户端请求错误，403表示签名错误或本地日期时间错误，404表示请求一个不存在的Bucket
* responceHeader:Http请求响应报头
* responce：失败时返回的响应正文
* Throwable：出错时抛出的异常,Bucket名字不符合需求，抛出IllegalArgumentException

**代码示例：**
```

		client.headBucket(Constants.BucketName,
				new HeadBucketResponseHandler() {

					@Override
					public void onSuccess(int statesCode,
							Header[] responceHeaders) {
					}

					@Override
					public void onFailure(int statesCode,
							Header[] responceHeaders, String response,
							Throwable paramThrowable) {
					}
				}
		);

```

*查询是否已经存在指定Bucket*

**方法名：** 

public void headBucket(HeadBucketRequest request,HeadBucketResponseHandler resultHandler) throws Ks3ClientException,Ks3ServiceException{}

**参数说明：**

* resultHandler：回调接口，包含onSuccess以及onFailure两个回调方法，运行在主线程
* request：HeadBucketRequest对象

**回调参数：**

* statesCode：Http请求返回的状态码，200表示请求成功，400表示客户端请求错误，403表示签名错误或本地日期时间错误，404表示请求一个不存在的Bucket
* responceHeader:Http请求响应报头
* responce：失败时返回的响应正文
* Throwable：出错时抛出的异常,Bucket名字不符合需求，抛出IllegalArgumentException

**代码示例：**
```

		client.headBucket(new HeadBucketRequest（Constants.BucketName,
				new HeadBucketResponseHandler() {

					@Override
					public void onSuccess(int statesCode,
							Header[] responceHeaders) {
					}

					@Override
					public void onFailure(int statesCode,
							Header[] responceHeaders, String response,
							Throwable paramThrowable) {
					}
				}
		);

```
###Object操作
####Get Object：

*下载该Object数据*  
**方法名：** 

public Ks3HttpRequest getObject(Context context, String bucketname, String key, GetObjectResponceHandler getObjectResponceHandler（File file,String bucketname,String objectKey）)throws Ks3ClientException, Ks3ServiceException{}

**参数说明：**

* resultHandler：回调接口，包含onSuccess以及onFailure两个回调方法，运行在主线程
* context：传入的上下文
* bucketname：指定的Bucket名称
* objectKey：指定的Object键
* file:指定生成后写入的File文件

**回调参数：**

* statesCode：Http请求返回的状态码，200表示请求成功，400表示客户端请求错误，403表示签名错误或本地日期时间错误，404表示指定的Bucket或者Object不存在
* responceHeader:Http请求响应报头
* responce：失败时返回的响应正文
* throwable：出错时抛出的异常
* file:成功后写入的文件
* object:成功后对应的Ks3Object实体类

**代码示例：**
```

		client.getObject(DummyActivity.this,Constants.BucketName,Constants.ObjectKey,
				new GetObjectResponceHandler(new File(Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
					Constants.ObjectKey), Constants.BucketName,
						Constants.ObjectKey) {

					@Override
					public void onTaskSuccess(int statesCode, Header[] responceHeaders) {	
					}
						
					@Override
					public void onTaskFailure(int statesCode, Header[] responceHeaders,
							String response, Throwable throwable) {
					}
	
					@Override
					public  void onTaskFinish(){
					
					}

					@Override
					public  void onTaskStart(){
					
					}
					
					@Override
					public void onTaskProgress(double progress){
					//运行在非UI线程，更新UI时需要注意
					//Progress为0-100之间的double类型数值
					}
				}
		);

```

*下载该Object数据*  

**方法名：** 

public Ks3HttpRequest getObject(GetObjectRequest request, GetObjectResponceHandler getObjectResponceHandler（File file,String bucketname,String objectKey）)throws Ks3ClientException, Ks3ServiceException{}

**参数说明：**

* resultHandler：回调接口，包含onSuccess以及onFailure两个回调方法，运行在主线程
* request：GetObjectRequest对象

**回调参数：**

* statesCode：Http请求返回的状态码，200表示请求成功，400表示客户端请求错误，403表示签名错误或本地日期时间错误，404表示指定的Bucket或者Object不存在
* responceHeader:Http请求响应报头
* responce：失败时返回的响应正文
* throwable：出错时抛出的异常
* file:成功后写入的文件
* object:成功后对应的Ks3Object实体类

**代码示例：**
```

		client.getObject(new GetObjectRequest(bucketname, key),
				new GetObjectResponceHandler(new File(Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
					Constants.ObjectKey), Constants.BucketName,
						Constants.ObjectKey) {

					@Override
					public void onTaskSuccess(int statesCode, Header[] responceHeaders) {	
					}
						
					@Override
					public void onTaskFailure(int statesCode, Header[] responceHeaders,
							String response, Throwable throwable) {
					}
	
					@Override
					public  void onTaskFinish(){
					
					}

					@Override
					public  void onTaskStart(){
					
					}
					
					@Override
					public void onTaskProgress(double progress){
					//运行在非UI线程，更新UI时需要注意
					//Progress为0-100之间的double类型数值
					}
				}
		);

```

####Head Object：

*查询是否已经存在指定Object*

**方法名：** 

public void headObject(String bucketname, String objectkey,HeadObjectResponseHandler resultHandler)throws Ks3ClientException, Ks3ServiceException;

**参数说明：**

* resultHandler：回调接口，包含onSuccess以及onFailure两个回调方法，运行在主线程
* bucketname：指定的Bucket名称
* objectKey：指定的Object键

**回调参数：**

* statesCode：Http请求返回的状态码，200表示请求成功，400表示客户端请求错误，403表示签名错误或本地日期时间错误，404表示指定的Bucket或者Object不存在
* headObjectResult:返回Object元数据封装类，包含元数据信息，Etag以及上次修改时间
* responceHeader:Http请求响应报头
* responce：失败时返回的响应正文
* throwable：出错时抛出的异常，Bucket名字不符合需求，抛出IllegalArgumentException

**代码示例：**
```

		client.headObject(Constants.BucketName,Constants.ObjectKey,
				new HeadObjectResponseHandler() {

					@Override
					public void onSuccess(int statesCode,
							Header[] responceHeaders,
							HeadObjectResult headObjectResult) {
					}

					@Override
					public void onFailure(int statesCode,
							Header[] responceHeaders, String response,
							Throwable throwable) {
					}
				}
		);
```
*查询是否已经存在指定Object*

**方法名：** 

public void headObject(HeadObjectRequest request,HeadObjectResponseHandler resultHandler)throws Ks3ClientException, Ks3ServiceException;

**参数说明：**

* resultHandler：回调接口，包含onSuccess以及onFailure两个回调方法，运行在主线程
* request：HeadObjectRequest对象

**回调参数：**

* statesCode：Http请求返回的状态码，200表示请求成功，400表示客户端请求错误，403表示签名错误或本地日期时间错误，404表示指定的Bucket或者Object不存在
* headObjectResult:返回Object元数据封装类，包含元数据信息，Etag以及上次修改时间
* responceHeader:Http请求响应报头
* responce：失败时返回的响应正文
* throwable：出错时抛出的异常，Bucket名字不符合需求，抛出IllegalArgumentException

**代码示例：**
```

		client.headObject(new HeadObjectRequest（Constants.BucketName,Constants.ObjectKey）
				new HeadObjectResponseHandler() {

					@Override
					public void onSuccess(int statesCode,
							Header[] responceHeaders,
							HeadObjectResult headObjectResult) {
					}

					@Override
					public void onFailure(int statesCode,
							Header[] responceHeaders, String response,
							Throwable throwable) {
					}
				}
		);
```

####Delete Object：

*删除指定Object*

**方法名：** 

public void deleteObject(String bucketname, String objectKey , DeleteObjectRequestHandler handler)
throws Ks3ClientException, Ks3ServiceException{}

**参数说明：**

* resultHandler：回调接口，包含onSuccess以及onFailure两个回调方法，运行在主线程
* bucketname：指定的Bucket名称
* objectKey：指定的Object键

**回调参数：**

* statesCode：Http请求返回的状态码，204表示成功但返回内容为空,400表示客户端请求错误，403表示签名错误或本地日期时间错误，404表示删除一个不存在的Bucket或Object
* responceHeader:Http请求响应报头
* responce：失败时返回的响应正文
* throwable：出错时抛出的异常，Bucket名字不符合需求，抛出IllegalArgumentException

**代码示例：**
```

		client.deleteObject(Constants.BucketName,Constants.ObjectKey, new DeleteObjectRequestHandler() {

				@Override
				public void onSuccess(int statesCode, Header[] responceHeaders) {
				}

				@Override
				public void onFailure(int statesCode, Header[] responceHeaders,
						String response, Throwable throwable) {
				}
			}
		);
```

*删除指定Object*

**方法名：** 

public void deleteObject(DeleteObjectRequest request, DeleteObjectRequestHandler handler)
throws Ks3ClientException, Ks3ServiceException{}

**参数说明：**

* resultHandler：回调接口，包含onSuccess以及onFailure两个回调方法，运行在主线程
* bucketname：指定的Bucket名称
* request：DeleteObjectRequest对象

**回调参数：**

* statesCode：Http请求返回的状态码，200表示请求成功，400表示客户端请求错误，403表示签名错误或本地日期时间错误，404表示删除一个不存在的Bucket,204表示成功但返回内容为空
* responceHeader:Http请求响应报头
* responce：失败时返回的响应正文
* throwable：出错时抛出的异常，Bucket名字不符合需求，抛出IllegalArgumentException

**代码示例：**
```

		client.deleteObject(new DeleteObjectRequest（Constants.BucketName,Constants.ObjectKey),new DeleteObjectRequestHandler() {

				@Override
				public void onSuccess(int statesCode, Header[] responceHeaders) {
				}

				@Override
				public void onFailure(int statesCode, Header[] responceHeaders,
						String response, Throwable throwable) {
				}
			}
		);
```

####Get Object ACL：

*获得Object的acl*

**方法名：** 

public void getObjectACL(String bucketName, String ObjectName , GetObjectACLResponseHandler resultHandler)throws Ks3ClientException, Ks3ServiceException{}

**参数说明：**

* resultHandler：回调接口，包含onSuccess以及onFailure两个回调方法，运行在主线程
* bucketname：指定的Bucket名称
* objectKey：指定的Object键

**回调参数：**

* statesCode：Http请求返回的状态码，200表示请求成功，400表示客户端请求错误，403表示签名错误或本地日期时间错误，404表示获取一个不存在Object的ACL
* responceHeader:Http请求响应报头
* responce：失败时返回的响应正文
* Throwable：出错时抛出的异常,Bucket名字不符合需求，抛出IllegalArgumentException
* accessControlPolicy ACL政策Model类，包括Owner信息以及ACL容器等

**代码示例：**
```

		client.getObjectACL(Constants.BucketName,Constants.ObjectKey,
				new GetObjectACLResponseHandler() {

					@Override
					public void onSuccess(int statesCode,
							Header[] responceHeaders,
							AccessControlPolicy accessControlPolicy) {
					}

					@Override
					public void onFailure(int statesCode,
							Header[] responceHeaders, String response,
							Throwable paramThrowable) {
					}
				}
		);

```

*获得Object的acl*

**方法名：** 

public void getObjectACL(GetObjectACLRequest request, GetObjectACLResponseHandler resultHandler)throws Ks3ClientException, Ks3ServiceException{}

**参数说明：**

* resultHandler：回调接口，包含onSuccess以及onFailure两个回调方法，运行在主线程
* request:GetObjectACLRequest对象

**回调参数：**

* statesCode：Http请求返回的状态码，200表示请求成功，400表示客户端请求错误，403表示签名错误或本地日期时间错误，404表示获取一个不存在Object的ACL
* responceHeader:Http请求响应报头
* responce：失败时返回的响应正文
* Throwable：出错时抛出的异常,Bucket名字不符合需求，抛出IllegalArgumentException
* accessControlPolicy ACL政策Model类，包括Owner信息以及ACL容器等

**代码示例：**
```

		client.getObjectACL(new GetObjectACLRequest(Constants.BucketName,Constants.ObjectKey),
				new GetObjectACLResponseHandler() {

					@Override
					public void onSuccess(int statesCode,
							Header[] responceHeaders,
							AccessControlPolicy accessControlPolicy) {
					}

					@Override
					public void onFailure(int statesCode,
							Header[] responceHeaders, String response,
							Throwable paramThrowable) {
					}
				}
		);

```

####Put Object ACL:

*上传object的acl，以CannedAccessControlList形式*

**方法名：** 

public void putObjectACL(String bucketName, String objectKey,CannedAccessControlList list, PutObjectACLResponseHandler resultHandler)
throws Ks3ClientException, Ks3ServiceException{}

**参数说明：**

* resultHandler：回调接口，包含onSuccess以及onFailure两个回调方法，运行在主线程
* request：请求封装类，需要添加要上传的ACL容器
* list:CannedAccessControlList对象

**回调参数：**

* statesCode：Http请求返回的状态码，200表示请求成功，400表示客户端请求错误，403表示签名错误或本地日期时间错误，404表示给一个不存在的Obejct设置ACL，
* responceHeader:Http请求响应报头
* responce：失败时返回的响应正文
* throwable：出错时抛出的异常，为存在的Object设置空ACL时,Bucket名字不符合需求，均抛出IllegalArgumentException

**代码示例：**
```

		CannedAccessControlList list = CannedAccessControlList.PublicRead;
		client.putObjectACL(bucketName, objectKey, list, new PutObjectACLResponseHandler() {
			@Override
			public void onSuccess(int statesCode, Header[] responceHeaders) {
				}

			@Override
			public void onFailure(int statesCode, Header[] responceHeaders,
					String response, Throwable throwable) {
				}
			}
		);
```

*上传object的acl，以AccessControlList形式*

**方法名：** 

public void putObjectACL(String bucketName, String objectName,AccessControlList list,PutObjectACLResponseHandler resultHandler)
throws Ks3ClientException, Ks3ServiceException{}

**参数说明：**

* resultHandler：回调接口，包含onSuccess以及onFailure两个回调方法，运行在主线程
* list：AccessControlList对象

**回调参数：**

* statesCode：Http请求返回的状态码，200表示请求成功，400表示客户端请求错误，403表示签名错误或本地日期时间错误，404表示给一个不存在的Obejct设置ACL，
* responceHeader:Http请求响应报头
* responce：失败时返回的响应正文
* throwable：出错时抛出的异常，为存在的Object设置空ACL时,Bucket名字不符合需求，均抛出IllegalArgumentException

**代码示例：**
```

		AccessControlList acList = new AccessControlList();
		GranteeId grantee = new GranteeId();
		grantee.setIdentifier("123456");
		grantee.setDisplayName("TESTTEST1");
		acList.addGrant(grantee, Permission.Read);
		client.putObjectACL(bucketName, objectKey, list, new PutObjectACLResponseHandler() {
			@Override
			public void onSuccess(int statesCode, Header[] responceHeaders) {
				}

			@Override
			public void onFailure(int statesCode, Header[] responceHeaders,
					String response, Throwable throwable) {
				}
			}
		);
```

*上传object的acl*

**方法名：** 

public void putObjectACL(PutObjectACLRequest request, PutObjectACLResponseHandler resultHandler)
throws Ks3ClientException, Ks3ServiceException{}

**参数说明：**

* resultHandler：回调接口，包含onSuccess以及onFailure两个回调方法，运行在主线程
* request：PutObjectACLRequest对象

**回调参数：**

* statesCode：Http请求返回的状态码，200表示请求成功，400表示客户端请求错误，403表示签名错误或本地日期时间错误，404表示给一个不存在的Obejct设置ACL
* responceHeader:Http请求响应报头
* responce：失败时返回的响应正文
* throwable：出错时抛出的异常，为存在的Object设置空ACL时,Bucket名字不符合需求，均抛出IllegalArgumentException

**代码示例：**
```
		CannedAccessControlList list = CannedAccessControlList.PublicRead;
		client.putObjectACL(new PutObjectACLRequest(bucketName, objectKey, list), new PutObjectACLResponseHandler() {

			@Override
			public void onSuccess(int statesCode, Header[] responceHeaders) {
				}

			@Override
			public void onFailure(int statesCode, Header[] responceHeaders,
					String response, Throwable throwable) {
				}
			}
		);
```

####List Objects：

*列举Bucket内的Object*

**方法名：** 

public void listObjects(String bucketname,ListObjectsResponseHandler resultHandler)
throws Ks3ClientException, Ks3ServiceException{}

**参数说明：**

* resultHandler：回调接口，包含onSuccess以及onFailure两个回调方法，运行在主线程
* bucketname：指定的Bucket名称

**回调参数：**

* statesCode：Http请求返回的状态码，200表示请求成功，400表示客户端请求错误，403表示签名错误或本地日期时间错误，404表示Bucket不存在，
* responceHeader:Http请求响应报头
* responce：失败时返回的响应正文
* throwable：出错时抛出的异常
* objectListing:成功时返回的指定Bucket下所有的Object ummary信息实体类，包含一个Ks3ObjectSummary的容器及其他信息

**代码示例：**
````

		client.listObjects(Constants.BucketName, new ListObjectsResponseHandler() {

			@Override
			public void onSuccess(int statesCode, Header[] responceHeaders,
					ObjectListing objectListing) {
			}

			@Override
			public void onFailure(int statesCode, Header[] responceHeaders,
					String response, Throwable throwable) {
			}
		}
		);
````

*列举Bucket内的Object*

**方法名：** 

public void listObjects(String bucketname,String prefix,ListObjectsResponseHandler resultHandler)
throws Ks3ClientException, Ks3ServiceException{}

**参数说明：**

* resultHandler：回调接口，包含onSuccess以及onFailure两个回调方法，运行在主线程
* bucketname：指定的Bucket名称
* prefix:delimiter是用来对Object名字进行分组的一个字符。包含指定的前缀到第一次出现的delimiter字符的所有Object名字作为一组结果CommonPrefix

**回调参数：**

* statesCode：Http请求返回的状态码，200表示请求成功，400表示客户端请求错误，403表示签名错误或本地日期时间错误，404表示Bucket不存在，
* responceHeader:Http请求响应报头
* responce：失败时返回的响应正文
* throwable：出错时抛出的异常
* objectListing:成功时返回的指定Bucket下所有的Object Summary信息实体类

**代码示例：**
````

		client.listObjects(Constants.BucketName,Constants.Prefix, new ListObjectsResponseHandler() {

			@Override
			public void onSuccess(int statesCode, Header[] responceHeaders,
					ObjectListing objectListing) {
			}

			@Override
			public void onFailure(int statesCode, Header[] responceHeaders,
					String response, Throwable throwable) {
			}
		}
		);
````

*列举Bucket内的Object*

**方法名：** 

public void listObjects(String bucketName, String prefix, String marker,
String delimiter, Integer maxKeys, ListObjectsResponseHandler resultHandler)
throws Ks3ClientException, Ks3ServiceException{}

**参数说明：**

* resultHandler：回调接口，包含onSuccess以及onFailure两个回调方法，运行在主线程
* bucketname：指定的Bucket名称
* prefix:限定返回的Object名字都以制定的prefix前缀开始
* marker:从一个指定的名字marker开始列出Object的名字
* maxKeys:设定返回的Object名字数量，返回的数量有可能比设定的少，但是绝不会比设定的多，如果还存在没有返回的Object名字，返回的结果包含<IsTruncated>true</IsTruncated>
* delimiter:delimiter是用来对Object名字进行分组的一个字符。包含指定的前缀到第一次出现的delimiter字符的所有Object名字作为一组结果CommonPrefix

**回调参数：**

* statesCode：Http请求返回的状态码，200表示请求成功，400表示客户端请求错误，403表示签名错误或本地日期时间错误，404表示Bucket不存在
* responceHeader:Http请求响应报头
* responce：失败时返回的响应正文
* throwable：出错时抛出的异常
* objectListing:成功时返回的指定Bucket下所有的Object Summary信息实体类

**代码示例：**
````

		client.listObjects(new ListObjectsRequest(bucketname,prefix,delimiter,maxKeys), new ListObjectsResponseHandler() {

			@Override
			public void onSuccess(int statesCode, Header[] responceHeaders,
					ObjectListing objectListing) {
			}

			@Override
			public void onFailure(int statesCode, Header[] responceHeaders,
					String response, Throwable throwable) {
			}
		}
		);
````

*列举Bucket内的Object*

**方法名：** 

public void listObjects(ListObjectsRequest request,ListObjectsResponseHandler resultHandler)
throws Ks3ClientException, Ks3ServiceException{}

**参数说明：**

* resultHandler：回调接口，包含onSuccess以及onFailure两个回调方法，运行在主线程
* request：ListObjectsRequest对象

**回调参数：**

* statesCode：Http请求返回的状态码，200表示请求成功，400表示客户端请求错误，403表示签名错误或本地日期时间错误，404表示Bucket不存在
* responceHeader:Http请求响应报头
* responce：失败时返回的响应正文
* throwable：出错时抛出的异常
* objectListing:成功时返回的指定Bucket下所有的Object Summary信息实体类

**代码示例：**
````

		client.listObjects(new ListObjectsRequest(bucketname, prefix), new ListObjectsResponseHandler() {

			@Override
			public void onSuccess(int statesCode, Header[] responceHeaders,
					ObjectListing objectListing) {
			}

			@Override
			public void onFailure(int statesCode, Header[] responceHeaders,
					String response, Throwable throwable) {
			}
		}
		);
````

####Put Object：

*上传Object数据*

**方法名：** 

public Ks3HttpRequest PutObject(String bucketname, String objectkey,
File file , PutObjectResponseHandler handler) throws Ks3ClientException, Ks3ServiceException;


**参数说明：**

* resultHandler：回调接口，包含onSuccess以及onFailure两个回调方法，运行在主线程
* bucketname：指定的Bucket名称
* objectKey：指定的Object键
* file：需要上传的file

**回调参数：**

* statesCode：Http请求返回的状态码，200表示请求成功，400表示客户端请求错误，403表示签名错误或本地日期时间错误
* responceHeader:Http请求响应报头
* responce：失败时返回的响应正文
* throwable：出错时抛出的异常

**代码示例：**
```

		client.PutObject(bucketname, objectkey, file, new PutObjectResponseHandler() {
						
					@Override
					public void onTaskSuccess(int statesCode, Header[] responceHeaders) {	
					}
						
					@Override
					public void onTaskFailure(int statesCode, Header[] responceHeaders,
							String response, Throwable throwable) {
					}
	
					@Override
					public  void onTaskFinish(){
					
					}

					@Override
					public  void onTaskStart(){
					
					}
					
					@Override
					public void onTaskProgress(double progress){
					//运行在非UI线程，更新UI时需要注意
					//Progress为0-100之间的double类型数值
					}
				}
		);

```

*上传Object数据*

**方法名：** 

public Ks3HttpRequest PutObject(PutObjectRequest request, PutObjectResponseHandler handler) throws Ks3ClientException, Ks3ServiceException;

**参数说明：**

* resultHandler：回调接口，包含onSuccess以及onFailure两个回调方法，运行在主线程
* request:PutObjectRequest对象

**回调参数：**

* statesCode：Http请求返回的状态码，200表示请求成功，400表示客户端请求错误，403表示签名错误或本地日期时间错误
* responceHeader:Http请求响应报头
* responce：失败时返回的响应正文
* throwable：出错时抛出的异常

**代码示例：**
```

		client.PutObject(new PutObjectRequest(bucketname, objectkey, file), new PutObjectResponseHandler() {
						
					@Override
					public void onTaskSuccess(int statesCode, Header[] responceHeaders) {	
					}
						
					@Override
					public void onTaskFailure(int statesCode, Header[] responceHeaders,
							String response, Throwable throwable) {
					}
	
					@Override
					public  void onTaskFinish(){
					
					}

					@Override
					public  void onTaskStart(){
					
					}
					
					@Override
					public void onTaskProgress(double progress){
					//运行在非UI线程，更新UI时需要注意
					//Progress为0-100之间的double类型数值
					}
				}
		);

```

#####Copy Object：

*复制Object*

**方法名：** 

public void copyObject(String destinationBucket, String destinationObject,String sourceBucket, String sourceKey,
CopyObjectResponseHandler resultHandler) throws Ks3ClientException,Ks3ServiceException {}

**参数说明：**

* resultHandler：回调接口，包含onSuccess以及onFailure两个回调方法，运行在主线程
* destinationBucket：需要复制到此Bucket之下
* destinationObject：复制到指定Bucket之后的ObjectKey
* sourceBucket:源BucketName
* sourceKey:源ObjectKey

**回调参数：**

* statesCode：Http请求返回的状态码，200表示请求成功，400表示客户端请求错误，403表示签名错误或本地日期时间错误
* responceHeader:Http请求响应报头
* responce：失败时返回的响应正文
* throwable：出错时抛出的异常
* result:成功时返回的Copy结果信息实体类

**代码示例：**
````

		client.copyObject("ks3-sdk-test", "object-source", "eflake", "object-copy", new CopyObjectResponseHandler() {
			
			@Override
			public void onSuccess(int statesCode, Header[] responceHeaders,
					CopyResult result) {
				
			}
			
			@Override
			public void onFailure(int statesCode, Header[] responceHeaders,
					String response, Throwable throwable) {
				
			}
		});
```

*复制Object*

**方法名：** 

public void copyObject(String destinationBucket, String destinationObject,String sourceBucket, String sourceKey,
CannedAccessControlList cannedAcl, CopyObjectResponseHandler resultHandler) throws Ks3ClientException,Ks3ServiceException {}

**参数说明：**

* resultHandler：回调接口，包含onSuccess以及onFailure两个回调方法，运行在主线程
* destinationBucket：需要复制到此Bucket之下
* destinationObject：复制到指定Bucket之后的ObjectKey
* sourceBucket:源BucketName
* sourceKey:源ObjectKey
* cannedAcl:传入的acl参数

**回调参数：**

* statesCode：Http请求返回的状态码，200表示请求成功，400表示客户端请求错误，403表示签名错误或本地日期时间错误
* responceHeader:Http请求响应报头
* responce：失败时返回的响应正文
* throwable：出错时抛出的异常
* result:成功时返回的Copy结果信息实体类

**代码示例：**

````

		CannedAccessControlList cannedAcl = CannedAccessControlList.PublicRead;
		client.copyObject("ks3-sdk-test", "object-source", "eflake", "object-copy",cannedAcl, new CopyObjectResponseHandler() {
			
			@Override
			public void onSuccess(int statesCode, Header[] responceHeaders,
					CopyResult result) {
				
			}
			
			@Override
			public void onFailure(int statesCode, Header[] responceHeaders,
					String response, Throwable throwable) {
				
			}
		});
````

*复制Object*

**方法名：** 

public void copyObject(String destinationBucket, String destinationObject,String sourceBucket, String sourceKey,
AccessControlList accessControlList, CopyObjectResponseHandler resultHandler) throws Ks3ClientException,Ks3ServiceException {}

**参数说明：**

* resultHandler：回调接口，包含onSuccess以及onFailure两个回调方法，运行在主线程
* destinationBucket：需要复制到此Bucket之下
* destinationObject：复制到指定Bucket之后的ObjectKey
* sourceBucket:源BucketName
* sourceKey:源ObjectKey
* accessControlList:传入的acl参数

**回调参数：**

* statesCode：Http请求返回的状态码，200表示请求成功，400表示客户端请求错误，403表示签名错误或本地日期时间错误
* responceHeader:Http请求响应报头
* responce：失败时返回的响应正文
* throwable：出错时抛出的异常
* result:成功时返回的Copy结果信息实体类

**代码示例：**

````

		AccessControlList acList = new AccessControlList();
		GranteeId grantee = new GranteeId();
		grantee.setIdentifier("123456");
		grantee.setDisplayName("TESTTEST1");
		acList.addGrant(grantee, Permission.Read);
		client.copyObject("ks3-sdk-test", "object-source", "eflake", "object-copy",acList, new CopyObjectResponseHandler() {
			
			@Override
			public void onSuccess(int statesCode, Header[] responceHeaders,
					CopyResult result) {
				
			}
			
			@Override
			public void onFailure(int statesCode, Header[] responceHeaders,
					String response, Throwable throwable) {
				
			}
		});
````

*复制Object*

**方法名：** 

public void copyObject(CopyObjectRequest request,CopyObjectResponseHandler resultHandler) throws Ks3ClientException,Ks3ServiceException {}

**参数说明：**

* resultHandler：回调接口，包含onSuccess以及onFailure两个回调方法，运行在主线程
* requset:request请求对象

**回调参数：**

* statesCode：Http请求返回的状态码，200表示请求成功，400表示客户端请求错误，403表示签名错误或本地日期时间错误
* responceHeader:Http请求响应报头
* responce：失败时返回的响应正文
* throwable：出错时抛出的异常
* result:成功时返回的Copy结果信息实体类

**代码示例：**

````
		
		CopyObjectRequest request = new CopyObjectRequest(destinationBucket,
				destinationObject, sourceBucket, sourceKey);
		client.copyObject(request, new CopyObjectResponseHandler() {
			
			@Override
			public void onSuccess(int statesCode, Header[] responceHeaders,
					CopyResult result) {
				
			}
			
			@Override
			public void onFailure(int statesCode, Header[] responceHeaders,
					String response, Throwable throwable) {
				
			}
		});
````

####Initiate Multipart Upload：
 
*调用这个接口会初始化一个分块上传，KS3 Server会返回一个upload id, upload id 用来标识属于当前object的具体的块，并且用来标识完成分块上传或者取消分块上传*

**方法名：** 

public void initiateMultipartUpload(String bucketname, String objectkey, InitiateMultipartUploadResponceHandler resultHandler) throws Ks3ClientException,
Ks3ServiceException;


**参数说明：**

* resultHandler：回调接口，包含onSuccess以及onFailure两个回调方法，运行在主线程
* bucketname：指定的Bucket名称
* objectKey：指定的Object键

**回调参数：**

* statesCode：Http请求返回的状态码，200表示请求成功，400表示客户端请求错误，403表示签名错误或本地日期时间错误
* responceHeader:Http请求响应报头
* responce：失败时返回的响应正文
* result：成功时返回的初始化分片上传结果实体类，包含uploadId信息等

**代码示例：**
```

	client.initiateMultipartUpload(bucketName, objectKey,
				new InitiateMultipartUploadResponceHandler() {

					@Override
					public void onFailure(int statesCode,
							Header[] responceHeaders, String response,
							Throwable paramThrowable) {
					}

					@Override
					public void onSuccess(int statesCode,
							Header[] responceHeaders,
							InitiateMultipartUploadResult result) {
						String uploadId = result.getUploadId();
				});

```

*调用这个接口会初始化一个分块上传，KS3 Server会返回一个upload id, upload id 用来标识属于当前object的具体的块，并且用来标识完成分块上传或者取消分块上传*

**方法名：** 

public void initiateMultipartUpload(InitiateMultipartUploadRequest request, InitiateMultipartUploadResponceHandler resultHandler) throws Ks3ClientException,
Ks3ServiceException;


**参数说明：**

* resultHandler：回调接口，包含onSuccess以及onFailure两个回调方法，运行在主线程
* request:InitiateMultipartUploadRequest对象

**回调参数：**

* statesCode：Http请求返回的状态码，200表示请求成功，400表示客户端请求错误，403表示签名错误或本地日期时间错误
* responceHeader:Http请求响应报头
* responce：失败时返回的响应正文
* result：成功时返回的初始化分片上传结果实体类，包含uploadId信息等

**代码示例：**
```

	client.initiateMultipartUpload(new InitiateMultipartUploadRequest(
				bucketName, objectKey),
				new InitiateMultipartUploadResponceHandler() {

					@Override
					public void onFailure(int statesCode,
							Header[] responceHeaders, String response,
							Throwable paramThrowable) {
					}

					@Override
					public void onSuccess(int statesCode,
							Header[] responceHeaders,
							InitiateMultipartUploadResult result) {
						String uploadId = result.getUploadId();
				});

```

####Upload Part：

*初始化分块上传后，上传分块接口。Part number 是标识每个分块的数字，介于0-10000之间。除了最后一块，每个块必须大于等于5MB，最后一块没有这个限制。*

**方法名：** 

public void uploadPart(String bucketName, String key, String uploadId,File file, long offset, int partNumber, long partSize,UploadPartResponceHandler resultHandler)throws Ks3ClientException, Ks3ServiceException{}

**参数说明：**

* resultHandler：回调接口，包含onSuccess以及onFailure两个回调方法，运行在主线程
* request：请求封装类，应包含uploadId，上传的文件及其指定offset和size信息
* bucketname：指定的Bucket名称
* objectKey：指定的Object键
* uploadId：初始化时得到的uploadId
* file：需要上传的file
* offset：此分块在文件中内容的起始位置
* partNumber：此分块是第几块
* partSize：此分块的大小，单位为Byte


**回调参数：**

* statesCode：Http请求返回的状态码，200表示请求成功，400表示客户端请求错误，403表示签名错误或本地日期时间错误
* responceHeader:Http请求响应报头
* responce：失败时返回的响应正文
* throwable：出错时抛出的异常
* result:成功时返回PartETag结果实体类，包含partNumber以及Etag信息

**代码示例：**
```

		client.uploadPart(bucketName, key,
				uploadId, file, offset, partNumbe，partsize,new UploadPartResponceHandler() {
					@Override
					public void onSuccess(int statesCode,
					Header[] responceHeaders,
					PartETag result) {
						Log.d("eflake","upload part success , partnumber = "+ result.getPartNumber()+ ",etag = "+ result.getETag());
					}

					@Override
					public void onFailure(int statesCode,Header[] responceHeaders,
						String response, Throwable throwable) {
						Log.d("eflake","upload part failed ,responce = "
						+ response);
					}
				}
			);

```

*初始化分块上传后，上传分块接口。Part number 是标识每个分块的数字，介于0-10000之间。除了最后一块，每个块必须大于等于5MB,最后一块没有大小限制。*

**方法名：** 

public void uploadPart(UploadPartRequest request,UploadPartResponceHandler resultHandler)throws Ks3ClientException, Ks3ServiceException{}

**参数说明：**

* resultHandler：回调接口，包含onSuccess以及onFailure两个回调方法，运行在主线程
* request：请求封装类，应包含uploadId，上传的文件及其指定offset和size信息

**回调参数：**

* statesCode：Http请求返回的状态码，200表示请求成功，400表示客户端请求错误，403表示签名错误或本地日期时间错误
* responceHeader:Http请求响应报头
* responce：失败时返回的响应正文
* throwable：出错时抛出的异常
* result:成功时返回UploadPart结果实体类，包含partNumber以及Etag信息

**代码示例：**
```

		client.uploadPart(new UploadPartRequest(bucketName, key,
				uploadId, file, offset, partNumber++, partsize),new UploadPartResponceHandler() {
					@Override
					public void onSuccess(int statesCode,
					Header[] responceHeaders,
					UploadPartResult result) {
						Log.d("eflake","upload part success , partnumber = "+ result.getPartNumber()+ ",etag = "+ result.getETag());
					}

					@Override
					public void onFailure(int statesCode,Header[] responceHeaders,
						String response, Throwable throwable) {
						Log.d("eflake","upload part failed ,responce = "
						+ response);
					}
				}
			);

```

####List Parts:

*罗列出已经上传的块*

**方法名：** 

public void ListParts(String bucketname, String objectkey,
String uploadId,ListPartsResponseHandler handler) throws Ks3ClientException, Ks3ServiceException{}

**参数说明：**

* resultHandler：回调接口，包含onSuccess以及onFailure两个回调方法，运行在主线程
* request：请求封装类，应包含uploadId，上传的文件及其指定offset和size信息
* bucketname：指定的Bucket名称
* objectKey：指定的bject键
* uploadId：初始化时得到的uploadId

**回调参数：**

* statesCode：Http请求返回的状态码，200表示请求成功，400表示客户端请求错误，403表示签名错误或本地日期时间错误
* responceHeader:Http请求响应报头
* responce：失败时返回的响应正文
* throwable：出错时抛出的异常
* result:成功时返回ListPartsResult结果实体类,包含Owner及一个Parts容器类

**代码示例：**
```

		client.ListParts(bucketName, objectKey, uploadId,
						new ListPartsResponseHandler() {
							@Override
							public void onSuccess(int statesCode,
									Header[] responceHeaders,
									ListPartsResult listPartsResult) {
								Log.d("eflake", "listPart success :"
										+ listPartsResult.toString());
								mLastListPartResul = listPartsResult;
								int mLastSize = listPartsResult.getParts()
										.size();
							}

							@Override
							public void onFailure(int statesCode,
									Header[] responceHeaders, String response,
									Throwable paramThrowable) {
								Log.d("eflake",
										"listPart fail ,reason :"
												+ response);

							}
						});

```

*罗列出已经上传的块*

**方法名：** 

public void ListParts(String bucketname, String objectkey,
String uploadId, int maxParts, ListPartsResponseHandler handler) throws Ks3ClientException, Ks3ServiceException{}

**参数说明：**

* resultHandler：回调接口，包含onSuccess以及onFailure两个回调方法，运行在主线程
* request：请求封装类，应包含uploadId，上传的文件及其指定offset和size信息
* bucketname：指定的Bucket名称
* objectKey：指定的Object键
* uploadId：初始化时得到的uploadId
* maxParts:返回块大小限制

**回调参数：**

* statesCode：Http请求返回的状态码，200表示请求成功，400表示客户端请求错误，403表示签名错误或本地日期时间错误
* responceHeader:Http请求响应报头
* responce：失败时返回的响应正文
* throwable：出错时抛出的异常
* result:成功时返回ListPartsResult结果实体类,包含Owner及一个Parts容器类

**代码示例：**
```

		client.ListParts(bucketName, objectKey,uploadId,maxParts, 
						new ListPartsResponseHandler() {
							@Override
							public void onSuccess(int statesCode,
									Header[] responceHeaders,
									ListPartsResult listPartsResult) {
								Log.d("eflake", "listPart success :"
										+ listPartsResult.toString());
								mLastListPartResul = listPartsResult;
								int mLastSize = listPartsResult.getParts()
										.size();
							}

							@Override
							public void onFailure(int statesCode,
									Header[] responceHeaders, String response,
									Throwable paramThrowable) {
								Log.d("eflake",
										"listPart fail ,reason :"
												+ response);
							}
						});

```

*罗列出已经上传的块*

**方法名：** 

public void ListParts(String bucketname, String objectkey,
String uploadId,int maxParts, int partNumberMarker, ListPartsResponseHandler handler) throws Ks3ClientException, Ks3ServiceException{}

**参数说明：**

* resultHandler：回调接口，包含onSuccess以及onFailure两个回调方法，运行在主线程
* request：请求封装类，应包含uploadId，上传的文件及其指定offset和size信息
* bucketname：指定的Bucket名称
* objectKey：指定的Object键
* uploadId：初始化时得到的uploadId
* maxParts:返回块大小限制
* partNumberMarker:块号标记，将返回大于此块号的分块

**回调参数：**

* statesCode：Http请求返回的状态码，200表示请求成功，400表示客户端请求错误，403表示签名错误或本地日期时间错误
* responceHeader:Http请求响应报头
* responce：失败时返回的响应正文
* throwable：出错时抛出的异常
* result:成功时返回ListPartsResult结果实体类,包含Owner及一个Parts容器类

**代码示例：**
```

		client.ListParts(bucketName, objectKey,uploadId,maxParts,partNumberMarker,
						new ListPartsResponseHandler() {
							@Override
							public void onSuccess(int statesCode,
									Header[] responceHeaders,
									ListPartsResult listPartsResult) {
								Log.d("eflake", "listPart success :"
										+ listPartsResult.toString());
								mLastListPartResul = listPartsResult;
								int mLastSize = listPartsResult.getParts()
										.size();
							}

							@Override
							public void onFailure(int statesCode,
									Header[] responceHeaders, String response,
									Throwable paramThrowable) {
								Log.d("eflake",
										"listPart fail ,reason :"
												+ response);
							}
						});

```



####Abort Multipart Upload:

*取消分块上传。*

**方法名：** 

public void abortMultipartUpload(String bucketname, String objectkey,
String uploadId,AbortMultipartUploadResponseHandler handler) throws Ks3ClientException, Ks3ServiceException{}

**参数说明：**

* resultHandler：回调接口，包含onSuccess以及onFailure两个回调方法，运行在主线程
* request：请求封装类，应包含uploadId，上传的文件及其指定offset和size信息
* bucketname：指定的Bucket名称
* objectKey：指定的Object键
* uploadId：初始化时得到的uploadId

**回调参数：**

* statesCode：Http请求返回的状态码，204表示请求成功，400表示客户端请求错误，403表示签名错误或本地日期时间错误
* responceHeader:Http请求响应报头
* responce：失败时返回的响应正文
* throwable：出错时抛出的异常

**代码示例：**
```

		client.abortMultipartUpload(bucketname, objectkey, uploadId, new AbortMultipartUploadResponseHandler() {
			
			@Override
			public void onSuccess(int statesCode, Header[] responceHeaders) {
			}
			
			@Override
			public void onFailure(int statesCode, Header[] responceHeaders,
					String response, Throwable paramThrowable) {
			}
		});
		
```

*取消分块上传。*

**方法名：** 

public void abortMultipartUpload(AbortMultipartUploadRequest request, AbortMultipartUploadResponseHandler handler) throws Ks3ClientException, Ks3ServiceException{}

**参数说明：**

* resultHandler：回调接口，包含onSuccess以及onFailure两个回调方法，运行在主线程
* request：请求封装类，应包含uploadId，上传的文件及其指定offset和size信息
* bucketname：指定的Bucket名称
* objectKey：指定的Object键
* uploadId：初始化时得到的uploadId

**回调参数：**

* statesCode：Http请求返回的状态码，200表示请求成功，400表示客户端请求错误，403表示签名错误或本地日期时间错误
* responceHeader:Http请求响应报头
* responce：失败时返回的响应正文
* throwable：出错时抛出的异常

**代码示例：**
```

		client.abortMultipartUpload(new AbortMultipartUploadRequest(bucketname, objectkey, uploadId), new AbortMultipartUploadResponseHandler() {
			
			@Override
			public void onSuccess(int statesCode, Header[] responceHeaders) {
			}
			
			@Override
			public void onFailure(int statesCode, Header[] responceHeaders,
					String response, Throwable paramThrowable) {
			}
		});
		
```

####Complete Multipart Upload:

*组装之前上传的块，然后完成分块上传。通过你提供的xml文件，进行分块组装。在xml文件中，块号必须使用升序排列。必须提供每个块的ETag值。*

**方法名：** 

public void completeMultipartUpload(String bucketname, String objectkey, String uploadId,
List<PartETag> partETags,CompleteMultipartUploadResponseHandler handler) throws Ks3ClientException,Ks3ServiceException{}

**参数说明：**

* resultHandler：回调接口，包含onSuccess以及onFailure两个回调方法，运行在主线程
* request：请求封装类，应包含uploadId，上传的文件及其指定offset和size信息
* bucketname：指定的Bucket名称
* objectKey：指定的Object键
* uploadId：初始化时得到的uploadId
* partETags：PartEtag容器，PartEtag是服务器UploadPart成功时，服务器返回的Etag以及PartNumber信息实体类

**回调参数：**

* statesCode：Http请求返回的状态码，200表示请求成功，400表示客户端请求错误，403表示签名错误或本地日期时间错误
* responceHeader:Http请求响应报头
* responce：失败时返回的响应正文
* throwable：出错时抛出的异常
* result:成功时返回结果实体类

**代码示例：**
```

		client.completeMultipartUpload(
							new CompleteMultipartUploadRequest(
									mLastListPartResul),
							new CompleteMultipartUploadResponseHandler() {

								@Override
								public void onSuccess(int statesCode,
										Header[] responceHeaders,
										CompleteMultipartUploadResult result) {
									Log.d("eflake",
											"completeMultipartUpload success, result = "
													+ result.getBucket());
								}

								@Override
								public void onFailure(int statesCode,
										Header[] responceHeaders,
										String response,
										Throwable paramThrowable) {
									Log.d("eflake",
											"completeMultipartUpload failed, reason =  "
													+ response);
								}
							});

```

*组装之前上传的块，然后完成分块上传。通过你提供的xml文件，进行分块组装。在xml文件中，块号必须使用升序排列。必须提供每个块的ETag值。*

**方法名：** 

public void completeMultipartUpload(ListPartsResult result, CompleteMultipartUploadResponseHandler handler) throws Ks3ClientException,Ks3ServiceException{}

**参数说明：**

* resultHandler：回调接口，包含onSuccess以及onFailure两个回调方法，运行在主线程
* result:ListParts()接口返回结果实体类

**回调参数：**

* statesCode：Http请求返回的状态码，200表示请求成功，400表示客户端请求错误，403表示签名错误或本地日期时间错误
* responceHeader:Http请求响应报头
* responce：失败时返回的响应正文
* throwable：出错时抛出的异常
* result:成功时返回结果实体类

**代码示例：**
```

		client.completeMultipartUpload(mLastListPartResul,
							new CompleteMultipartUploadResponseHandler() {

								@Override
								public void onSuccess(int statesCode,
										Header[] responceHeaders,
										CompleteMultipartUploadResult result) {
									Log.d("eflake",
											"completeMultipartUpload success, result = "
													+ result.getBucket());
								}

								@Override
								public void onFailure(int statesCode,
										Header[] responceHeaders,
										String response,
										Throwable paramThrowable) {
									Log.d("eflake",
											"completeMultipartUpload failed, reason =  "
													+ response);
								}
							});

```

*组装之前上传的块，然后完成分块上传。通过你提供的xml文件，进行分块组装。在xml文件中，块号必须使用升序排列。必须提供每个块的ETag值。*

**方法名：** 

public void completeMultipartUpload(CompleteMultipartUploadRequest request, CompleteMultipartUploadResponseHandler handler) throws Ks3ClientException,Ks3ServiceException{}

**参数说明：**

* resultHandler：回调接口，包含onSuccess以及onFailure两个回调方法，运行在主线程
* request：请求封装类，应包含uploadId，上传的文件及其指定offset和size信息

**回调参数：**

* statesCode：Http请求返回的状态码，200表示请求成功，400表示客户端请求错误，403表示签名错误或本地日期时间错误
* responceHeader:Http请求响应报头
* responce：失败时返回的响应正文
* throwable：出错时抛出的异常
* result:成功时返回结果实体类

**代码示例：**
```

		client.completeMultipartUpload(
							new CompleteMultipartUploadRequest(
									mLastListPartResul),
							new CompleteMultipartUploadResponseHandler() {

								@Override
								public void onSuccess(int statesCode,
										Header[] responceHeaders,
										CompleteMultipartUploadResult result) {
									Log.d("eflake",
											"completeMultipartUpload success, result = "
													+ result.getBucket());
								}

								@Override
								public void onFailure(int statesCode,
										Header[] responceHeaders,
										String response,
										Throwable paramThrowable) {
									Log.d("eflake",
											"completeMultipartUpload failed, reason =  "
													+ response);
								}
							});

```

####Multipart Upload Example Code:

*分片上传代码示例*

````

		handler = new UploadPartHandler();
		client.initiateMultipartUpload(new InitiateMultipartUploadRequest(
				bucketName, objectKey),
				new InitiateMultipartUploadResponceHandler() {

					@Override
					public void onSuccess(int statesCode,
							Header[] responceHeaders,
							InitiateMultipartUploadResult result) {
						String uploadId = result.getUploadId();
						handler.sendEmptyMessage(TransferManager.STATE_UPLOAD_NEXT);

					@Override
					public void onFailure(int statesCode,
							Header[] responceHeaders, String response,
							Throwable paramThrowable) {
					}
				}
			);

		public class UploadPartHandler extends Handler {

					@Override
					public void handleMessage(Message msg) {
						super.handleMessage(msg);
						switch (msg.what) {
					case TransferManager.STATE_UPLOAD_NEXT:
					if (localUploadPartRequestFactory.hasMoreRequests()) {
					UploadPartRequest uploadPartRequest = localUploadPartRequestFactory
							.getNextUploadPartRequest();
					client.uploadPart(uploadPartRequest,
							new UploadPartResponceHandler() {
								@Override
								public void onSuccess(int statesCode,
										Header[] responceHeaders,
										UploadPartResult result) {
									Log.d("eflake",
											"upload part success , partnumber = "
													+ result.getPartNumber()
													+ ",etag = "
													+ result.getETag());
									handler.sendEmptyMessage(TransferManager.STATE_UPLOAD_NEXT);
								}
								@Override
								public void onFailure(int statesCode,
										Header[] responceHeaders,
										String response, Throwable throwable) {
									Log.d("eflake",
											"upload part failed ,responce = "
													+ response);
									handler.sendEmptyMessage(TransferManager.STATE_UPLOAD_FAILED);
								}
							});
				} else {
					handler.sendEmptyMessage(TransferManager.STATE_UPLOAD_FINISH);
				}
				break;
			case TransferManager.STATE_UPLOAD_FINISH:
				client.ListParts(bucketName, objectKey,
						transferResponceHandler.getMultipartUoloadId(),
						new ListPartsResponseHandler() {

							@Override
							public void onSuccess(int statesCode,
									Header[] responceHeaders,
									ListPartsResult listPartsResult) {
								mLastListPartResul = listPartsResult;
								handler.sendEmptyMessage(TransferManager.STATE_UPLOAD_COMPLETE);
							}

							@Override
							public void onFailure(int statesCode,
									Header[] responceHeaders, String response,
									Throwable paramThrowable) {
								Log.d("eflake",
										"listPart failed, reason :"
												+ response);

							}
						});
				break;
			case TransferManager.STATE_UPLOAD_FAILED:
				break;
			case TransferManager.STATE_UPLOAD_COMPLETE:
				if (mLastListPartResul != null) {
					client.completeMultipartUpload(
							new CompleteMultipartUploadRequest(
									mLastListPartResul),
							new CompleteMultipartUploadResponseHandler() {

								@Override
								public void onSuccess(int statesCode,
										Header[] responceHeaders,
										CompleteMultipartUploadResult result) {
									Log.d("eflake",
											"completeMultipartUpload success, result = "
													+ result.getBucket());
								}

								@Override
								public void onFailure(int statesCode,
										Header[] responceHeaders,
										String response,
										Throwable paramThrowable) {
									Log.d("eflake",
											"completeMultipartUpload failed, reason =  "
													+ response);
								}
							});
				}
				break;
			default:
				break;
			}
		}
	}

````

##其它
>完整示例，请见 [KS3-Android-SDK-Demo](https://github.com/ks3sdk/ks3-android-sdk/tree/master/Ks3SdkAndroid/demo) 
