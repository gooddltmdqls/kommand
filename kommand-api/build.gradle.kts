plugins {
    alias(libs.plugins.dokka)
    `maven-publish`
    signing
}

tasks {
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

publishing {
    publications {
        create<MavenPublication>("kommand-api") {
            artifactId = "kommand-api"

            from(project.components["java"])

            if (hasProperty("dev")) {
                artifact(project.tasks["devJar"])
            }
            artifact(projectApi.tasks["sourcesJar"])
            artifact(projectApi.tasks["dokkaJar"])

            pom {
                name.set("kommand-api")
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
    sign(publishing.publications["kommand-api"])
}