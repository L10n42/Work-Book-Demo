package com.kappdev.wordbook.share_feature.data.repository

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kappdev.wordbook.core.domain.model.Card
import com.kappdev.wordbook.core.domain.model.Collection
import com.kappdev.wordbook.core.domain.repository.CardRepository
import com.kappdev.wordbook.core.domain.repository.CollectionRepository
import com.kappdev.wordbook.core.domain.util.ZipUtil.zipContains
import com.kappdev.wordbook.share_feature.domain.repository.ShareCollection
import com.kappdev.wordbook.share_feature.domain.util.ImportCollectionResult
import com.kappdev.wordbook.share_feature.domain.util.ShareCollectionResult
import okio.use
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream
import javax.inject.Inject
import kotlin.random.Random

class ShareCollectionImpl @Inject constructor(
    private val context: Context,
    private val collectionRepository: CollectionRepository,
    private val cardRepository: CardRepository
) : ShareCollection {

    companion object {
        private const val ZIP_IMAGES_DIR = "images/"
        private const val ZIP_CARDS_JSON = "data/cards.json"
        private const val ZIP_COLLECTION_JSON = "data/collection.json"
    }

    private val appImagesDir = File(context.filesDir, "images")

    private val gson = Gson()
    private val cardsListType = object : TypeToken<List<Card>>() {}.type

    private lateinit var collection: Collection
    private lateinit var cards: List<Card>

    override suspend fun createCollectionZip(collectionId: Int): ShareCollectionResult {
        getCollection(collectionId)?.let { return it }
        getCards(collectionId)?.let { return it }

        val zipFile = generateCollectionZip()

        val shareCollectionUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", zipFile)
        return ShareCollectionResult.Success(shareCollectionUri)
    }

    private fun getCards(collectionId: Int): ShareCollectionResult.EmptyCollection? {
        cards = cardRepository.getCollectionCards(collectionId)
        return if (cards.isEmpty()) ShareCollectionResult.EmptyCollection else null
    }

    private suspend fun getCollection(collectionId: Int): ShareCollectionResult.CollectionError? {
        val shareCollection = collectionRepository.getCollectionById(collectionId)
        return if (shareCollection != null) {
            collection = shareCollection
            null
        } else ShareCollectionResult.CollectionError
    }

    override suspend fun importCollection(zipUri: Uri): ImportCollectionResult {
        val cachedZip = cacheZipFile(zipUri)

        if (cachedZip == null || !cachedZip.exists()) {
            return ImportCollectionResult.FileError
        }

        if (!hasDataFiles(cachedZip)) {
            cachedZip.delete()
            return ImportCollectionResult.WrongFile
        }

        val zipCollection = extractCollectionFromZip(cachedZip)
        val zipCards = extractCardsFromZip(cachedZip)
        if (zipCollection == null || zipCards == null) {
            return ImportCollectionResult.DataError
        }

        val imagePaths = extractImagesFromZip(cachedZip) ?: return ImportCollectionResult.DataError

        val newCollection = zipCollection.copy(
            id = 0,
            backgroundImage = zipCollection.backgroundImage?.let { imagePaths[it.substringAfterLast(File.separator)] }
        )
        val collectionId = collectionRepository.insertCollection(newCollection)
        val newCars = zipCards.map {
            it.copy(
                id = 0,
                collectionId = collectionId.toInt(),
                image = it.image?.let { oldPath -> imagePaths[oldPath.substringAfterLast(File.separator)] }
            )
        }
        cardRepository.insertCards(newCars)
        return ImportCollectionResult.Success
    }

    private fun extractCardsFromZip(zipFile: File): List<Card>? {
        return try {
            ZipFile(zipFile).use { zip ->
                val entry = zip.getEntry(ZIP_CARDS_JSON)
                zip.getInputStream(entry).use { inputStream ->
                    InputStreamReader(inputStream).use { reader ->
                        val cardsJson = reader.readText()
                        return gson.fromJson(cardsJson, cardsListType)
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun extractCollectionFromZip(zipFile: File): Collection? {
        return try {
            ZipFile(zipFile).use { zip ->
                val entry = zip.getEntry(ZIP_COLLECTION_JSON)
                zip.getInputStream(entry).use { inputStream ->
                    InputStreamReader(inputStream).use { reader ->
                        val collectionJson = reader.readText()
                        return gson.fromJson(collectionJson, Collection::class.java)
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    /** @return a map where key is old filename(with extension) and the value is new file path */
    private fun extractImagesFromZip(zipFile: File): Map<String, String>? {
        if (!appImagesDir.exists()) {
            appImagesDir.mkdirs()
        }
        try {
            val images = mutableMapOf<String, String>()
            ZipFile(zipFile).use { zip ->
                zip.forEachEntry { entry ->
                    if (entry.name.startsWith(ZIP_IMAGES_DIR)) {
                        val newImagePath = zip.extractImageEntryTo(entry, appImagesDir.path)
                        images[entry.name.substringAfterLast(File.separator)] = newImagePath
                    }
                }
            }
            return images
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    private fun ZipFile.forEachEntry(action: (ZipEntry) -> Unit) {
        this.entries().asSequence().forEach(action)
    }

    private fun ZipFile.extractImageEntryTo(entry: ZipEntry, destinationDir: String): String {
        getInputStream(entry).use { input ->
            val file = File(destinationDir, generateImageName())
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
            return file.absolutePath
        }
    }

    private fun generateImageName() = "IMG_${System.currentTimeMillis()}${Random.nextInt(100, 1000)}.jpg"

    private fun cacheZipFile(uri: Uri): File? {
        return try {
            val destinationFile = File(context.cacheDir, "${System.currentTimeMillis()}.zip")
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destinationFile).use { output ->
                    input.copyTo(output)
                }
            }
            destinationFile
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun hasDataFiles(zipFile: File): Boolean {
        return when {
            !zipContains(zipFile, ZIP_CARDS_JSON) -> false
            !zipContains(zipFile, ZIP_COLLECTION_JSON) -> false
            else -> true
        }
    }

    private fun generateCollectionZip(): File {
        val zipFile = File(context.externalCacheDir, "${collection.name}.zip")
        try {
            ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile))).use { output ->
                putCollectionToZip(output)
                putCardsToZip(output)
                putCollectionImageToZip(output)
                putCardImagesToZip(output)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return zipFile
    }

    private fun putCollectionImageToZip(zipOutput: ZipOutputStream) {
        collection.backgroundImage?.let { coverImagePath ->
            val imageFile = File(coverImagePath)
            copyFileToZip(imageFile, ZIP_IMAGES_DIR.plus(imageFile.name), zipOutput)
        }
    }

    private fun putCardImagesToZip(zipOutput: ZipOutputStream) {
        cards.forEach { card ->
            if (card.image != null) {
                val imageFile = File(card.image)
                copyFileToZip(imageFile, ZIP_IMAGES_DIR.plus(imageFile.name), zipOutput)
            }
        }
    }

    private fun putCardsToZip(zipOutput: ZipOutputStream) {
        val json = gson.toJson(cards, cardsListType)
        writeJsonToZip(json, ZIP_CARDS_JSON, zipOutput)
    }

    private fun putCollectionToZip(zipOutput: ZipOutputStream) {
        val json = gson.toJson(collection)
        writeJsonToZip(json, ZIP_COLLECTION_JSON, zipOutput)
    }

    private fun writeJsonToZip(json: String, name: String, zipOutput: ZipOutputStream) {
        zipOutput.putNextEntry(ZipEntry(name))
        zipOutput.write(json.toByteArray(Charsets.UTF_8))
        zipOutput.closeEntry()
    }

    private fun copyFileToZip(file: File, name: String, zipOutput: ZipOutputStream) {
        FileInputStream(file).use { input ->
            val entry = ZipEntry(name)
            zipOutput.putNextEntry(entry)
            input.copyTo(zipOutput)
            zipOutput.closeEntry()
        }
    }
}