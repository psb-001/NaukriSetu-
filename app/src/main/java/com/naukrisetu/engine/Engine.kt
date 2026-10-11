package com.naukrisetu.engine

import com.naukrisetu.data.Seed

/**
 * Eligibility gate first, rank survivors only.
 * checkEligibility returns failure reasons; empty list = eligible.
 */
fun checkEligibility(seeker: Seeker, opp: Opportunity): List<String> {
    val failures = mutableListOf<String>()
    for (c in opp.criteria) {
        when (c) {
            is Criterion.City -> if (!seeker.city.equals(c.city, ignoreCase = true)) {
                failures += "Needs ${c.city} address — you live in ${seeker.city}"
            }
            is Criterion.Certificate -> if (seeker.certificates.none { it.equals(c.name, ignoreCase = true) }) {
                failures += "Needs certificate: ${c.name}"
            }
            is Criterion.MinAge -> {
                val age = seeker.age
                if (age == null || age < c.years) failures += "Needs age ≥ ${c.years} years"
            }
            is Criterion.MinExperience -> {
                val exp = seeker.experienceYears ?: 0
                if (exp < c.years) failures += "Needs experience ≥ ${c.years} years — you have $exp"
            }
        }
    }
    return failures
}

/** Seekers only ever see Live postings — enforced here, not in the UI. */
fun seekerVisibleOpps(opps: List<Opportunity>): List<Opportunity> =
    opps.filter { it.status == OppStatus.Live }

fun seekerVisibleOpps(store: com.naukrisetu.data.DemoStore): List<Opportunity> =
    seekerVisibleOpps(store.allOpportunities())

/**
 * Ranks ONLY gate survivors. Throws on any ineligible input — callers must
 * filter with checkEligibility first. Weights: skill 50 / commute 20 / pay 15 / experience 15.
 */
fun rankEligible(seeker: Seeker, opps: List<Opportunity>): List<Ranked> {
    for (opp in opps) {
        val failures = checkEligibility(seeker, opp)
        require(failures.isEmpty()) {
            "rankEligible called with ineligible opportunity ${opp.id}: ${failures.joinToString("; ")}"
        }
    }
    return opps.map { opp ->
        val skillPct = skillMatchPct(seeker.skills, opp.requiredSkills)
        val skillPts = skillPct * 50 / 100
        val commutePts = when {
            opp.distanceKm <= 2.0 -> 20
            opp.distanceKm <= 5.0 -> 15
            opp.distanceKm <= 10.0 -> 10
            else -> 5
        }
        val payPts = if (seeker.payMinimum == null || opp.payMax >= seeker.payMinimum) 15 else 5
        val needExp = opp.criteria.filterIsInstance<Criterion.MinExperience>().maxOfOrNull { it.years } ?: 0
        val expPts = if ((seeker.experienceYears ?: 0) >= needExp) 15 else 5
        val score = (skillPts + commutePts + payPts + expPts).coerceIn(0, 100)
        val topSkill = opp.requiredSkills.firstOrNull() ?: "General"
        Ranked(
            opportunity = opp,
            score = score,
            skillPct = skillPct,
            reason = "$topSkill matches $skillPct% · ${"%.1f".format(opp.distanceKm)} km away",
        )
    }.sortedByDescending { it.score }
}

private fun skillMatchPct(have: List<String>, need: List<String>): Int {
    if (need.isEmpty()) return 100
    val h = have.map { it.lowercase() }.toSet()
    val matched = need.count { it.lowercase() in h }
    return matched * 100 / need.size
}

private fun coursesForSkill(skill: String): List<Course> =
    Seed.courses.filter { c -> c.skillsTaught.any { it.equals(skill, ignoreCase = true) } }

/** Missing items for (seeker, opportunity) — entry point is always a match card, never blank. */
fun gapsFor(seeker: Seeker, opp: Opportunity): List<Gap> {
    val gaps = mutableListOf<Gap>()
    for (c in opp.criteria) {
        when (c) {
            is Criterion.Certificate -> if (seeker.certificates.none { it.equals(c.name, ignoreCase = true) }) {
                gaps += Gap(label = c.name, kind = "Certificate", courses = coursesForSkill(c.name))
            }
            is Criterion.MinExperience -> {
                val exp = seeker.experienceYears ?: 0
                if (exp < c.years) gaps += Gap(
                    label = "Experience ≥ ${c.years} years",
                    kind = "Criterion",
                )
            }
            is Criterion.MinAge -> {
                val age = seeker.age
                if (age == null || age < c.years) gaps += Gap(label = "Age ≥ ${c.years} years", kind = "Criterion")
            }
            is Criterion.City -> if (!seeker.city.equals(c.city, ignoreCase = true)) {
                gaps += Gap(label = "${c.city} address", kind = "Criterion")
            }
        }
    }
    val have = seeker.skills.map { it.lowercase() }.toSet()
    for (skill in opp.requiredSkills) {
        if (skill.lowercase() !in have) {
            gaps += Gap(label = skill, kind = "Skill", courses = coursesForSkill(skill))
        }
    }
    return gaps
}
