package com.naukrisetu.data

import com.naukrisetu.engine.Course
import com.naukrisetu.engine.Criterion
import com.naukrisetu.engine.OppStatus
import com.naukrisetu.engine.Opportunity
import com.naukrisetu.engine.Seeker

/** Demo seed: 3 seekers, 8 opportunities (mixed eligible/ineligible), 12 courses. */
object Seed {
    val demoSeeker = Seeker(
        id = "seeker-ravi",
        name = "Ravi Kadam",
        email = "ravi.kadam@gmail.com",
        phone = "9876543210",
        city = "Pune",
        age = 24,
        experienceYears = 2,
        skills = listOf("House wiring", "Fan repair", "Basic plumbing"),
        certificates = listOf("ITI Electrician Certificate"),
        payMinimum = 24000,
    )

    val opportunities: List<Opportunity> = listOf(
        Opportunity(
            id = "opp-voltas-ac",
            title = "AC Technician",
            employer = "Voltas Service Partner",
            city = "Pune",
            payMin = 26000,
            payMax = 26000,
            distanceKm = 3.2,
            requiredSkills = listOf("House wiring", "AC Repair & Servicing"),
            criteria = listOf(Criterion.City("Pune"), Criterion.Certificate("ITI Electrician Certificate")),
            status = OppStatus.Live,
            description = "Split-AC install, servicing and gas top-ups for residential customers.",
            workInvolved = "Field visits across Pune",
            employerId = "emp-voltas",
        ),
        Opportunity(
            id = "opp-fan-rbi",
            title = "Fan Repair Technician",
            employer = "RBI Colony Society",
            city = "Pune",
            payMin = 19000,
            payMax = 19000,
            distanceKm = 1.1,
            requiredSkills = listOf("Fan repair"),
            criteria = listOf(Criterion.City("Pune")),
            status = OppStatus.Live,
        ),
        Opportunity(
            id = "opp-plumb-rao",
            title = "Plumbing Helper",
            employer = "Rao Constructions",
            city = "Pune",
            payMin = 15500,
            payMax = 15500,
            distanceKm = 5.8,
            requiredSkills = listOf("Basic plumbing"),
            criteria = listOf(Criterion.City("Pune")),
            status = OppStatus.Live,
        ),
        Opportunity(
            id = "opp-wiring-kesari",
            title = "Wiring Assistant",
            employer = "Kesari Electricals",
            city = "Pune",
            payMin = 17000,
            payMax = 17000,
            distanceKm = 4.2,
            requiredSkills = listOf("House wiring"),
            criteria = listOf(Criterion.City("Pune")),
            status = OppStatus.Live,
        ),
        Opportunity(
            id = "opp-pump-water",
            title = "Pump Repair Helper",
            employer = "City Water Works",
            city = "Pune",
            payMin = 14000,
            payMax = 14000,
            distanceKm = 6.5,
            requiredSkills = listOf("Pump repair"),
            criteria = listOf(Criterion.City("Pune")),
            status = OppStatus.Live,
        ),
        Opportunity(
            id = "opp-mumbai-elec",
            title = "House Electrician",
            employer = "Sharma Enterprises",
            city = "Mumbai",
            payMin = 28000,
            payMax = 28000,
            distanceKm = 150.0,
            requiredSkills = listOf("House wiring"),
            criteria = listOf(Criterion.City("Mumbai")),
            status = OppStatus.Live,
        ),
        Opportunity(
            id = "opp-cooltech-ac",
            title = "AC Technician",
            employer = "CoolTech Services",
            city = "Pune",
            payMin = 27000,
            payMax = 27000,
            distanceKm = 2.4,
            requiredSkills = listOf("AC Repair & Servicing", "AC gas charging"),
            criteria = listOf(
                Criterion.City("Pune"),
                Criterion.Certificate("AC Repair & Servicing"),
            ),
            status = OppStatus.Live,
        ),
        Opportunity(
            id = "opp-sharma-site",
            title = "Site Electrician",
            employer = "Sharma Builders",
            city = "Pune",
            payMin = 24000,
            payMax = 24000,
            distanceKm = 7.1,
            requiredSkills = listOf("House wiring"),
            criteria = listOf(Criterion.City("Pune"), Criterion.MinExperience(3)),
            status = OppStatus.Live,
        ),
    )

    val courses: List<Course> = listOf(
        Course("ELEC-101", "House Wiring Fundamentals", "NSDC / Skill India", 4, listOf("House wiring")),
        Course("HVAC-201", "AC Repair & Servicing", "Voltas Academy", 8, listOf("AC Repair & Servicing", "Split-AC install")),
        Course("ELEC-104", "Fan Motor Repair", "NSDC / Skill India", 3, listOf("Fan repair", "Fan Motor Repair")),
        Course("PLMB-102", "Basic Plumbing", "ITI Pune", 6, listOf("Basic plumbing", "Pipe fitting")),
        Course("ELEC-102", "Switchboard Fitting", "NSDC / Skill India", 3, listOf("Switchboard fitting")),
        Course("HVAC-202", "Refrigeration Basics", "Voltas Academy", 6, listOf("Refrigeration")),
        Course("CARP-101", "Carpentry Foundations", "ITI Pune", 8, listOf("Carpentry")),
        Course("PANT-101", "House Painting", "ITI Pune", 4, listOf("Painting")),
        Course("WELD-101", "Arc Welding Basics", "NSDC / Skill India", 6, listOf("Welding")),
        Course("MSNY-101", "Masonry Essentials", "ITI Pune", 6, listOf("Masonry")),
        Course("PUMP-101", "Pump Repair & Maintenance", "City Water Works", 4, listOf("Pump repair")),
        Course("PLMB-103", "Advanced Pipe Fitting", "ITI Pune", 5, listOf("Pipe fitting")),
    )
}
