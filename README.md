# Gooey Loader
![image](https://raw.githubusercontent.com/ehehhh/Gooey-Loader/master/demo.gif)

### Usage
Just include in your layout:
```
<ee.subscribe.gooeyloader.GooeyLoaderView
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />
```

### Attributes
| Attribute | Description |
| --- | --- |
| `glv_color` | Color of the balls (default is #9c27b0) |
| `glv_duration_in_ms` | Animation duration in ms (default is 3500ms, limits are 1000-7000ms) |

### Including In Your Project
In your root build.gradle add at the end of repositories:
```
allprojects {
        repositories {
                ...
                maven { url "https://jitpack.io" }
        }
}
```
Then add the dependency:
```
dependencies {
        compile 'com.github.ehehhh:Gooey-Loader:v1.1.1'
}
```

### License

* [Apache Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

```
Copyright 2016 Rait Maltsaar

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
