# StickerCamera

[![JitPack](https://img.shields.io/github/tag/yongjhih/StickerCamera.svg?label=JitPack)](https://jitpack.io/#yongjhih/StickerCamera)
[![Build Status](https://travis-ci.org/yongjhih/StickerCamera.svg)](https://travis-ci.org/yongjhih/StickerCamera)
[![javadoc](https://img.shields.io/github/tag/yongjhih/StickerCamera.svg?label=javadoc)](https://jitpack.io/com/github/yongjhih/StickerCamera/-SNAPSHOT/javadoc/)

![](art/StickerCamera.png)

Allow sticking and tagging.

## Feature

* Achieve the function of camera.
* Achieve the function of photo cropping.
* Achieve the function of photo filter.
* Can stick collage on photo (collage can be moved,enlarged, and rotated).
* Can tag in pictures(also can be moved).
* Save the completed pictures locally, show more custom views and better framework.

 (Translation powered by Amy)

## Usage

```java
mStickerManager = new StickerManager(parent);

@OnClick(R.id.sticker)
public void addSticker() {
    mStickerManager.addSticker();
}

@OnClick(R.id.bubble)
public void addBubble() {
    mStickerManager.addLabel();
}
```

## Installation

```gradle
repositories {
    // ...
    maven { url "https://jitpack.io" }
}

dependencies {
    compile 'com.github.yongjhih:StickerCamera:-SNAPSHOT'
}
```

## Thanks

- [Gpu-image](https://github.com/CyberAgent/android-gpuimage)
- [Android-Universal-Image-Loader](https://github.com/nostra13/Android-Universal-Image-Loader)
- [ImageViewTouch](https://github.com/ojas-webonise/ImageViewTouch)
- [butterknife](https://github.com/JakeWharton/butterknife)
- [Notes](https://github.com/lguipeng/Notes)
- [SystemBarTint](https://github.com/jgilfelt/SystemBarTint)
- [cropimage](https://github.com/biokys/cropimage)
- [EventBus](https://github.com/greenrobot/EventBus)
- [Aviary-Android-SDK](https://github.com/kitek/Aviary-Android-SDK)
- [fastjson](https://github.com/alibaba/fastjson)
- [PagerSlidingTabStrip](https://github.com/astuetz/PagerSlidingTabStrip)
- [android-common](https://github.com/Trinea/android-common)

## Contributors

- [ouyezi](https://github.com/ouyezi)
- [Skykai521](https://github.com/Skykai521)

## Contact Me

- Weibo: http://weibo.com/2030683111
- Email: 1132234509@qq.com

## License
```
Copyright 2015 8tory, Inc.
Copyright 2015 DaQingkai

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
