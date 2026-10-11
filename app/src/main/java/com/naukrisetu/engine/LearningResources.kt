package com.naukrisetu.engine

/**
 * Offline, zero-dependency learning-resource catalogue.
 *
 * When a seeker is NOT eligible for a role, the app shows *how to unlock it*:
 * each missing skill maps to 1–3 free, beginner-friendly resources
 * (YouTube / freeCodeCamp / official docs / SWAYAM-NPTEL for Indian students).
 *
 * No network calls, no new permissions — pure static data so the build stays
 * lightweight and works offline on the emulator.
 */
object LearningResources {

    private data class Res(
        val title: String,
        val provider: String,
        val weeks: Int,
        val free: Boolean = true,
    )

    private val catalogue: Map<String, List<Res>> = mapOf(
        "python" to listOf(
            Res("Python for Everybody (freeCodeCamp · 4h)", "freeCodeCamp YouTube", 2),
            Res("CS50P – Python Programming", "Harvard / edX (free audit)", 4),
            Res("Python Programming – NPTEL SWAYAM", "SWAYAM-NPTEL (free)", 4),
        ),
        "java" to listOf(
            Res("Java Full Course (freeCodeCamp · 4h)", "freeCodeCamp YouTube", 2),
            Res("Java Programming – NPTEL", "SWAYAM-NPTEL (free)", 4),
        ),
        "javascript" to listOf(
            Res("JavaScript Full Course for Beginners", "freeCodeCamp YouTube", 2),
            Res("Namaste JavaScript (Hindi/English)", "Akshay Saini YouTube", 2),
        ),
        "react" to listOf(
            Res("React Full Course – Build Projects", "freeCodeCamp YouTube", 3),
            Res("React Official Tutorial (Tic-Tac-Toe)", "react.dev docs", 1),
        ),
        "html/css" to listOf(
            Res("HTML + CSS Full Course", "freeCodeCamp YouTube", 2),
            Res("Responsive Web Design Certification", "freeCodeCamp (free)", 3),
        ),
        "web development" to listOf(
            Res("Responsive Web Design Certification", "freeCodeCamp (free)", 3),
            Res("Full Stack Open – Part 0-2", "University of Helsinki (free)", 3),
        ),
        "android" to listOf(
            Res("Android Basics with Compose", "Google Developers (free)", 3),
            Res("Android Kotlin Full Course", "freeCodeCamp YouTube", 2),
        ),
        "kotlin" to listOf(
            Res("Kotlin Full Course – JetBrains", "JetBrains Academy (free)", 2),
            Res("Kotlin for Java Developers", "Coursera audit (free)", 3),
        ),
        "mobile app development" to listOf(
            Res("Android Basics with Compose", "Google Developers (free)", 3),
            Res("Flutter vs Native – pick one path", "NaukriSetu guide", 1),
        ),
        "node.js" to listOf(
            Res("Node.js + Express Full Course", "freeCodeCamp YouTube", 2),
            Res("Full Stack Open – Backend", "University of Helsinki (free)", 2),
        ),
        "sql" to listOf(
            Res("SQL Full Course (4h)", "freeCodeCamp YouTube", 1),
            Res("SQLBolt – Interactive Lessons", "sqlbolt.com (free)", 1),
        ),
        "excel" to listOf(
            Res("Excel for Data Analysis", "freeCodeCamp YouTube", 1),
            Res("Excel Skills for Business (audit)", "Coursera audit (free)", 2),
        ),
        "problem solving" to listOf(
            Res("DSA in Python – 15 patterns", "NeetCode roadmap (free)", 4),
            Res("Striver A2Z DSA Sheet", "takeUforward (free)", 6),
        ),
        "git" to listOf(
            Res("Git & GitHub Crash Course", "Traversy Media YouTube", 1),
            Res("Pro Git Book – Chapters 1-3", "git-scm.com (free)", 1),
        ),
        "figma" to listOf(
            Res("Figma UI/UX Full Course", "freeCodeCamp YouTube", 2),
            Res("Figma Official Tutorials", "help.figma.com (free)", 1),
        ),
        "ui/ux design" to listOf(
            Res("Google UX Design – Course 1 (audit)", "Coursera audit (free)", 3),
            Res("Figma UI/UX Full Course", "freeCodeCamp YouTube", 2),
        ),
        "machine learning" to listOf(
            Res("Machine Learning – Andrew Ng (audit)", "Coursera audit (free)", 4),
            Res("Intro to ML – Kaggle micro-courses", "Kaggle Learn (free)", 2),
        ),
    )

    private val generic = listOf(
        Res("freeCodeCamp Full Curriculum", "freeCodeCamp (free)", 4),
        Res("NPTEL SWAYAM – pick your domain", "SWAYAM-NPTEL (free)", 4),
    )

    private fun normalise(skill: String): String = skill.trim().lowercase()

    /** Courses for one missing skill — never empty, so UI always has something to show. */
    fun coursesForSkill(skill: String): List<Course> {
        val key = normalise(skill)
        // direct hit, then substring hit (e.g. "React Native" → "react")
        val hits = catalogue[key]
            ?: catalogue.entries.firstOrNull { key.contains(it.key) || it.key.contains(key) }?.value
            ?: generic
        return hits.mapIndexed { i, r ->
            Course(
                code = "lr-${key.hashCode().toString().take(4).replace("-", "0")}-$i",
                title = r.title,
                provider = r.provider,
                weeks = r.weeks,
                skillsTaught = listOf(skill),
            )
        }
    }

    /** All resources for a role's missing skills, de-duplicated by title. */
    fun coursesForSkills(skills: List<String>): List<Course> =
        skills.flatMap { coursesForSkill(it) }
            .distinctBy { it.title }
            .take(6)
}
