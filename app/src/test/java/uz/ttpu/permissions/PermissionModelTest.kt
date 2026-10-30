package uz.ttpu.permissions

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import uz.ttpu.permissions.model.PermissionModel

class PermissionModelTest {

    @Test
    fun testDeniedTwiceFlow() {
        val model = PermissionModel()

        // Initial state
        assertFalse(model.granted)
        assertFalse(model.rationale)
        assertTrue(model.dialogShows)

        // 1-so'rov: rad etildi
        model.request(userAllows = false)
        assertFalse(model.granted)
        assertTrue("Rationale faqat 1 marta rad etilganda true bo'lishi kerak", model.rationale)
        assertTrue("1-rad etishdan keyin dialog hali ko'rsatiladi", model.dialogShows)

        // 2-so'rov: yana rad etildi
        model.request(userAllows = false)
        assertFalse(model.granted)
        assertFalse("2 marta rad etilgandan keyin rationale false bo'lishi kerak", model.rationale)
        assertFalse("2 marta rad etilgandan keyin dialogShows false bo'lishi kerak", model.dialogShows)

        // 3-so'rov: so'raldi, lekin dialog ko'rinmaydi ("dialog yo'q, darhol rad")
        val result3 = model.request(userAllows = false)
        assertEquals("dialog yo'q, darhol rad", result3)
        assertFalse(model.granted)
        assertFalse(model.dialogShows)

        // 4-so'rov: ruxsat berishga urinish bo'lsa ham dialog ko'rinmaydi
        val result4 = model.request(userAllows = true)
        assertEquals("dialog yo'q, darhol rad", result4)
        assertFalse(model.granted)
        assertFalse(model.dialogShows)
    }

    @Test
    fun testGrantFromSettings() {
        val model = PermissionModel()
        model.request(false)
        model.request(false)
        assertFalse(model.dialogShows)

        model.grantFromSettings()
        assertTrue(model.granted)
        assertTrue(model.dialogShows)
        assertFalse(model.rationale)
    }
}
