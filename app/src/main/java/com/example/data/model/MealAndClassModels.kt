package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class MealType(val displayName: String, val timeSlot: String, val defaultHour: Int, val defaultMinute: Int) {
    BREAKFAST_MORNING("Breakfast (Morning)", "7:00 AM – 9:30 AM", 7, 0),
    BREAKFAST_EVENING("Evening Snack & Tea", "4:30 PM – 5:30 PM", 16, 30),
    LUNCH("Lunch", "12:30 PM – 2:30 PM", 12, 30),
    DINNER("Dinner", "8:00 PM – 10:00 PM", 20, 0)
}

enum class LabGroup(val displayName: String, val shortName: String, val description: String) {
    GROUP_1("Group I", "Gr. I", "Roll No. 01 to 30"),
    GROUP_2("Group II", "Gr. II", "Roll No. 31 onwards"),
    ALL("All Students", "All", "Full batch Sec B")
}

data class SubjectInfo(
    val code: String,
    val name: String,
    val faculty: String,
    val defaultVenue: String,
    val color: Long,
    val isLab: Boolean = false
)

object SubjectsCatalog {
    val EC031304 = SubjectInfo("EC031304", "Signal and Systems", "Dr. B. C. Sahana", "LH217", 0xFF3B82F6)
    val EC031304_LAB = SubjectInfo("EC031304 Lab", "Signal and Systems Lab", "Dr. B. C. Sahana", "DSP Lab", 0xFF6366F1, isLab = true)
    
    val EC031301 = SubjectInfo("EC031301", "Digital Electronics", "Dr. Subodh Srivastava", "LH217", 0xFF10B981)
    val EC031301_LAB = SubjectInfo("EC031301 Lab", "Digital Electronics Lab", "Dr. Gaurav Varshney", "DE Lab", 0xFF059669, isLab = true)
    
    val EC031303 = SubjectInfo("EC031303", "Network Theory", "Dr. Gaurav Varshney", "LH217", 0xFFF59E0B)
    
    val EC031302 = SubjectInfo("EC031302", "Analog Electronics", "Dr. Girdhar Gopal", "LH217", 0xFFEC4899)
    val EC031302_LAB = SubjectInfo("EC031302 Lab", "Analog Electronics Lab", "Dr. Giridhar Gopal", "EDC Lab", 0xFFD946EF, isLab = true)
    
    val CS031301 = SubjectInfo("CS031301", "Data Structures", "Dr. Balaji Naik", "LH217", 0xFF8B5CF6)
    val CS031301_LAB = SubjectInfo("CS031301 Lab", "Data Structures Lab", "Dr. Balaji Naik", "CC Lab", 0xFF7C3AED, isLab = true)

    val ALL_SUBJECTS = listOf(
        EC031304, EC031304_LAB,
        EC031301, EC031301_LAB,
        EC031303,
        EC031302, EC031302_LAB,
        CS031301, CS031301_LAB
    )

    fun findSubject(code: String): SubjectInfo? {
        return ALL_SUBJECTS.firstOrNull { it.code.equals(code, ignoreCase = true) || "${it.code} Lab".equals(code, ignoreCase = true) }
    }
}
