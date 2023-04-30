plugins {
    id("java")
    id("maven-publish")
}

repositories {
    mavenLocal()
    mavenCentral()
}

java {
    withSourcesJar()
    withJavadocJar()

    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

publishing {
    publications {
        create<MavenPublication>("qft-junit") {
            from(components["java"])

            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
        }
    }

    repositories {
        mavenLocal()
        maven {
            name = "Gitea"
            // change URLs to point to your repos, e.g. http://my.org/repo
            val releasesRepoUrl = uri("https://gitea.cloud.contexx.one/api/packages/contexx/maven")
            val snapshotsRepoUrl = uri("https://gitea.cloud.contexx.one/api/packages/contexx-snapshots/maven")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            credentials.username = project.properties["gitea_user"].toString()
            credentials.password = project.properties["gitea_password"].toString()
        }
    }
}

dependencies {
    implementation(project(":qft-core"))
    implementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    implementation("org.junit.jupiter:junit-jupiter-params:5.8.1")
    implementation("org.junit.jupiter:junit-jupiter-engine")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    testImplementation("com.google.jimfs:jimfs:1.2")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}