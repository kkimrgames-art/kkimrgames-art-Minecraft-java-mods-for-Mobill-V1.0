plugins {
	id("com.android.application")
}

android {
	namespace = "net.eaglercraft.webapk"
	compileSdk = 35
	buildToolsVersion = "35.0.0"

	defaultConfig {
		applicationId = "net.eaglercraft.webapk"
		minSdk = 24
		targetSdk = 35
		versionCode = 1
		versionName = "1.0"
	}

	buildTypes {
		release {
			isMinifyEnabled = false
		}
	}

	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}

	sourceSets {
		getByName("main") {
			assets.srcDir(file("../../../../web-build"))
		}
	}
}

dependencies {
	implementation("androidx.appcompat:appcompat:1.7.0")
	implementation("androidx.webkit:webkit:1.12.1")
}

configurations.configureEach {
	exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk7")
	exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk8")
}
