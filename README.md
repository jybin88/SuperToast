# SuperToast
android自带的Toast，手机设置不显示通知后无法显示。自定义实现一个Toast规避这种问题
## 使用步骤

### 1. 在project的build.gradle添加如下代码(如下图)

	allprojects {
	    repositories {
	        ...
	        maven { url "https://jitpack.io" }
	    }
	}

![](<https://github.com/jybin88/public/raw/master/dependence.png>)


### 2. 在Module的build.gradle添加依赖

    compile 'com.github.jybin88:SuperToast:v0.1'
