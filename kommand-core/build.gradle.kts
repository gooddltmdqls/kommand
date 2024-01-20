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
        archiveClassifier.set("reobf")

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

        from("$buildDir/dokka/html/") {
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
    publications {
        create<MavenPublication>("kommand-core") {
            artifactId = "kommand-core"

            from(project.components["java"])

            if (hasProperty("dev")) {
                artifact(tasks["devJar"])
            }
            artifact(tasks["sourcesJar"])
            artifact(tasks["dokkaJar"])
            artifact(tasks["reobfJar"])

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
        }
    }
}

signing {
    isRequired = true
    sign(publishing.publications["kommand-core"])
}