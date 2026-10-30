# PermissionsLab - Android Permissions Amaliy Topshiriqlar Yechimi

Ushbu loyiha Android-da ruxsatnomalar (Permissions) bilan ishlashning barcha asosiy turlari va holatlarini namoyish etadi.

---

## 📱 Sinovdan o'tkazilgan API darajalari (Tested SDK Levels)

Loyiha quyidagi Android emulyatorlari va versiyalarida muvaffaqiyatli sinab ko'rildi:
- **API 30 (Android 11)** - Kamera va Location ruxsatnomalari uchun "Denied twice" va Rationale holatlari.
- **API 33 (Android 13)** - `POST_NOTIFICATIONS` runtime ruxsatnomasi.
- **API 34 (Android 14)** - Overlay (`SYSTEM_ALERT_WINDOW`) va "Only this time" bir martalik joylashuv ruxsatnomasi.

---

## 🔍 5-topshiriq Kuzatuv Natijasi ("Only this time" / "Faqat bu safar")

Joylashuv ruxsatnomasi so'ralganda foydalanuvchi **"Only this time" ("Faqat bu safar")** opsiyasini tanlasa, Android tizimi ilovaga faqat joriy seans va ilova ekranda (foreground) bo'lgan vaqt uchun vaqtincha ruxsat beradi. Ilova fonga o'tkazilganda yoki foydalanuvchi boshqa ilovaga o'tganda, Android operatsion tizimi ushbu ruxsatni avtomatik ravishda bekor qiladi (revoke). Shuning uchun ruxsat holatini `onCreate()` da bir marta emas, balki har safar ilova aktivlashganda `LifecycleEventEffect(Lifecycle.Event.ON_RESUME)` orqali qayta tekshirish o'ta muhim hisoblanadi. Ilovaga qaytilganida `ON_RESUME` hodisasi ruxsat bekor qilinganini zudlik bilan aniqlaydi va UI-ni "Ruxsat yo'q" holatiga o'tkazadi.

---

## 🔔 6-topshiriq Kuzatuv Natijasi (API < 33 vs API >= 33 Bildirishnomalar)

Android 12 va undan past SDK versiyalarida (API < 33) bildirishnomalar yuborish **Normal Permission** hisoblanib, ilova o'rnatilishi bilan avtomatik ravishda ruxsat beriladi va hech qanday runtime dialog talab qilinmaydi. Android 13 (API 33) va undan yuqori versiyalarda esa foydalanuvchi maxfiyligi va bezovtalanmasligini ta'minlash maqsadida **`POST_NOTIFICATIONS`** yangi runtime ruxsatnomasi sifatida kiritildi. API 33+ qurilmalarda ilova bildirishnoma yuborishdan oldin foydalanuvchidan dialog orqali ruxsat so'rashi shart.

---

## 📝 A Qism: Nazariy Savollar va Ma'ruza Tushunchalari Javoblari

### 1. Normal va Dangerous Permissionlar farqi nimada?
- **Normal Permissions** (masalan, `INTERNET`, `ACCESS_NETWORK_STATE`): Foydalanuvchi maxfiyligiga va shaxsiy ma'lumotlariga past darajada xavf tug'diradi. Manifestda e'lon qilinadi va ilova o'rnatilayotganda Android tizimi tomonidan avtomatik beriladi (runtime dialog talab qilmaydi).
- **Dangerous Permissions** (masalan, `CAMERA`, `ACCESS_FINE_LOCATION`): Foydalanuvchining shaxsiy ma'lumotlariga yoki apparat vositalariga (kamera, mikrafon, joylashuv) kirish imkonini beradi. Tizim ilova ishlayotgan vaqtda (runtime) foydalanuvchidan muloqot oynasi orqali ruxsat so'rashni talab qiladi.

### 2. "Denied Twice" modeli va Rationale tushunchasi
- Foydalanuvchi ruxsatni 1-marta rad etganda, `shouldShowRequestPermissionRationale()` `true` qiymat qaytaradi va ilova nima uchun bu ruxsat kerakligini tushuntiruvchi **Rationale** oynasini/matnini ko'rsatadi.
- Foydalanuvchi ruxsatni 2-marta rad etganda ("Don't ask again" yoki ketma-ket rad), `shouldShowRequestPermissionRationale()` `false` qiymat qaytaradi va tizim dialog oynasi boshqa qayta chiqmaydi ("dialog yo'q, darhol rad").
- Ushbu holatda foydalanuvchini faqat `Settings.ACTION_APPLICATION_DETAILS_SETTINGS` intanti orqali ilova sozlamalari oynasiga yo'naltirib, ruxsatni qo'lda yoqishini so'rash mumkin.

### 3. Special Permissions (`SYSTEM_ALERT_WINDOW`) xavfsizlik modeli
- Special permissionlar tizim darajasidagi yuqori huquqlarni talab qiladi. Shuning uchun ularni standart runtime dialog orqali olib bo'lmaydi.
- Dasturchi foydalanuvchini `Settings.ACTION_MANAGE_OVERLAY_PERMISSION` intenti orqali maxsus Sozlamalar sahifasiga yo'naltirishi kerak. Ilovaga qaytgach, `Settings.canDrawOverlays(context)` yordamida ruxsat berilganligi `ON_RESUME` da tekshiriladi.

---

## 🛠️ Loyihani Ishga Tushirish va Tekshirish

1. **PermissionModel Unit Testlarini yuritish:**
   ```bash
   ./gradlew test
   ```
2. **Loyiha binosini tekshirish:**
   ```bash
   ./gradlew assembleDebug
   ```
