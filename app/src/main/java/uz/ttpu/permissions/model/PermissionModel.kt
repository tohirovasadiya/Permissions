package uz.ttpu.permissions.model

/**
 * 1-topshiriq: "Denied twice" ruxsatnoma modeli.
 *
 * @property granted Ruxsat berilganligi holati
 * @property rationale Rationale (tushuntirish) ko'rsatilishi kerakligi (faqat bir marta rad etilganda true)
 * @property dialogShows Ruxsat so'rov muloqot oynasi (dialog) ko'rsatilishi mumkinligi
 */
class PermissionModel {
    var granted: Boolean = false
        private set
    var rationale: Boolean = false
        private set
    var dialogShows: Boolean = true
        private set

    private var denyCount: Int = 0

    /**
     * Ruxsat so'rovini amalga oshirish.
     * @param userAllows Foydalanuvchi dialogda "Ruxsat berish" (true) yoki "Rad etish" (false) bosganligi
     * @return So'rov natijasi va dialog ko'ringan-ko'rinmaganligi haqida xabar
     */
    fun request(userAllows: Boolean): String {
        if (!dialogShows) {
            val message = "dialog yo'q, darhol rad"
            println("So'rov natijasi: $message -> [granted=$granted, rationale=$rationale, dialogShows=$dialogShows]")
            return message
        }

        if (userAllows) {
            granted = true
            rationale = false
            dialogShows = true
        } else {
            denyCount++
            granted = false
            if (denyCount == 1) {
                rationale = true
                dialogShows = true
            } else {
                // 2 marta rad etilgandan keyin dialog qayta ko'rinmaydi va rationale false bo'ladi
                rationale = false
                dialogShows = false
            }
        }

        val message = "So'rov bajarildi (userAllows=$userAllows) -> [granted=$granted, rationale=$rationale, dialogShows=$dialogShows]"
        println(message)
        return message
    }

    /**
     * Sozlamalardan (Settings) ruxsat berilganda holatni yangilash simulyatsiyasi.
     */
    fun grantFromSettings() {
        granted = true
        rationale = false
        dialogShows = true
        denyCount = 0
        println("Sozlamalardan ruxsat berildi -> [granted=$granted, rationale=$rationale, dialogShows=$dialogShows]")
    }

    override fun toString(): String {
        return "PermissionModel(granted=$granted, rationale=$rationale, dialogShows=$dialogShows)"
    }
}

fun main() {
    println("=== 1-topshiriq: PermissionModel holatlar o'tishi test simulyatsiyasi ===")
    val model = PermissionModel()
    println("Boshlang'ich holat: $model")

    println("\n1. Birinchi so'rov (Rad etish / Deny):")
    model.request(userAllows = false)

    println("\n2. Ikkinchi so'rov (Rad etish / Deny):")
    model.request(userAllows = false)

    println("\n3. Uchinchi so'rov (Qayta so'rov chaqiruvi):")
    model.request(userAllows = false)

    println("\n4. To'rtinchi so'rov (Ruxsat so'rashga urinish):")
    model.request(userAllows = true)

    println("\n=== Yakuniy holat ===")
    println(model)
}
