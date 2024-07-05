package com.kappdev.wordbook.main_feature.data.repository

import com.kappdev.wordbook.R
import com.kappdev.wordbook.core.domain.model.Card
import com.kappdev.wordbook.core.domain.model.Collection
import com.kappdev.wordbook.core.domain.repository.CardRepository
import com.kappdev.wordbook.core.domain.repository.CollectionRepository
import com.kappdev.wordbook.core.domain.util.Result
import com.kappdev.wordbook.main_feature.domain.repository.SampleDataProvider
import com.kappdev.wordbook.main_feature.domain.repository.StorageRepository
import com.kappdev.wordbook.theme.SampleCollectionColor
import java.util.Locale
import javax.inject.Inject

class SampleDataProviderImpl @Inject constructor(
    private val collectionsRepository: CollectionRepository,
    private val cardsRepository: CardRepository,
    private val storageRepository: StorageRepository
) : SampleDataProvider {

    override suspend fun insertSampleDataIfNeed() {
        if (collectionsRepository.hasData()) return

        val sampleCollection = Collection(
            name = "Your first collection",
            description = "Explore these sample cards to see how it works.",
            termLanguage = Locale.UK,
            definitionLanguage = Locale.UK,
            backgroundImage = null,
            color = SampleCollectionColor,
            created = System.currentTimeMillis(),
            lastEdit = System.currentTimeMillis()
        )

        val collectionId = collectionsRepository.insertCollection(sampleCollection)

        val dogImagePath = storageRepository.storeImageFromResources(R.raw.card_sample_dog)
        val dogSampleCard = Card(
            collectionId = collectionId.toInt(),
            term = "Dog",
            transcription = "/dɒɡ/",
            definition = "A common animal with four legs, especially kept by people as a pet or to guard things.",
            example = "We could hear dogs barking in the distance.",
            image = if (dogImagePath is Result.Success) dogImagePath.value else null,
            created = System.currentTimeMillis(),
            lastEdit = System.currentTimeMillis()
        )
        cardsRepository.insertCard(dogSampleCard)

        val catImagePath = storageRepository.storeImageFromResources(R.raw.card_sample_cat)
        val catSampleCard = Card(
            collectionId = collectionId.toInt(),
            term = "Cat",
            transcription = "/kæt/",
            definition = "A small animal with fur, four legs, a tail, and claws, usually kept as a pet.",
            example = "The cat slept on the windowsill.",
            image = if (catImagePath is Result.Success) catImagePath.value else null,
            created = System.currentTimeMillis(),
            lastEdit = System.currentTimeMillis()
        )
        cardsRepository.insertCard(catSampleCard)

        val lionImagePath = storageRepository.storeImageFromResources(R.raw.card_sample_lion)
        val lionSampleCard = Card(
            collectionId = collectionId.toInt(),
            term = "Lion",
            transcription = "/ˈlaɪ.ən/",
            definition = "A large wild animal of the cat family with yellowish-brown fur that lives in Africa and Asia.",
            example = "",
            image = if (lionImagePath is Result.Success) lionImagePath.value else null,
            created = System.currentTimeMillis(),
            lastEdit = System.currentTimeMillis()
        )
        cardsRepository.insertCard(lionSampleCard)

        val bearImagePath = storageRepository.storeImageFromResources(R.raw.card_sample_bear)
        val bearSampleCard = Card(
            collectionId = collectionId.toInt(),
            term = "Bear",
            transcription = "/beər/",
            definition = "A large, strong, omnivorous mammal with thick fur found in forests and mountains.",
            example = "The bear caught a fish in the river.",
            image = if (bearImagePath is Result.Success) bearImagePath.value else null,
            created = System.currentTimeMillis(),
            lastEdit = System.currentTimeMillis()
        )
        cardsRepository.insertCard(bearSampleCard)

        val horseImagePath = storageRepository.storeImageFromResources(R.raw.card_sample_horse)
        val horseSampleCard = Card(
            collectionId = collectionId.toInt(),
            term = "Horse",
            transcription = "/hɔːs/",
            definition = "A large animal with four legs that people ride on or use for carrying things.",
            example = "",
            image = if (horseImagePath is Result.Success) horseImagePath.value else null,
            created = System.currentTimeMillis(),
            lastEdit = System.currentTimeMillis()
        )
        cardsRepository.insertCard(horseSampleCard)
    }

}