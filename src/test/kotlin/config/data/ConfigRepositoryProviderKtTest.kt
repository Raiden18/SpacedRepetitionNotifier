package config.data

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.config.data.ConfigRepositoryProvider
import org.danceofvalkyries.config.data.LocalFileConfigRepository

class ConfigRepositoryProviderKtTest : FunSpec() {
    init {
        test("Should return LocalFileRepository") {
            val isLocalFileConfigRepository = ConfigRepositoryProvider() is LocalFileConfigRepository
            isLocalFileConfigRepository shouldBe true
        }
    }
}