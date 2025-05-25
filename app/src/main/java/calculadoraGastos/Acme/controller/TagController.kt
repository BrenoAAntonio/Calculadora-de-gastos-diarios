package calculadoraGastos.Acme.controller

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import calculadoraGastos.Acme.database.AppDatabase
import calculadoraGastos.Acme.model.Tag
import calculadoraGastos.Acme.model.TagDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TagController(context: Context) {
    private val tagDao: TagDao = AppDatabase.getDatabase(context).tagDao()

    val allTags: LiveData<List<Tag>> = tagDao.getAll().asLiveData()

    fun insertTag(nome: String, onComplete: (Long) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val newTag = Tag(nome = nome)
            val id = tagDao.insert(newTag)
            launch(Dispatchers.Main) {
                onComplete(id)
            }
        }
    }

    fun updateTag(tag: Tag, onComplete: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            tagDao.update(tag)
            launch(Dispatchers.Main) {
                onComplete()
            }
        }
    }

    fun deleteTag(tag: Tag, onComplete: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            tagDao.delete(tag)
            launch(Dispatchers.Main) {
                onComplete()
            }
        }
    }

    suspend fun getTagById(id: Int): Tag? {
        return tagDao.getById(id)
    }

    suspend fun getTagByName(nome: String): Tag? {
        return tagDao.getByName(nome)
    }
}