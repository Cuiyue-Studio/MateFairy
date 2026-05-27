import com.pico.spatial.core.math.Quat
import com.pico.spatial.core.math.Vector3
import java.lang.reflect.Modifier

fun main() {
    println("--- Quat methods ---")
    Quat::class.java.methods.forEach {
        println("${Modifier.toString(it.modifiers)} ${it.returnType.simpleName} ${it.name}(${it.parameterTypes.joinToString { p -> p.simpleName }})")
    }
    println("--- Vector3 methods ---")
    Vector3::class.java.methods.forEach {
        println("${Modifier.toString(it.modifiers)} ${it.returnType.simpleName} ${it.name}(${it.parameterTypes.joinToString { p -> p.simpleName }})")
    }
}
