plugins {
    alias(libs.plugins.dokka)
    alias(libs.plugins.paperweight) apply false
    `maven-publish`
    signing
}

dependencies {
    implementation(projectApi)
}

tasks {
    register<Jar>("devJar") {
        archiveClassifier.set("dev")

        from(sourceSets["main"].output)
        subprojects {
            from(sourceSets["main"].output)
        }
    }

    register<Jar>("reobfJar") {
        from(sourceSets["main"].output)
        subprojects {
            val reobfJar = tasks.named("reobfJar").get() as io.papermc.paperweight.tasks.RemapJar
            dependsOn(reobfJar)
            from(zipTree(reobfJar.outputJar))
        }
    }

    create<Jar>("sourcesJar") {
        archiveClassifier.set("sources")
        from(sourceSets["main"].allSource)
    }

    create<Jar>("dokkaJar") {
        archiveClassifier.set("javadoc")
        dependsOn("dokkaHtml")

        from("${layout.buildDirectory.asFile.get()}/dokka/html/") {
            include("**")
        }
    }
}

subprojects {
    apply(plugin = rootProject.libs.plugins.paperweight.get().pluginId)
    dependencies {
        implementation(projectApi)
        implementation(projectCore)

        val paperweight = (this as ExtensionAware).extensions.getByName("paperweight")
                as io.papermc.paperweight.userdev.PaperweightUserDependenciesExtension
        paperweight.paperDevBundle("${name.substring(1)}-R0.1-SNAPSHOT")
    }
}

publishing {
    repositories {
        mavenLocal()

        maven {
            name = "server"
            url = rootProject.uri(".server/libraries")
        }

        maven {
            name = "central"

            credentials.runCatching {
                val nexusUsername: String by project
                val nexusPassword: String by project
                username = nexusUsername
                password = nexusPassword
            }.onFailure {
                logger.warn("Failed to load nexus credentials, Check the gradle.properties")
            }

            url = uri(
                if ("SNAPSHOT" in version as String) {
                    "https://s01.oss.sonatype.org/content/repositories/snapshots/"
                } else {
                    "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
                }
            )
        }
    }

    publications {
        create<MavenPublication>("kommand-core") {
            artifactId = "kommand-core"

            from(project.components["java"])

            if (hasProperty("dev")) {
                artifact(tasks["devJar"])
            }
            artifacts.removeIf { it.classifier == null }
            artifact(tasks["sourcesJar"])
            artifact(tasks["dokkaJar"])
            artifact(tasks["reobfJar"]) {
                classifier = null
            }

            pom {
                name.set("kommand-core")
                description.set("Kotlin DSL for PaperMC commands")
                url.set("https://github.com/gooddltmdqls/${rootProject.name}")

                licenses {
                    license {
                        name.set("GNU General Public License version 3")
                        url.set("https://opensource.org/licenses/GPL-3.0")
                    }
                }

                developers {
                    developer {
                        id.set("monun")
                        name.set("Monun")
                        email.set("monun1010@gmail.com")
                        url.set("https://github.com/monun")
                        roles.addAll("developer")
                        timezone.set("Asia/Seoul")
                    }
                    developer {
                        id.set("icetang0123")
                        name.set("Icetang0123")
                        email.set("1415wwwh@gmail.com")
                        url.set("https://github.com/gooddltmdqls")
                        roles.addAll("developer")
                        timezone.set("Asia/Seoul")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/gooddltdmqls/${rootProject.name}.git")
                    developerConnection.set("scm:git:ssh://github.com:gooddltmdqls/${rootProject.name}.git")
                    url.set("https://github.com/gooddltmdqls/${rootProject.name}")
                }
            }

            afterEvaluate {
                tasks["generateMetadataFileForKommand-corePublication"].dependsOn("reobfJar")
            }
        }
    }
}

signing {
    isRequired = true
    sign(publishing.publications["kommand-core"])
}