PicoImg
=====
PicoImg is a fast and compact image loading library for Android apps. It can fetch and cache images from the web, play APNG and GIF animation, provide smooth scrolling and much more in a tiny footprint (~80KB source). It's written purely in JAVA and works on almost every Android out there (min api8 -> Android 2.2).

Adding
--------
With Gradle:

```gradle
Add the Jitpack repository to your root build.gradle file
allprojects {
    repositories {
	...
	maven { url 'https://jitpack.io' }
    }
}

Add the dependency to your app build.gradle file
dependencies {
        implementation 'com.github.aigilea:PicoImg:0.3'
}
```
For the information on the other build environments see the [Jitpack release page][1].

Using
-------------------
First, PicoImg should be explicitly initialized:
```java
import in.cpp.picoimg.PicoImg;

...
PicoImg.init(context, cachePath, cacheLimitBytes);
```
cachePath and cacheLimitBytes are optional, you may pass null to use the default cache location and 0 to disable size limit.

You can call the init function from any place you like, just make sure you call it before any other function.
The good choice for a single activity app is Acivity.onCreate, in case of a multi activity app consider using Application.attachBaseContext.
If you are going to call it from some other place try to call it only once as each subsequent call will flush the RAM cache resulting in a slight performance penalty.


Now we can load the image:
```java
import in.cpp.picoimg.PicoImg;
...
ImageView imageView = (ImageView) findViewById(R.id.image_view);
PicoImg.loadUrl(context, "https://upload.wikimedia.org/wikipedia/commons/1/14/Animated_PNG_example_bouncing_beach_ball.png").to(imageView).sizeToView().runAsync();
```
If you don't need smooth scroll optimization you may remove sizeToView() (see below).

Using all the power
-------------------
in.cpp.picoimg.PicoImg:

```
Initializer, see above:
public static void init(Context ctx, String cacheDir, long cacheLimit), see above

Cache config functions:
public static long getCacheUsage()
public static void setCacheSize(long limit)
public static boolean emptyCache(final Context ctx, final Runnable onDone)

Request functions:
public static PicoImgRequest loadResource(Context ctx, int resId), creates a request to load the specified resource.
public static PicoImgRequest loadAsset(Context ctx, String name), same with asset.
public static PicoImgRequest loadUrl(Context ctx, String url), same with URL.
public static PicoImgRequest loadUrl(Context ctx, String url, String key), same with URL and explicit cache key.
public static void cancel(ImageView v), if there's a pending request with the specified view as a target, cancel it.

Scaling flags:
SCALE_TOP    Aligns source image to top, bottom or center (if not specified) in the resized image.
SCALE_BOTTOM  
SCALE_LEFT   Aligns source image to left, right or center (if not specified) in the resized image.
SCALE_RIGHT
SCALE_CROP   Resizes the source image preserving the aspect ratio, crops the excess.
SCALE_FIT    Resizes the source image preserving the aspect ratio, fills the empty space with transparent color.
SCALE_FILL   Resizes the source image not preserving the aspect ratio.
```

in.cpp.picoimg.PicoImgRequest
```
Target functions (at least one should be called):
public PicoImgRequest to(ImageView v), specify ImageView to set the loaded drawable to.
public PicoImgRequest callback(TargetCallback c), specify the request event callback.

Configuration functions (optional):
public PicoImgRequest placeholder(int resId), specify the image to set to the ImageView while request is running.
public PicoImgRequest placeholder(Drawable drawable)
public PicoImgRequest fade(int steps, int durationMillis, boolean always), specify fade animation when image is loaded and if the animation should run in case of cache hit and immediate request completion.
public PicoImgRequest size(int width, int height), resize image to the given size.
public PicoImgRequest sizeToView(), resize image to the target ImageView size. This trick makes scrolling smoother by preventing extra measure/layout pass when the image is loaded.
public PicoImgRequest sizeToScreen(), limits image size to the screen size. This may be useful if the target size is unknown, but you want to save some memory on the huge image.
public PicoImgRequest scale(int scale), scaling mode for size* functions, one of the PicoImg.SCALE_* flags.
public PicoImgRequest disableAnimation(boolean disable), always load static image.
public PicoImgRequest skipCache(boolean skipRamLookup, boolean skipRamStore, boolean skipDiskLookup, boolean skipDiskStore)
public PicoImgRequest cachedOnly(boolean cachedOnly)

Launch functions:
public void runAsync(), performs the request in the background. 
public void run(), performs the request synchronously. For debugging purposes only.

```

in.cpp.picoimg.PicoDrawable
```
public boolean isAnimated()
```

Authors
-------
[aigilea][2]

License
-------
Three-clause BSD license, for more information see [LICENSE][3].

[1]: https://jitpack.io#aigilea/PicoImg/0.3
[2]: https://github.com/aigilea/
[3]: https://github.com/aigilea/PicoImg/blob/master/LICENSE
