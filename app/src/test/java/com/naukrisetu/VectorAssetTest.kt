package com.naukrisetu

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** Kombai illustration vectors must exist and be well-formed. */
class VectorAssetTest {

    private fun resFile(name: String): File {
        // Gradle runs unit tests with user.dir = module dir (app/).
        var dir = File(System.getProperty("user.dir"))
        while (dir.name != "app" && dir.parentFile != null) dir = dir.parentFile
        return File(dir, "src/main/res/drawable/$name")
    }

    private fun assertVector(name: String, minPaths: Int) {
        val f = resFile(name)
        assertTrue("$name missing at ${f.absolutePath}", f.exists())
        val text = f.readText()
        assertTrue("$name is not a vector", text.contains("<vector"))
        val paths = "<path".toRegex().findAll(text).count()
        assertTrue("$name has only $paths paths, expected >= $minPaths", paths >= minPaths)
    }

    @Test fun `splash illustration vector exists`() = assertVector("ill_splash_bridge.xml", 10)

    @Test fun `banner illustration vector exists`() = assertVector("ill_banner_ac.xml", 10)

    @Test fun `empty search illustration vector exists`() = assertVector("ill_empty_search.xml", 10)

    @Test fun `kyc shield illustration vector exists`() = assertVector("ill_kyc_shield.xml", 10)
}
