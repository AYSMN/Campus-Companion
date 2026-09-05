package com.example.data.local

import java.util.Calendar

object DefaultData {

    fun getDefaultMessMeals(): List<MessMealEntity> {
        val list = mutableListOf<MessMealEntity>()
        
        // Monday (Calendar.MONDAY = 2)
        list.add(MessMealEntity(
            id = 1L,
            dayOfWeek = Calendar.MONDAY,
            mealType = "BREAKFAST_MORNING",
            timeSlot = "7:00 AM – 9:30 AM",
            menuDescription = "Paratha, Aalu Chana, Cornflakes (Full Cream Milk 200ml), Tea, Banana"
        ))
        list.add(MessMealEntity(
            id = 2L,
            dayOfWeek = Calendar.MONDAY,
            mealType = "LUNCH",
            timeSlot = "12:30 PM – 2:30 PM",
            menuDescription = "Yellow Dal (Arhar), Rice, Roti, Sabji, Bhujia, Salad, Pickle, Seasonal Fruit"
        ))
        list.add(MessMealEntity(
            id = 3L,
            dayOfWeek = Calendar.MONDAY,
            mealType = "BREAKFAST_EVENING",
            timeSlot = "4:30 PM – 5:30 PM",
            menuDescription = "Tea, Biscuits (4 total: 2 sweet, 2 salty)"
        ))
        list.add(MessMealEntity(
            id = 4L,
            dayOfWeek = Calendar.MONDAY,
            mealType = "DINNER",
            timeSlot = "8:00 PM – 10:00 PM",
            menuDescription = "Dal (Sabut Mung), Veg Pulao, Roti, Shahi Paneer, Raita"
        ))

        // Tuesday (Calendar.TUESDAY = 3)
        list.add(MessMealEntity(
            id = 5L,
            dayOfWeek = Calendar.TUESDAY,
            mealType = "BREAKFAST_MORNING",
            timeSlot = "7:00 AM – 9:30 AM",
            menuDescription = "Poha, Sweet Daliya (Milk), Bread (2 pcs) with Butter/Jam, Sprouts, Banana, Tea"
        ))
        list.add(MessMealEntity(
            id = 6L,
            dayOfWeek = Calendar.TUESDAY,
            mealType = "LUNCH",
            timeSlot = "12:30 PM – 2:30 PM",
            menuDescription = "Rajma, Rice, Roti, Sabji, Bhujiya, Salad, Pickles, Seasonal Fruit"
        ))
        list.add(MessMealEntity(
            id = 7L,
            dayOfWeek = Calendar.TUESDAY,
            mealType = "BREAKFAST_EVENING",
            timeSlot = "4:30 PM – 5:30 PM",
            menuDescription = "Coffee, Biscuits (4 total: 2 sweet, 2 salty)"
        ))
        list.add(MessMealEntity(
            id = 8L,
            dayOfWeek = Calendar.TUESDAY,
            mealType = "DINNER",
            timeSlot = "8:00 PM – 10:00 PM",
            menuDescription = "Yellow Dal (Arhar), Sabji, Rice, Roti, Kheer, Salad"
        ))

        // Wednesday (Calendar.WEDNESDAY = 4)
        list.add(MessMealEntity(
            id = 9L,
            dayOfWeek = Calendar.WEDNESDAY,
            mealType = "BREAKFAST_MORNING",
            timeSlot = "7:00 AM – 9:30 AM",
            menuDescription = "Idli Sambhar, Coconut Chutney, Bread (2 pcs) with Butter/Jam, Cornflakes (Full Cream Milk 200ml), Banana, Tea"
        ))
        list.add(MessMealEntity(
            id = 10L,
            dayOfWeek = Calendar.WEDNESDAY,
            mealType = "LUNCH",
            timeSlot = "12:30 PM – 2:30 PM",
            menuDescription = "Yellow Dal (Arhar), Rice, Roti, Mix Veg, Bhujiya, Salad, Pickles, Seasonal Fruit"
        ))
        list.add(MessMealEntity(
            id = 11L,
            dayOfWeek = Calendar.WEDNESDAY,
            mealType = "BREAKFAST_EVENING",
            timeSlot = "4:30 PM – 5:30 PM",
            menuDescription = "Tea, Biscuits (4 total: 2 sweet, 2 salty)"
        ))
        list.add(MessMealEntity(
            id = 12L,
            dayOfWeek = Calendar.WEDNESDAY,
            mealType = "DINNER",
            timeSlot = "8:00 PM – 10:00 PM",
            menuDescription = "Dal Makhni, Sabji/Bhujiya, Rice, Roti, Sweet (1 Rasgulla + 1 Gulab Jamun)"
        ))

        // Thursday (Calendar.THURSDAY = 5)
        list.add(MessMealEntity(
            id = 13L,
            dayOfWeek = Calendar.THURSDAY,
            mealType = "BREAKFAST_MORNING",
            timeSlot = "7:00 AM – 9:30 AM",
            menuDescription = "Aalu Paratha, Plain Curd (100g), Bread (2 pcs) with Butter/Jam, Sprouts, Banana, Tea, Pickle"
        ))
        list.add(MessMealEntity(
            id = 14L,
            dayOfWeek = Calendar.THURSDAY,
            mealType = "LUNCH",
            timeSlot = "12:30 PM – 2:30 PM",
            menuDescription = "Chana Dal, Rice, Roti, Kofta (Lauki), Onion Pakoda (2 pcs), Papad, Salad, Pickles, Seasonal Fruit"
        ))
        list.add(MessMealEntity(
            id = 15L,
            dayOfWeek = Calendar.THURSDAY,
            mealType = "BREAKFAST_EVENING",
            timeSlot = "4:30 PM – 5:30 PM",
            menuDescription = "Coffee, Rusk (2 pcs)"
        ))
        list.add(MessMealEntity(
            id = 16L,
            dayOfWeek = Calendar.THURSDAY,
            mealType = "DINNER",
            timeSlot = "8:00 PM – 10:00 PM",
            menuDescription = "Arhar Dal Tadka, Rice, Puri, Mutter Paneer, Raita"
        ))

        // Friday (Calendar.FRIDAY = 6)
        list.add(MessMealEntity(
            id = 17L,
            dayOfWeek = Calendar.FRIDAY,
            mealType = "BREAKFAST_MORNING",
            timeSlot = "7:00 AM – 9:30 AM",
            menuDescription = "Puri, Black Chana, Halwa, Sprouts, Banana, Coffee"
        ))
        list.add(MessMealEntity(
            id = 18L,
            dayOfWeek = Calendar.FRIDAY,
            mealType = "LUNCH",
            timeSlot = "12:30 PM – 2:30 PM",
            menuDescription = "Kadhi Pakaura, Rice, Roti, Sabji, Bhujiya, Salad, Pickles, Seasonal Fruit"
        ))
        list.add(MessMealEntity(
            id = 19L,
            dayOfWeek = Calendar.FRIDAY,
            mealType = "BREAKFAST_EVENING",
            timeSlot = "4:30 PM – 5:30 PM",
            menuDescription = "Tea, Biscuits (4 total: 2 sweet, 2 salty)"
        ))
        list.add(MessMealEntity(
            id = 20L,
            dayOfWeek = Calendar.FRIDAY,
            mealType = "DINNER",
            timeSlot = "8:00 PM – 10:00 PM",
            menuDescription = "Sabut Masoor Dal (Chilka Wala), Mix Veg, Rice, Roti, Sewai"
        ))

        // Saturday (Calendar.SATURDAY = 7)
        list.add(MessMealEntity(
            id = 21L,
            dayOfWeek = Calendar.SATURDAY,
            mealType = "BREAKFAST_MORNING",
            timeSlot = "7:00 AM – 9:30 AM",
            menuDescription = "Chole Bhature (3 pcs, standard size), Cornflakes (Full Cream Milk 200ml) with Bread/Butter/Jam, Sprouts, Pickles, Banana, Coffee"
        ))
        list.add(MessMealEntity(
            id = 22L,
            dayOfWeek = Calendar.SATURDAY,
            mealType = "LUNCH",
            timeSlot = "12:30 PM – 2:30 PM",
            menuDescription = "Sabji, Rice, Roti, Yellow Dal (Arhar), Papad, Salad, Pickles, Seasonal Fruit"
        ))
        list.add(MessMealEntity(
            id = 23L,
            dayOfWeek = Calendar.SATURDAY,
            mealType = "BREAKFAST_EVENING",
            timeSlot = "4:30 PM – 5:30 PM",
            menuDescription = "Coffee, Biscuits (4 total: 2 sweet, 2 salty)"
        ))
        list.add(MessMealEntity(
            id = 24L,
            dayOfWeek = Calendar.SATURDAY,
            mealType = "DINNER",
            timeSlot = "8:00 PM – 10:00 PM",
            menuDescription = "Veg Biryani, Raita, Roti, Chana Dal Tadka, Veg Manchurian, Ice-Cream 80ml (summer) / Hot Gulab Jamun 2 pcs (winter)"
        ))

        // Sunday (Calendar.SUNDAY = 1)
        list.add(MessMealEntity(
            id = 25L,
            dayOfWeek = Calendar.SUNDAY,
            mealType = "BREAKFAST_MORNING",
            timeSlot = "7:00 AM – 9:30 AM",
            menuDescription = "Masala Dosa/Uttapam (2 pcs, standard size), Sambar, Coconut Chutney, Sprouts, Banana, Tea"
        ))
        list.add(MessMealEntity(
            id = 26L,
            dayOfWeek = Calendar.SUNDAY,
            mealType = "LUNCH",
            timeSlot = "12:30 PM – 2:30 PM",
            menuDescription = "Kadhi Pakaura, Rice, Roti, Sabji, Bhujiya, Papad, Salad, Pickles, Seasonal Fruit"
        ))
        list.add(MessMealEntity(
            id = 27L,
            dayOfWeek = Calendar.SUNDAY,
            mealType = "BREAKFAST_EVENING",
            timeSlot = "4:30 PM – 5:30 PM",
            menuDescription = "Tea, Biscuits (4 total: 2 sweet, 2 salty)"
        ))
        list.add(MessMealEntity(
            id = 28L,
            dayOfWeek = Calendar.SUNDAY,
            mealType = "DINNER",
            timeSlot = "8:00 PM – 10:00 PM",
            menuDescription = "Jeera Rice, Poori, Paneer Butter Masala, Chhole, Halwa (Suji/Moon Dal), Raita"
        ))

        return list
    }

