[![JitPack](https://img.shields.io/github/tag/yongjhih/RxList.svg?label=JitPack)](https://jitpack.io/#yongjhih/RxList)
[![Build Status](https://travis-ci.org/yongjhih/RxList.svg)](https://travis-ci.org/yongjhih/RxList)

# RxList

Observable as List.

* created by @Wendly, Mon., Jul. 28, 2014
* exported by @yongjhih, 2016

## Usage

rx.list.RxList:

```java
List<String> lengthList = new RxList<>(Observable.from(Arrays.asList("Hello", "World!")));
```

## Bonus

rx.list.MapList:

```java
List<Integer> lengthList = new MapList<String, Integer>(Arrays.asList("Hello", "World!"))
                               .map(name -> name.length());
```

rx.list.RxMapList:

```java
List<Integer> lengthList = RxMapList.create(Observable.from(Arrays.asList("Hello", "World!")), obs -> obs.map(name -> name.length()));
```

## Installation

rxlist for java project @ jar:

```gradle
repositories {
    jcenter()
    maven { url "https://jitpack.io" }
}

dependencies {
    compile 'com.github.yongjhih.RxList:rxlist:-SNAPSHOT' // depends on unofficial yongjhih/jave-util@jar LruCache
    compile 'com.github.yongjhih.RxList:rxlist-rxjava:-SNAPSHOT'
}
```

or, rxlist-android for android project @ aar:

```gradle
repositories {
    jcenter()
    maven { url "https://jitpack.io" }
}

dependencies {
    compile 'com.github.yongjhih.RxList:rxlist-android:-SNAPSHOT' // depends on support-v4@aar LruCache
    compile 'com.github.yongjhih.RxList:rxlist-android-rxjava:-SNAPSHOT' // depends on support-v4@aar LruCache
}
```

## TODO

```
lazylist.filter(predication);
for (Item i : lazylist) {}; // if (!iter.predication()) iter.next();
```

## LICENSE

```
Copyright 2015 8tory, Inc.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
```
