package com.jaredsburrows.license

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
final class LicensePluginSpec extends Specification {
  final static def COMPILE_SDK_VERSION = 25
  final static def BUILD_TOOLS_VERSION = "25.0.2"
  final static def APPLICATION_ID = "com.example"
  def project

  def "setup"() {
    project = ProjectBuilder.builder().build()
  }

  def "test unsupported project project"() {
    when:
    new LicensePlugin().apply(project)

    then:
    def e = thrown(IllegalStateException)
    e.message == "License report plugin can only be applied to android, groovy or java projects."
  }

  def "test groovy project"() {
    given:
    project.apply plugin: "groovy"

    when:
    new LicensePlugin().apply(project)

    then:
    notThrown(IllegalStateException)
  }

  def "test java project"() {
    given:
    project.apply plugin: "java"

    when:
    new LicensePlugin().apply(project)

    then:
    notThrown(IllegalStateException)
  }

  def "test android application project"() {
    given:
    project.apply plugin: "com.android.application"

    when:
    new LicensePlugin().apply(project)

    then:
    notThrown(IllegalStateException)
  }

  def "test android library project"() {
    given:
    project.apply plugin: "com.android.library"

    when:
    new LicensePlugin().apply(project)

    then:
    notThrown(IllegalStateException)
  }

  def "test android test project"() {
    given:
    project.apply plugin: "com.android.test"

    when:
    new LicensePlugin().apply(project)

    then:
    notThrown(IllegalStateException)
  }

  def "test java default all tasks created"() {
    given:
    project.apply plugin: "java"

    when:
    project.evaluate()
    new LicensePlugin().apply(project)

    then:
    project.tasks.getByName("licenseReport")
  }

  def "test android default all tasks created"() {
    given:
    project.apply plugin: "com.android.application"
    project.android {
      compileSdkVersion COMPILE_SDK_VERSION
      buildToolsVersion BUILD_TOOLS_VERSION

      defaultConfig {
        applicationId APPLICATION_ID
      }
    }

    when:
    project.evaluate()
    new LicensePlugin().apply(project)

    then:
    project.tasks.getByName("licenseDebugReport")
  }

  def "test android buildTypes all tasks created"() {
    given:
    project.apply plugin: "com.android.application"
    project.android {
      compileSdkVersion COMPILE_SDK_VERSION
      buildToolsVersion BUILD_TOOLS_VERSION

      defaultConfig {
        applicationId APPLICATION_ID
      }

      buildTypes {
        debug {}
        release {}
      }
    }

    when:
    project.evaluate()
    new LicensePlugin().apply(project)

    then:
    project.tasks.getByName("licenseDebugReport")
    project.tasks.getByName("licenseReleaseReport")
  }

  def "test android buildTypes productFlavors all tasks created"() {
    given:
    project.apply plugin: "com.android.application"
    project.android {
      compileSdkVersion COMPILE_SDK_VERSION
      buildToolsVersion BUILD_TOOLS_VERSION

      defaultConfig {
        applicationId APPLICATION_ID
      }

      buildTypes {
        debug {}
        release {}
      }

      productFlavors {
        flavor1 {}
        flavor2 {}
      }
    }

    when:
    project.evaluate()
    new LicensePlugin().apply(project)

    then:
    project.tasks.getByName("licenseFlavor1DebugReport")
    project.tasks.getByName("licenseFlavor1ReleaseReport")
    project.tasks.getByName("licenseFlavor2DebugReport")
    project.tasks.getByName("licenseFlavor2ReleaseReport")
  }

  def "test android buildTypes productFlavors flavorDimensions all tasks created"() {
    given:
    project.apply plugin: "com.android.application"
    project.android {
      compileSdkVersion COMPILE_SDK_VERSION
      buildToolsVersion BUILD_TOOLS_VERSION

      defaultConfig {
        applicationId APPLICATION_ID
      }

      buildTypes {
        debug {}
        release {}
      }

      flavorDimensions "a", "b"

      productFlavors {
        flavor1 { dimension "a" }
        flavor2 { dimension "a" }
        flavor3 { dimension "b" }
        flavor4 { dimension "b" }
      }
    }

    when:
    project.evaluate()
    new LicensePlugin().apply(project)

    then:
    project.tasks.getByName("licenseFlavor1Flavor3DebugReport")
    project.tasks.getByName("licenseFlavor1Flavor3ReleaseReport")
    project.tasks.getByName("licenseFlavor2Flavor4DebugReport")
    project.tasks.getByName("licenseFlavor2Flavor4ReleaseReport")
  }
}
