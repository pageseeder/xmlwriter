plugins {
  id("java-library")
  id("maven-publish")
  alias(libs.plugins.jreleaser)
}

val title: String by project
val gitName: String by project
val website: String by project

group = "org.pageseeder.xmlwriter"
version = file("version.txt").readText().trim()
description = "XML Writer API for Java"

repositories {
  mavenCentral()
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(11))
  }
  withJavadocJar()
  withSourcesJar()
}

dependencies {
  compileOnly(libs.jspecify)

  // JUnit 5 dependencies
  testImplementation(platform(libs.junit.bom))
  testImplementation(libs.bundles.junit.testing)
  testImplementation(libs.diffx) {
    exclude(module = "pso-xmlwriter")
  }

  testRuntimeOnly(libs.junit.jupiter.engine)
}

tasks.test {
  useJUnitPlatform()
  testLogging {
    events("passed", "skipped", "failed")
  }
}

tasks.jar {
  manifest {
    attributes(
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to "Allette Systems",
            "Specification-Title" to project.description,
            "Specification-Version" to project.version,
            "Specification-Vendor" to "Allette Systems"
    )
  }
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      from(components["java"])
      groupId = group.toString()
      pom {
        name.set(title)
        description.set(project.description)
        url.set(website)
        licenses {
          license {
            name.set("The Apache Software License, Version 2.0")
            url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
          }
        }
        organization {
          name.set("Allette Systems")
          url.set("https://www.allette.com.au")
        }
        scm {
          url.set("git@github.com:pageseeder/${gitName}.git")
          connection.set("scm:git:git@github.com:pageseeder/${gitName}.git")
          developerConnection.set("scm:git:git@github.com:pageseeder/${gitName}.git")
        }
        developers {
          developer {
            name.set("Christophe Lauret")
            email.set("clauret@weborganic.com")
          }
          developer {
            name.set("Philip Rutherford")
            email.set("philipr@weborganic.com")
          }
          developer {
            name.set("Alberto Santos")
            email.set("asantos@allette.com.au")
          }
          developer {
            name.set("Valerie Ku")
            email.set("vku@allette.com.au")
          }
        }
      }
    }
  }
  repositories {
    maven {
      url = layout.buildDirectory.dir("staging-deploy").get().asFile.toURI()
    }
  }
}

jreleaser {
  configFile.set(file("jreleaser.toml"))
}

