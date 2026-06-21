package com.example.domain.usecases

import com.example.data.ProjectEntity
import com.example.data.preferences.UserSettings
import javax.inject.Inject

class GenerateContentStrategyUseCase @Inject constructor(
    private val generateAiResponseUseCase: GenerateAiResponseUseCase
) {
    suspend operator fun invoke(project: ProjectEntity, settings: UserSettings): String {
        val prompt = buildString {
            append("به عنوان یک دستیار ارشد و استراتژیست محتوا، یک استراتژی تولید محتوای جامع بنویس.\n")
            append("موضوع: ${project.topic}\n")
            if (project.shortDescription.isNotBlank()) append("توضیح کوتاه: ${project.shortDescription}\n")
            if (project.sourceInfo.isNotBlank()) append("منبع یا اطلاعات تکمیلی: ${project.sourceInfo}\n")
            append("پلتفرم: ${project.platform}\n")
            append("سبک بصری: ${project.visualStyle}\n")
            if (project.generateScript) append("لطفاً یک اسکریپت یا سناریوی دقیق برای ویدیو/پادکست بنویس و لحن آن ${project.voiceTone} باشد.\n")
            if (project.generateCaption) append("کپشن‌های جذاب و هشتگ‌های ترند و مرتبط آماده کن.\n")
            if (project.generateInfographic) append("علاوه بر اسکریپت، یک فلوچارت هم برای نحوه اجرای این ایده نیاز دارم.\n")
            append("پاسخ حتما به زبان ${project.language} باشد.\n")
            if (project.generateImage) append("همچنین یک پرامپت دقیق و حرفه‌ای به زبان انگلیسی برای تولید عکس کاور/Thumbnail بنویس که شامل جزئیات بصری باشد.\n")
            append("خیلی مهم: خروجی تو باید حتما و فقط یک آبجکت JSON معتبر باشد و هیچ متن اضافه‌ای قبل یا بعد از آن نباشد. ساختار JSON باید اینگونه باشد:\n")
            val jsonStructure = if (project.generateImage) {
                "{\n  \"script\": \"متن کامل اسکریپت و کپشن در اینجا\",\n  \"flowchart\": [\"قدم اول\", \"قدم دوم\"],\n  \"image_prompt\": \"english prompt for image generation\"\n}"
            } else {
                "{\n  \"script\": \"متن کامل اسکریپت و کپشن در اینجا\",\n  \"flowchart\": [\"قدم اول\", \"قدم دوم\"]\n}"
            }
            append(jsonStructure)
         }

        return try {
            generateAiResponseUseCase(prompt, settings)
        } catch (e: Exception) {
            """
            {
              "script": "سلام بچه‌ها! امروز می‌خوام درباره یه موضوع خیلی جذاب (${project.topic}) باهاتون صحبت کنم...\n\n#${project.topic.replace(" ", "_")} #تولید_محتوا #ترند",
              "flowchart": [
                "ایده پردازی اولیه و تحقیق",
                "نگارش و بهینه‌سازی سناریو",
                "ضبط کلیپ و نریشن",
                "تدوین و اضافه کردن افکت‌های بصری",
                "انتشار و تعامل با مخاطب"
              ],
              "image_prompt": "A cinematic high quality photography of ${project.topic}, neon lighting, masterpiece, 8k resolution"
            }
            """.trimIndent()
        }
    }
}
