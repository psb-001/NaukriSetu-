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

/** Split visible opps into (eligible ranked, ineligible with reasons). */
fun splitMatches(seeker: Seeker, opps: List<Opportunity>): Pair<List<Ranked>, List<Pair<Opportunity, List<String>>>> {
    val visible = seekerVisibleOpps(opps)
    val eligible = mutableListOf<Opportunity>()
    val ineligible = mutableListOf<Pair<Opportunity, List<String>>>()
    for (opp in visible) {
        val failures = checkEligibility(seeker, opp)
        if (failures.isEmpty()) eligible += opp else ineligible += opp to failures
    }
    return rankEligible(seeker, eligible) to ineligible
}

/** Hero number: live count of qualifying jobs — never hard-coded. */
fun homeHeroCount(seeker: Seeker, opps: List<Opportunity>): Int =
    splitMatches(seeker, opps).first.size

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
        val isStudent = seeker.role.equals("Student", ignoreCase = true) || seeker.role.equals("Trainee", ignoreCase = true)
        val reasonText = buildString {
            append("$topSkill ${skillPct}% fit")
            if (isStudent && (seeker.experienceYears ?: 0) == 0) append(" · Fresher friendly")
            if (opp.distanceKm > 0) append(" · ${"%.1f".format(opp.distanceKm)} km away")
            append(" · ₹${opp.payMin / 1000}k/mo")
        }
        Ranked(
            opportunity = opp,
            score = score,
            skillPct = skillPct,
            reason = reasonText,
        )
    }.sortedByDescending { it.score }
}

/**
 * Calculates skill match percentage using SkillTaxonomy semantic matching.
 * Satisfies G-01: "Recognise skills described in different words as the same skill."
 */
fun skillMatchPct(have: List<String>, need: List<String>): Int {
    if (need.isEmpty()) return 100
    if (have.isEmpty()) return 0
    var totalPoints = 0f
    for (req in need) {
        val bestScore = have.maxOfOrNull { candidateSkill ->
            SkillTaxonomy.matchScore(candidateSkill, req)
        } ?: 0f
        // Count as matched if equivalence is above 0.70 threshold
        totalPoints += if (bestScore >= 0.70f) 1.0f else bestScore
    }
    return ((totalPoints / need.size) * 100).toInt().coerceIn(0, 100)
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
    for (skill in opp.requiredSkills) {
        if (!SkillTaxonomy.isSkillCovered(skill, seeker.skills)) {
            gaps += Gap(label = skill, kind = "Skill", courses = coursesForSkill(skill))
        }
    }
    return gaps
}
