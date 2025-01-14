package environment

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.environment.Environment
import org.danceofvalkyries.environment.ProductionEnvironment
import org.danceofvalkyries.environment.TestEnvironment

class EnvironmentKtTest : FunSpec() {

    init {
        test("Should return prod environment") {
            val isProd = Environment("prod") is ProductionEnvironment
            isProd shouldBe true
        }

        test("Should return test environment"){
            val isTest =  Environment("test") is TestEnvironment
            isTest shouldBe true
        }
    }
}