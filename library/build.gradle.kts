import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.publish)
}

extensions.configure(MavenPublishBaseExtension::class.java) {
    publishToMavenCentral(SonatypeHost.S01, automaticRelease = true)
    signAllPublications()
}

dependencies {
    api(libs.ktor.server.netty)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.server.html.builder)
    implementation(libs.kotlin.css)
    implementation(libs.kotlin.logging)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.html.jvm)
    implementation(libs.knotion)
    implementation(libs.scrimage)
}