# RxList

Observable as List.

## Usage

rx.list.RxList:

```java
List<ParseUser> users = new RxList<>(ParseObservable.find(ParseUser.getQuery()));
```

## Bonus

rx.list.SimpleMapList:

```java
List<ParseUser> users = new SimpleMapList<FriendRequest, ParseUser>(getIdolsQuery().find())
                            .map(request -> request.getToUser());
```

rx.list.RxMapList:

```java
List<Post> posts = RxMapList.create(post.getCachedPosts(), obs -> obs.map(p -> new ParsePost(p).setIsMainPost(false)));
```

rx.list.MapList:

```java
List<MediaEvent> events = new MapList<>(datas, new MapList.Mapper<MediaEvent>() {
    @Override
    public MediaEvent map(List<? extends Object> data, int index) {
        MediaEvent event;
        Object object = data.get(index);
        if (object instanceof MediaEvent) {
            event = (MediaEvent) object;
        } else {
            event = MediaEvent.loadFromJson(new JSONObject((Map<?, ?>) object).toString());
        }
        event.document = docs.get(index);
        return event;
    }
});
```

## Installation

rxlist for java project @ jar:

```gradle
repositories {
    jcenter()
    maven { url "https://jitpack.io" }
}

dependencies {
    compile 'com.github.yongjhih:rxlist:1.0.0' // depends on unofficial yongjhih/jave-util@jar LruCache
}
```

or, rxlist-android for android project @ aar:

```gradle
repositories {
    jcenter()
    maven { url "https://jitpack.io" }
}

dependencies {
    compile 'com.github.yongjhih.rxlist:rxlist-android:1.0.1' // depends on support-v4@aar LruCache
}
```

## LICENSE

Copyright 2015 8tory, Inc.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
