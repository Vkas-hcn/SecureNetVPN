package how.to.finish.the.project.tricevpn

import com.secure.net.vpn.data.SecureUpDataUtils
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {

        val inputString = "40,220,dsa,111,444" // 这个输入是数字的例子
        println("test-data=${SecureUpDataUtils.complexLogicCalculatedTrue(inputString)}") // 将总是打印true
    }
}