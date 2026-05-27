import java.io.File
import java.util.zip.ZipFile

fun main() {
    val dir = File("/Users/bytedance/.gradle/caches/modules-2/files-2.1/")
    dir.walkTopDown().filter { it.extension == "jar" && it.absolutePath.contains("com.pico.spatial") }.forEach { jar ->
        ZipFile(jar).use { zip ->
            zip.entries().asSequence().forEach { entry ->
                if (entry.name.contains("LocalSpatialNavigator")) {
                    println("Found ${entry.name} in ${jar.absolutePath}")
                }
            }
        }
    }
}
