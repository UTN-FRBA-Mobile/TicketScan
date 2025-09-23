package com.example.ticketscan.repositories.store

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.ticketscan.domain.model.Store
import com.example.ticketscan.domain.repositories.store.StoreRepositorySQLite
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID
import kotlinx.coroutines.runBlocking

@RunWith(AndroidJUnit4::class)
class StoreRepositorySQLiteTest {
    private lateinit var repo: StoreRepositorySQLite
    private lateinit var context: Context

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        repo = StoreRepositorySQLite(context)
        val db = context.openOrCreateDatabase("ticketscan.db", Context.MODE_PRIVATE, null)
        db.execSQL("CREATE TABLE IF NOT EXISTS stores (id TEXT PRIMARY KEY, name TEXT NOT NULL, cuit INTEGER NOT NULL, location TEXT NOT NULL)")
        db.close()
    }

    @Test
    fun testInsertAndGetStore() = runBlocking {
        val store = Store(UUID.randomUUID(), "TestStore", 12345678901L, "TestLocation")
        assertTrue(repo.insertStore(store))
        val result = repo.getStoreById(store.id)
        assertNotNull(result)
        assertEquals(store.name, result?.name)
        assertEquals(store.cuit, result?.cuit)
        assertEquals(store.location, result?.location)
    }

    @Test
    fun testUpdateStore() = runBlocking {
        val store = Store(UUID.randomUUID(), "TestStore", 12345678901L, "TestLocation")
        repo.insertStore(store)
        val updated = store.copy(name = "UpdatedStore", location = "NewLocation")
        assertTrue(repo.updateStore(updated))
        val result = repo.getStoreById(store.id)
        assertEquals("UpdatedStore", result?.name)
        assertEquals("NewLocation", result?.location)
    }

    @Test
    fun testDeleteStore() = runBlocking {
        val store = Store(UUID.randomUUID(), "TestStore", 12345678901L, "TestLocation")
        repo.insertStore(store)
        assertTrue(repo.deleteStore(store.id))
        val result = repo.getStoreById(store.id)
        assertNull(result)
    }
}
