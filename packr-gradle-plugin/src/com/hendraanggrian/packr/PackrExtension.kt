@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.packr

import com.badlogicgames.packr.PackrConfig
import java.io.File
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.kotlin.dsl.invoke

open class PackrExtension(private val project: Project) : VmArged {
    companion object {
        const val MINIMIZATION_SOFT = "soft"
        const val MINIMIZATION_HARD = "hard"
        const val MINIMIZATION_ORACLEJRE8 = "oraclejre8"
    }

    private val distributions: MutableSet<Distribution> = mutableSetOf()

    internal operator fun get(platform: PackrConfig.Platform): Distribution? =
        distributions.singleOrNull { it.platform == platform }

    /**
     * Name of the native executable, without extension such as `.exe`.
     * Default is project's name.
     */
    var executable: String? = null

    /**
     * File locations of the JAR files to package.
     * Default is empty.
     */
    var classpath: Iterable<File> = emptyList()

    /** Convenient method to set classpath from file path, relative to project directory. */
    fun classpath(vararg relativePaths: String) {
        classpath = relativePaths.map { project.projectDir.resolve(it) }
    }

    /**
     * File locations of JAR files to remove native libraries which do not match the target platform.
     * Default is empty.
     */
    var removePlatformLibs: Iterable<File> = emptyList()

    /** Convenient method to set remove platform libraries from file path, relative to project directory. */
    fun removePlatformLibs(vararg relativePaths: String) {
        removePlatformLibs = relativePaths.map { project.projectDir.resolve(it) }
    }

    /**
     * The fully qualified name of the main class, using dots to delimit package names.
     * Must be defined or will throw an exception.
     */
    var mainClass: String? = null

    override var vmArgs: Iterable<String> = emptyList()

    /**
     * List of files and directories to be packaged next to the native executable.
     * Default is empty.
     */
    var resources: Iterable<File> = emptyList()

    /** Convenient method to set resources from file path, relative to project directory. */
    fun resources(vararg relativePaths: String) {
        resources = relativePaths.map { project.projectDir.resolve(it) }
    }

    /**
     * Minimize the JRE by removing directories and files as specified by an additional config file.
     * Comes with a few config files out of the box.
     * Default is [MINIMIZATION_SOFT].
     */
    var minimizeJre: String = MINIMIZATION_SOFT

    /**
     * The output directory.
     * Default is `release` directory in project's build directory.
     */
    lateinit var outputDir: File

    /** Convenient method to set output directory from file path, relative to project directory. */
    var outputDirectory: String
        get() = outputDir.absolutePath
        set(value) {
            outputDir = project.projectDir.resolve(value)
        }

    /**
     * An optional directory to cache the result of JRE extraction and minimization.
     * Default is disabled.
     */
    var cacheJreDir: File? = null

    /** Convenient method to set JRE cache directory from file path, relative to project directory. */
    var cacheJreDirectory: String?
        get() = cacheJreDir?.absolutePath
        set(value) {
            cacheJreDir = value?.let { project.projectDir.resolve(it) }
        }

    /** An optional property which, when enabled, prints extra messages about JRE minimization. */
    var isVerbose: Boolean = false

    /** An optional property which, when enabled, opens [outputDir] upon packing completion. */
    var isAutoOpen: Boolean = false

    /** Enable macOS distribution with default configuration. */
    fun configureMacOS() {
        distributions += MacOSDistribution(project)
    }

    /**
     * Enable macOS distribution with customized [configuration].
     * Unlike other distributions, macOS configuration have some OS-specific properties.
     */
    fun configureMacOS(configuration: Action<MacOSDistributionBuilder>) {
        distributions += MacOSDistribution(project).also { configuration(it) }
    }

    /**
     * Enable macOS distribution with customized [configuration] in Kotlin DSL.
     * Unlike other distributions, macOS configuration have some OS-specific properties.
     */
    inline fun macOS(noinline configuration: MacOSDistributionBuilder.() -> Unit) = configureMacOS(configuration)

    /** Enable Windows 32-bit distribution with default configuration. */
    fun configureWindows32() {
        distributions += Distribution(project, PackrConfig.Platform.Windows32)
    }

    /** Enable Windows 32-bit distribution with customized [configuration]. */
    fun configureWindows32(configuration: Action<DistributionBuilder>) {
        distributions += Distribution(project, PackrConfig.Platform.Windows32).also { configuration(it) }
    }

    /** Enable Windows 32-bit distribution with customized [configuration] in Kotlin DSL. */
    inline fun windows32(noinline configuration: DistributionBuilder.() -> Unit) = configureWindows32(configuration)

    /** Enable Windows 64-bit distribution with default configuration. */
    fun configureWindows64() {
        distributions += Distribution(project, PackrConfig.Platform.Windows64)
    }

    /** Enable Windows 64-bit distribution with customized [configuration]. */
    fun configureWindows64(configuration: Action<DistributionBuilder>) {
        distributions += Distribution(project, PackrConfig.Platform.Windows64).also { configuration(it) }
    }

    /** Enable Windows 64-bit distribution with customized [configuration] in Kotlin DSL. */
    inline fun windows64(noinline configuration: DistributionBuilder.() -> Unit) = configureWindows64(configuration)

    /** Enable Linux 32-bit distribution with default configuration. */
    fun configureLinux32() {
        distributions += Distribution(project, PackrConfig.Platform.Linux32)
    }

    /** Enable Linux 32-bit distribution with customized [configuration]. */
    fun configureLinux32(configuration: Action<DistributionBuilder>) {
        distributions += Distribution(project, PackrConfig.Platform.Linux32).also { configuration(it) }
    }

    /** Enable Linux 32-bit distribution with customized [configuration] in Kotlin DSL. */
    inline fun linux32(noinline configuration: DistributionBuilder.() -> Unit) = configureLinux32(configuration)

    /** Enable Linux 64-bit distribution with default configuration. */
    fun configureLinux64() {
        distributions += Distribution(project, PackrConfig.Platform.Linux64)
    }

    /** Enable Linux 64-bit distribution with customized [configuration]. */
    fun configureLinux64(configuration: Action<DistributionBuilder>) {
        distributions += Distribution(project, PackrConfig.Platform.Linux64).also { configuration(it) }
    }

    /** Enable Linux 64-bit distribution with customized [configuration] in Kotlin DSL. */
    inline fun linux64(noinline configuration: DistributionBuilder.() -> Unit) = configureLinux64(configuration)
}
