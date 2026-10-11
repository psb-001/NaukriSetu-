package com.naukrisetu

import com.naukrisetu.engine.SkillTaxonomy
import com.naukrisetu.engine.skillMatchPct
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SkillTaxonomyTest {

    @Test
    fun `exact match scores 100 percent`() {
        val have = listOf("House wiring")
        val need = listOf("House wiring")
        assertEquals(100, skillMatchPct(have, need))
    }

    @Test
    fun `synonyms and informal words match through taxonomy`() {
        // "wiring" vs "House wiring"
        assertTrue(SkillTaxonomy.matchScore("wiring", "House wiring") >= 0.85f)

        // "electrician" vs "House wiring"
        assertTrue(SkillTaxonomy.matchScore("electrician", "House wiring") >= 0.80f)

        // "fan motor repair" vs "Fan repair"
        assertTrue(SkillTaxonomy.matchScore("fan motor repair", "Fan repair") >= 0.80f)

        // "pipe fitting" vs "Basic plumbing"
        assertTrue(SkillTaxonomy.matchScore("pipe fitting", "Basic plumbing") >= 0.80f)

        // "AC servicing" vs "AC Repair & Servicing"
        assertTrue(SkillTaxonomy.matchScore("AC servicing", "AC Repair & Servicing") >= 0.85f)
    }

    @Test
    fun `programming and tech student skills match across synonyms`() {
        // "react" vs "Web Development"
        assertTrue(SkillTaxonomy.matchScore("react", "Web Development") >= 0.80f)

        // "kotlin" vs "Android Development"
        assertTrue(SkillTaxonomy.matchScore("kotlin", "Android Development") >= 0.80f)

        // "machine learning" vs "Python"
        assertTrue(SkillTaxonomy.matchScore("machine learning", "Python") >= 0.80f)

        // "excel" vs "Data Entry & Excel"
        assertTrue(SkillTaxonomy.matchScore("excel", "Data Entry & Excel") >= 0.85f)

        // "figma" vs "Graphic Design"
        assertTrue(SkillTaxonomy.matchScore("figma", "Graphic Design") >= 0.80f)
    }

    @Test
    fun `unrelated skills score zero`() {
        val score = SkillTaxonomy.matchScore("Painting", "House wiring")
        assertEquals(0.0f, score, 0.01f)
    }

    @Test
    fun `candidate with differently phrased skills still gets high match score`() {
        val candidateHave = listOf("Wiring", "Fan motor repair", "Pipe fitting")
        val jobRequires = listOf("House wiring", "Fan repair")
        val score = skillMatchPct(candidateHave, jobRequires)
        assertEquals(100, score)
    }
}