    fun getDefaultClassSlots(): List<ClassSlotEntity> {
        val list = mutableListOf<ClassSlotEntity>()

        // Monday (Calendar.MONDAY = 2)
        list.add(ClassSlotEntity(
            dayOfWeek = Calendar.MONDAY,
            startTime = "10:30",
            endTime = "12:30",
            subjectCode = "EC031302 Lab",
            subjectName = "Analog Electronics Lab (Gr. II)",
            faculty = "Dr. Giridhar Gopal",
            roomOrLab = "EDC Lab",
            groupType = "GROUP_2",
            isLab = true
        ))
        list.add(ClassSlotEntity(
            dayOfWeek = Calendar.MONDAY,
            startTime = "13:30",
            endTime = "14:30",
            subjectCode = "EC031303",
            subjectName = "Network Theory",
            faculty = "Dr. Gaurav Varshney",
            roomOrLab = "LH217",
            groupType = "ALL",
            isLab = false
        ))
        list.add(ClassSlotEntity(
            dayOfWeek = Calendar.MONDAY,
            startTime = "14:30",
            endTime = "15:30",
            subjectCode = "EC031301",
            subjectName = "Digital Electronics",
            faculty = "Dr. Subodh Srivastava",
            roomOrLab = "LH217",
            groupType = "ALL",
            isLab = false
        ))

        // Tuesday (Calendar.TUESDAY = 3)
        // Two parallel group labs: Gr. II is EC031301 Lab, Gr. I is EC031302 Lab
        list.add(ClassSlotEntity(
            dayOfWeek = Calendar.TUESDAY,
            startTime = "10:30",
            endTime = "12:30",
            subjectCode = "EC031301 Lab",
            subjectName = "Digital Electronics Lab (Gr. II)",
            faculty = "Dr. Gaurav Varshney",
            roomOrLab = "DE Lab",
            groupType = "GROUP_2",
            isLab = true
        ))
        list.add(ClassSlotEntity(
            dayOfWeek = Calendar.TUESDAY,
            startTime = "10:30",
            endTime = "12:30",
            subjectCode = "EC031302 Lab",
            subjectName = "Analog Electronics Lab (Gr. I)",
            faculty = "Dr. Giridhar Gopal",
            roomOrLab = "EDC Lab",
            groupType = "GROUP_1",
            isLab = true
        ))
        list.add(ClassSlotEntity(
            dayOfWeek = Calendar.TUESDAY,
            startTime = "13:30",
            endTime = "14:30",
            subjectCode = "EC031303",
            subjectName = "Network Theory",
            faculty = "Dr. Gaurav Varshney",
            roomOrLab = "LH217",
            groupType = "ALL",
            isLab = false
        ))
        list.add(ClassSlotEntity(
            dayOfWeek = Calendar.TUESDAY,
            startTime = "14:30",
            endTime = "15:30",
            subjectCode = "CS031301",
            subjectName = "Data Structures",
            faculty = "Dr. Balaji Naik",
            roomOrLab = "LH217",
            groupType = "ALL",
            isLab = false
        ))
        list.add(ClassSlotEntity(
            dayOfWeek = Calendar.TUESDAY,
            startTime = "15:30",
            endTime = "17:30",
            subjectCode = "CS031301 Lab",
            subjectName = "Data Structures Lab",
            faculty = "Dr. Balaji Naik",
            roomOrLab = "CC Lab",
            groupType = "ALL",
            isLab = true
        ))

        // Wednesday (Calendar.WEDNESDAY = 4)
        list.add(ClassSlotEntity(
            dayOfWeek = Calendar.WEDNESDAY,
            startTime = "10:30",
            endTime = "12:30",
            subjectCode = "EC031304 Lab",
            subjectName = "Signal and Systems Lab (Gr. I)",
            faculty = "Dr. B. C. Sahana",
            roomOrLab = "DSP Lab",
            groupType = "GROUP_1",
            isLab = true
        ))
        list.add(ClassSlotEntity(
            dayOfWeek = Calendar.WEDNESDAY,
            startTime = "13:30",
            endTime = "14:30",
            subjectCode = "EC031301",
            subjectName = "Digital Electronics",
            faculty = "Dr. Subodh Srivastava",
            roomOrLab = "LH217",
            groupType = "ALL",
            isLab = false
        ))
        list.add(ClassSlotEntity(
            dayOfWeek = Calendar.WEDNESDAY,
            startTime = "14:30",
            endTime = "15:30",
            subjectCode = "EC031304",
            subjectName = "Signal and Systems",
            faculty = "Dr. B. C. Sahana",
            roomOrLab = "LH217",
            groupType = "ALL",
            isLab = false
        ))
        list.add(ClassSlotEntity(
            dayOfWeek = Calendar.WEDNESDAY,
            startTime = "15:30",
            endTime = "16:30",
            subjectCode = "CS031301",
            subjectName = "Data Structures",
            faculty = "Dr. Balaji Naik",
            roomOrLab = "LH217",
            groupType = "ALL",
            isLab = false
        ))
        list.add(ClassSlotEntity(
            dayOfWeek = Calendar.WEDNESDAY,
            startTime = "16:30",
            endTime = "17:30",
            subjectCode = "EC031302",
            subjectName = "Analog Electronics",
            faculty = "Dr. Girdhar Gopal",
            roomOrLab = "LH217",
            groupType = "ALL",
            isLab = false
        ))

        // Thursday (Calendar.THURSDAY = 5)
        list.add(ClassSlotEntity(
            dayOfWeek = Calendar.THURSDAY,
            startTime = "10:30",
            endTime = "12:30",
            subjectCode = "EC031304 Lab",
            subjectName = "Signal and Systems Lab (Gr. II)",
            faculty = "Dr. B. C. Sahana",
            roomOrLab = "DSP Lab",
            groupType = "GROUP_2",
            isLab = true
        ))
        list.add(ClassSlotEntity(
            dayOfWeek = Calendar.THURSDAY,
            startTime = "13:30",
            endTime = "14:30",
            subjectCode = "EC031304",
            subjectName = "Signal and Systems",
            faculty = "Dr. B. C. Sahana",
            roomOrLab = "LH217",
            groupType = "ALL",
            isLab = false
        ))
        list.add(ClassSlotEntity(
            dayOfWeek = Calendar.THURSDAY,
            startTime = "14:30",
            endTime = "15:30",
            subjectCode = "EC031302",
            subjectName = "Analog Electronics",
            faculty = "Dr. Girdhar Gopal",
            roomOrLab = "LH217",
            groupType = "ALL",
            isLab = false
        ))
        list.add(ClassSlotEntity(
            dayOfWeek = Calendar.THURSDAY,
            startTime = "15:30",
            endTime = "16:30",
            subjectCode = "CS031301",
            subjectName = "Data Structures",
            faculty = "Dr. Balaji Naik",
            roomOrLab = "LH217",
            groupType = "ALL",
            isLab = false
        ))
        list.add(ClassSlotEntity(
            dayOfWeek = Calendar.THURSDAY,
            startTime = "16:30",
            endTime = "17:30",
            subjectCode = "EC031303",
            subjectName = "Network Theory",
            faculty = "Dr. Gaurav Varshney",
            roomOrLab = "LH217",
            groupType = "ALL",
            isLab = false
        ))

        // Friday (Calendar.FRIDAY = 6)
        list.add(ClassSlotEntity(
            dayOfWeek = Calendar.FRIDAY,
            startTime = "10:30",
            endTime = "12:30",
            subjectCode = "EC031301 Lab",
            subjectName = "Digital Electronics Lab (Gr. I)",
            faculty = "Dr. Gaurav Varshney",
            roomOrLab = "DE Lab",
            groupType = "GROUP_1",
            isLab = true
        ))
        list.add(ClassSlotEntity(
            dayOfWeek = Calendar.FRIDAY,
            startTime = "13:30",
            endTime = "14:30",
            subjectCode = "EC031301",
            subjectName = "Digital Electronics",
            faculty = "Dr. Subodh Srivastava",
            roomOrLab = "LH217",
            groupType = "ALL",
            isLab = false
        ))
        list.add(ClassSlotEntity(
            dayOfWeek = Calendar.FRIDAY,
            startTime = "14:30",
            endTime = "15:30",
            subjectCode = "EC031302",
            subjectName = "Analog Electronics",
            faculty = "Dr. Girdhar Gopal",
            roomOrLab = "LH217",
            groupType = "ALL",
            isLab = false
        ))
        list.add(ClassSlotEntity(
            dayOfWeek = Calendar.FRIDAY,
            startTime = "15:30",
            endTime = "16:30",
            subjectCode = "EC031304",
            subjectName = "Signal and Systems",
            faculty = "Dr. B. C. Sahana",
            roomOrLab = "LH217",
            groupType = "ALL",
            isLab = false
        ))
        list.add(ClassSlotEntity(
            dayOfWeek = Calendar.FRIDAY,
            startTime = "16:30",
            endTime = "17:30",
            subjectCode = "EC031303",
            subjectName = "Network Theory",
            faculty = "Dr. Gaurav Varshney",
            roomOrLab = "LH217",
            groupType = "ALL",
            isLab = false
        ))

        return list
    }
}
