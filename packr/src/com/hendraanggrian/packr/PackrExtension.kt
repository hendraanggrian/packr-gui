package com.hendraanggrian.packr

import com.badlogicgames.packr.PackrConfig
import com.badlogicgames.packr.PackrConfig.Platform
import com.badlogicgames.packr.PackrConfig.Platform.Linux32
import com.badlogicgames.packr.PackrConfig.Platform.Linux64
import com.badlogicgames.packr.PackrConfig.Platform.MacOS
import com.badlogicgames.packr.PackrConfig.Platform.Windows32
import com.badlogicgames.packr.PackrConfig.Platform.Windows64
import org.gradle.api.Project
import java.io.File
import kotlin.DeprecationLevel.ERROR

open class PackrExtension(val project: Project) {

    val platform: JDK = JDK()
    private var _executable: String = project.name
    private val _classpath: MutableList<String> = mutableListOf()
    private var _mainClass: String? = null
    private val _vmArgs: MutableList<String> = mutableListOf()
    private val _resources: MutableList<File> = mutableListOf()
    private var _minimizeJre: String = "soft"
    private var _outputDir: File = project.buildDir.resolve("release").resolve(_executable)
    private var _icon: File? = null
    private var _bundle: String? = null

    /**
     * Name of the native executable, without extension such as ".exe".
     * Default is project's name.
     */
    var executable: String
        @Deprecated(NO_GETTER, level = ERROR) get() = noGetter()
        set(value) {
            _executable = value
        }

    /**
     * File locations of the JAR files to package.
     */
    fun classpath(vararg jars: String) {
        _classpath += jars.map { File(project.projectDir, it).path }
    }

    /**
     * The fully qualified name of the main class, using dots to delimit package names.
     * Must be defined or else will throw an exception.
     */
    var mainClass: String
        @Deprecated(NO_GETTER, level = ERROR) get() = noGetter()
        set(value) {
            _mainClass = value
        }

    /**
     * List of arguments for the JVM, without leading dashes, e.g. "Xmx1G".
     */
    fun vmArgs(vararg args: String) {
        _vmArgs += args
    }

    /**
     * List of files and directories to be packaged next to the native executable.
     */
    fun resources(vararg resources: String) {
        _resources += resources.map { File(project.projectDir, it) }
    }

    /**
     * Minimize the JRE by removing directories and files as specified by an additional config file.
     * Comes with a few config files out of the box.
     * Default is `soft`.
     */
    var minimizeJre: String
        @Deprecated(NO_GETTER, level = ERROR) get() = noGetter()
        set(value) {
            _minimizeJre = value
        }

    /**
     * The output directory.
     * Default is `release` directory in project's build directory.
     */
    var outputDir: String
        @Deprecated(NO_GETTER, level = ERROR) get() = noGetter()
        set(value) {
            _outputDir = project.buildDir.resolve(value).resolve(_executable)
        }

    /**
     * Location of an AppBundle icon resource (.icns file).
     * This is an optional property.
     */
    var icon: String
        @Deprecated(NO_GETTER, level = ERROR) get() = noGetter()
        set(value) {
            _icon = project.projectDir.resolve(value)
        }

    /**
     * The bundle identifier of your Java application, e.g. "com.my.app".
     * This is an optional property.
     */
    var bundle: String
        @Deprecated(NO_GETTER, level = ERROR) get() = noGetter()
        set(value) {
            _bundle = value
        }

    internal fun toConfigs(): List<PackrConfig> = mutableListOf<PackrConfig>().apply {
        if (platform.mac != null) this += MacOS with platform.mac!!
        if (platform.windows32 != null) this += Windows32 with platform.windows32!!
        if (platform.windows64 != null) this += Windows64 with platform.windows64!!
        if (platform.linux32 != null) this += Linux32 with platform.linux32!!
        if (platform.linux64 != null) this += Linux64 with platform.linux64!!
    }

    private infix fun Platform.with(jdkPath: String): PackrConfig = PackrConfig().apply {
        platform = this@with
        jdk = jdkPath
        executable = _executable
        classpath = _classpath
        mainClass = _mainClass ?: error("Undefined main class")
        outDir = if (platform == MacOS) File(_outputDir.parent, "${_outputDir.name}.app") else _outputDir
        vmArgs = _vmArgs
        resources = _resources
        minimizeJre = _minimizeJre
        if (_icon != null) iconResource = _icon
        if (_bundle != null) bundleIdentifier = _bundle
    }

    data class JDK(
        var mac: String? = null,
        var windows32: String? = null,
        var windows64: String? = null,
        var linux32: String? = null,
        var linux64: String? = null
    )

    companion object {
        private const val NO_GETTER: String = "Property does not have a getter"

        private fun noGetter(): Nothing = throw UnsupportedOperationException(NO_GETTER)
    }
}