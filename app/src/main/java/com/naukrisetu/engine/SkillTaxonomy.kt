package com.naukrisetu.engine

/**
 * Skill Equivalence and Taxonomy Engine for RozgarLink / NaukriSetu.
 *
 * Problem Statement Requirement:
 * "Recognise skills described in different words as the same skill. AI may extract skills
 * and judge how closely they correspond, and these judgements may inform ranking.
 * They must never decide eligibility, which follows the criteria stated by the opportunity."
 */
object SkillTaxonomy {

    /**
     * Canonical skill families. Any skill or synonym in a group shares a direct equivalence.
     */
    private val skillClusters: List<Set<String>> = listOf(
        // Electrician & Wiring Cluster
        setOf(
            "house wiring",
            "wiring",
            "electrician",
            "electrical wiring",
            "switchboard fitting",
            "switchboard installation",
            "domestic wiring",
            "industrial wiring",
            "electrical maintenance",
            "circuit breaker",
            "inverter wiring",
        ),
        // Fan & Small Motor Cluster
        setOf(
            "fan repair",
            "fan motor repair",
            "ceiling fan repair",
            "motor winding",
            "fan servicing",
            "cooler motor repair",
            "exhaust fan fitting",
        ),
        // AC & Refrigeration (HVAC) Cluster
        setOf(
            "ac repair & servicing",
            "ac servicing",
            "ac maintenance",
            "split-ac install",
            "split ac installation",
            "window ac repair",
            "ac gas charging",
            "gas top-up",
            "refrigeration",
            "fridge repair",
            "hvac technician",
            "cooling appliance repair",
        ),
        // Plumbing & Pipe Fitting Cluster
        setOf(
            "basic plumbing",
            "plumbing",
            "plumber",
            "pipe fitting",
            "pipeline repair",
            "drainage repair",
            "tap fitting",
            "sanitary fitting",
            "water tank connection",
        ),
        // Water Pump Cluster
        setOf(
            "pump repair",
            "water pump repair",
            "submersible pump fitting",
            "motor pump servicing",
            "monoblock repair",
        ),
        // Carpentry Cluster
        setOf(
            "carpentry",
            "carpenter",
            "wood work",
            "furniture repair",
            "door fitting",
            "cabinet making",
        ),
        // Painting Cluster
        setOf(
            "painting",
            "painter",
            "house painting",
            "wall painting",
            "whitewash",
            "putty application",
            "spray painting",
        ),
        // Welding & Fabrication Cluster
        setOf(
            "welding",
            "welder",
            "arc welding",
            "gas welding",
            "metal fabrication",
            "grill welding",
        ),
        // Masonry & Construction Cluster
        setOf(
            "masonry",
            "mason",
            "brick work",
            "plastering",
            "tile fitting",
            "civil work",
        ),
    )

    private fun normalize(str: String): String =
        str.trim().lowercase().replace(Regex("[^a-z0-9 ]"), " ").replace(Regex("\\s+"), " ")

    /**
     * Calculates the semantic/taxonomic similarity between candidate skill [have] and required skill [need].
     * Returns a float between 0.0f (no relation) and 1.0f (exact or identical meaning).
     */
    fun matchScore(have: String, need: String): Float {
        val h = normalize(have)
        val n = normalize(need)

        // 1. Exact normalized match
        if (h == n) return 1.0f

        // 2. Direct substring / word stem match (e.g. "plumbing" vs "basic plumbing")
        if (h.contains(n) || n.contains(h)) return 0.95f

        // 3. Cluster / Taxonomy lookup (e.g. "fan motor repair" vs "fan repair")
        for (cluster in skillClusters) {
            val hInCluster = cluster.any { normalize(it) == h || h.contains(normalize(it)) }
            val nInCluster = cluster.any { normalize(it) == n || n.contains(normalize(it)) }
            if (hInCluster && nInCluster) {
                // Related sub-skill within the exact same trade family
                return 0.85f
            }
        }

        // 4. Token overlap (e.g. "ac gas charging" and "ac repair")
        val hTokens = h.split(" ").filter { it.length > 2 }.toSet()
        val nTokens = n.split(" ").filter { it.length > 2 }.toSet()
        val overlap = hTokens.intersect(nTokens)
        if (overlap.isNotEmpty()) {
            return (overlap.size.toFloat() / maxOf(hTokens.size, nTokens.size).toFloat()) * 0.75f
        }

        return 0.0f
    }

    /**
     * Determines if a required skill is satisfied by a candidate's skill list,
     * either strictly or through taxonomic equivalence.
     */
    fun isSkillCovered(required: String, candidateSkills: List<String>, threshold: Float = 0.70f): Boolean {
        return candidateSkills.any { matchScore(it, required) >= threshold }
    }

    /**
     * Maps informal user text (or AI voice output) to the closest standard canonical skills.
     */
    fun findCanonicalEquivalents(query: String): List<String> {
        val normalizedQuery = normalize(query)
        if (normalizedQuery.isBlank()) return emptyList()

        return canonicalSkills()
            .map { canonical -> canonical to matchScore(normalizedQuery, canonical) }
            .filter { it.second >= 0.50f }
            .sortedByDescending { it.second }
            .map { it.first }
    }
}
