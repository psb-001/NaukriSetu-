package com.naukrisetu.engine

enum class OppStatus { Live, Pending, Rejected }

enum class Role { Seeker, Employer, Verifier }

data class Seeker(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val city: String,
    val age: Int? = null,
    val experienceYears: Int? = null,
    val skills: List<String> = emptyList(),
    val certificates: List<String> = emptyList(),
    val role: String? = null,
    val goal: String? = null,
    val payMinimum: Int? = null,
)

/** Hard requirement on a posting. Seekers who fail are excluded, not ranked lower. */
sealed interface Criterion {
    data class City(val city: String) : Criterion
    data class Certificate(val name: String) : Criterion
    data class MinAge(val years: Int) : Criterion
    data class MinExperience(val years: Int) : Criterion
}

data class Opportunity(
    val id: String,
    val title: String,
    val employer: String,
    val city: String,
    val payMin: Int,
    val payMax: Int,
    val distanceKm: Double,
    val requiredSkills: List<String> = emptyList(),
    val criteria: List<Criterion> = emptyList(),
    val status: OppStatus = OppStatus.Live,
    val description: String = "",
    val workInvolved: String = "",
    val employerId: String = "",
)

data class Course(
    val code: String,
    val title: String,
    val provider: String,
    val weeks: Int,
    val skillsTaught: List<String> = emptyList(),
)

data class Ranked(
    val opportunity: Opportunity,
    val score: Int,
    val skillPct: Int,
    val reason: String,
)

data class Gap(
    val label: String,
    val kind: String,
    val courses: List<Course> = emptyList(),
)
