package com.kappdev.wordbook.share_feature.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextDirectionHeuristics
import android.text.TextPaint
import android.text.TextUtils
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.content.FileProvider
import com.kappdev.wordbook.R
import com.kappdev.wordbook.core.domain.model.Card
import com.kappdev.wordbook.core.domain.repository.CardRepository
import com.kappdev.wordbook.core.domain.repository.CollectionRepository
import com.kappdev.wordbook.core.presentation.util.getCurrentAppLocale
import com.kappdev.wordbook.share_feature.domain.model.CollectionPDFInfo
import com.kappdev.wordbook.share_feature.domain.repository.ShareCollectionAsPDF
import com.kappdev.wordbook.share_feature.domain.util.CollectionPDFResult
import com.kappdev.wordbook.theme.Graphite
import com.kappdev.wordbook.theme.LinenWhite
import com.kappdev.wordbook.theme.MediumGray
import java.io.File
import javax.inject.Inject


class ShareCollectionAsPDFImpl @Inject constructor(
    private val collectionRepository: CollectionRepository,
    private val cardRepository: CardRepository,
    private val context: Context
) : ShareCollectionAsPDF {

    private lateinit var layoutDirection: LayoutDirection
    private var localizedContext: Context

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            localizedContext = context
        } else {
            val currentLocale = getCurrentAppLocale()
            localizedContext = if (currentLocale != null) {
                val configuration = context.resources.configuration
                configuration.setLocale(currentLocale)
                context.createConfigurationContext(configuration)
            } else {
                context
            }
        }
    }

    override fun createCollectionPDF(collectionId: Int, layoutDirection: LayoutDirection): CollectionPDFResult {
        this.layoutDirection = layoutDirection

        val doc = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(PDF_PAGE_WIDTH, PDF_PAGE_HEIGHT, 1).create()

        val collectionInfo = collectionRepository.getCollectionPDFInfo(collectionId)
        val cards = cardRepository.getCollectionCards(collectionId)

        val page = doc.startPage(pageInfo)
        renderPDFCover(page.canvas, collectionInfo)
        doc.finishPage(page)

        cards.chunked(4).forEach { pageCards ->
            var dy = PAGE_PADDING
            val dynamicPage = doc.startPage(pageInfo)
            pageCards.forEach { card ->
                renderCard(dynamicPage.canvas, card, dy)
                dy += CARD_HEIGHT + PAGE_PADDING
            }
            renderWatermark(dynamicPage.canvas)
            doc.finishPage(dynamicPage)
        }

        val cachedPdfFile = cachePDF(doc, collectionInfo.name)
        val pdfUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", cachedPdfFile)
        return CollectionPDFResult.Success(pdfUri)
    }

    private fun renderWatermark(canvas: Canvas) {
        val textPaint = createDefaultTextPaint(14f, Typeface.DEFAULT_BOLD)
        textPaint.color = Color.Black.copy(0.64f).toArgb()

        val watermarkText = localizedContext.getString(R.string.watermark)
        val watermarkLayout = staticLayoutOf(watermarkText, textPaint, canvas.width, Layout.Alignment.ALIGN_CENTER)
        val watermarkTop = canvas.height - watermarkLayout.height - 8f
        canvas.drawStaticText(0f, watermarkTop, watermarkLayout)
    }

    private fun renderCard(canvas: Canvas, card: Card, dy: Float) {
        val cardRect = RectF(PAGE_PADDING, dy, canvas.width - PAGE_PADDING, dy + CARD_HEIGHT)

        // Draw card background
        val backgroundPaint = Paint()
        backgroundPaint.color = LinenWhite.toArgb()
        canvas.drawRect(cardRect, backgroundPaint)

        // Draw image
        if (card.image != null) {
            val bitmap = BitmapFactory.decodeFile(card.image)
            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, IMAGE_SIZE, IMAGE_SIZE, true)
            val imageLeft = if (layoutDirection.isRtl) cardRect.right - IMAGE_SIZE else cardRect.left
            canvas.drawBitmap(scaledBitmap, imageLeft, cardRect.top, null)
        }

        val actualImageSize = if (card.image != null) IMAGE_SIZE else 0
        val contentStart = if (layoutDirection.isRtl) {
            cardRect.left + CARD_CONTENT_PADDING
        } else {
            cardRect.left + actualImageSize + CARD_CONTENT_PADDING
        }
        val contentTop = cardRect.top + CARD_CONTENT_PADDING
        val contentWidth = cardRect.width() - actualImageSize - CARD_CONTENT_PADDING * 2

        // Draw info
        val textPaint = createDefaultTextPaint(16f, Typeface.DEFAULT_BOLD)

        // Draw term
        val termLayout = staticLayoutOf(card.term, textPaint, contentWidth.toInt())
        canvas.drawStaticText(contentStart, contentTop, termLayout)

        // Draw transcription
        var transcriptionBottom = contentTop + termLayout.height
        if (card.transcription.isNotBlank()) {
            textPaint.textSize = 12f
            textPaint.typeface = Typeface.DEFAULT
            val transcriptionLayout = staticLayoutOf(card.transcription, textPaint, contentWidth.toInt(), maxLines = 1)
            val transcriptionY = contentTop + termLayout.height + TRANSCRIPTION_PADDING
            canvas.drawStaticText(contentStart, transcriptionY, transcriptionLayout)
            transcriptionBottom += transcriptionLayout.height + TRANSCRIPTION_PADDING
        }

        // Draw definition
        textPaint.textSize = 14f
        textPaint.typeface = Typeface.DEFAULT_BOLD
        val transcriptionLayout = staticLayoutOf(card.definition, textPaint, contentWidth.toInt(), maxLines = 3)
        val definitionY = transcriptionBottom + DEFINITION_PADDING
        canvas.drawStaticText(contentStart, definitionY, transcriptionLayout)

        // Draw example
        if (card.example.isNotBlank()) {
            textPaint.color = MediumGray.toArgb()
            textPaint.textSize = 12f
            textPaint.typeface = Typeface.defaultFromStyle(Typeface.ITALIC)
            val exampleLayout = staticLayoutOf(card.example, textPaint, contentWidth.toInt())
            val exampleY = cardRect.bottom - exampleLayout.height - CARD_CONTENT_PADDING
            canvas.drawStaticText(contentStart, exampleY, exampleLayout)
        }
    }

    private fun renderPDFCover(canvas: Canvas, info: CollectionPDFInfo) {
        val width = canvas.width.toFloat()
        val height = canvas.height.toFloat()

        // Draw image
        val decodedImage = BitmapFactory.decodeResource(context.resources, R.drawable.art_connected_world)
        val desiredHeight = 300
        val desiredWidth = desiredHeight * (decodedImage.width.toFloat() / decodedImage.height.toFloat())
        val image = Bitmap.createScaledBitmap(decodedImage, desiredWidth.toInt(), desiredHeight, true)

        val imageLeft = (width / 2) - (image.width / 2)
        canvas.drawBitmap(image, imageLeft, PAGE_PADDING, null)

        // Draw text views
        val textPaint = createDefaultTextPaint(24f, Typeface.DEFAULT_BOLD)
        val textBoxWidth = (width - PAGE_PADDING * 2).toInt()

        // Collection Name
        val nameLayout = staticLayoutOf(info.name, textPaint, textBoxWidth, Layout.Alignment.ALIGN_CENTER)
        val nameY = height / 2 - nameLayout.height
        canvas.drawStaticText(PAGE_PADDING, nameY, nameLayout)

        // Collection Description
        textPaint.textSize = 16f
        val descriptionLayout = staticLayoutOf(info.description, textPaint, textBoxWidth, Layout.Alignment.ALIGN_CENTER)
        val descriptionY = height / 2 + 4
        canvas.drawStaticText(PAGE_PADDING, descriptionY, descriptionLayout)

        // Cards Count
        val cardsCountText = localizedContext.getString(R.string.contains_cards, info.cardsCount)
        textPaint.typeface = Typeface.DEFAULT
        val cardsCountLayout = staticLayoutOf(cardsCountText, textPaint, textBoxWidth, Layout.Alignment.ALIGN_CENTER)
        val cardsCountY = height - PAGE_PADDING - cardsCountLayout.height
        canvas.drawStaticText(PAGE_PADDING, cardsCountY, cardsCountLayout)
    }

    private fun staticLayoutOf(
        text: CharSequence,
        paint: TextPaint,
        width: Int,
        align: Layout.Alignment = Layout.Alignment.ALIGN_NORMAL,
        maxLines: Int = 2
    ): StaticLayout {
        return StaticLayout.Builder.obtain(text, 0, text.length, paint, width)
            .setAlignment(align)
            .setMaxLines(maxLines)
            .setEllipsize(TextUtils.TruncateAt.END)
            .setTextDirection(
                when (layoutDirection) {
                    LayoutDirection.Ltr -> TextDirectionHeuristics.LTR
                    LayoutDirection.Rtl -> TextDirectionHeuristics.RTL
                }
            )
            .build()
    }

    private fun Canvas.drawStaticText(dx: Float, dy: Float, layout: StaticLayout) {
        this.save()
        this.translate(dx, dy)
        layout.draw(this)
        this.restore()
    }

    private fun createDefaultTextPaint(fontSize: Float, typeface: Typeface): TextPaint {
        return TextPaint(TextPaint.ANTI_ALIAS_FLAG).apply {
            this.color = Graphite.toArgb()
            this.textSize = fontSize
            this.isAntiAlias = true
            this.typeface = typeface
        }
    }

    private fun cachePDF(pdf: PdfDocument, name: String): File {
        val pdfFile = File(context.externalCacheDir, "$name.pdf")
        val parentDirectory = pdfFile.parentFile

        if (parentDirectory?.exists() == false) {
            parentDirectory.mkdirs()
        }

        pdf.writeTo(pdfFile.outputStream())
        pdf.close()
        return pdfFile
    }

    private val LayoutDirection.isRtl: Boolean
        get() = (this == LayoutDirection.Rtl)

    companion object {
        /* Dimension For A4 Size Paper (1 inch = 72 points) */
        private const val PDF_PAGE_WIDTH = 595 //8.26 Inch
        private const val PDF_PAGE_HEIGHT = 842 //11.69 Inch

        private const val PAGE_PADDING = 20f

        private const val CARD_HEIGHT = 180f
        private const val CARD_CONTENT_PADDING = 8f

        private const val IMAGE_SIZE = 180

        private const val TRANSCRIPTION_PADDING = 4
        private const val DEFINITION_PADDING = 10
    }
}