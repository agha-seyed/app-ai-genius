<div align="center">
  <img width="800" src="logo.jpg" alt="Smart Studio Banner" />
</div>

<h1 align="center">استودیو هوشمند محتوا (Smart Studio AI)</h1>

<p align="center">
  <strong>یک دستیار قدرتمند هوش مصنوعی برای ایده‌پردازی، تولید و مدیریت محتوا</strong>
</p>

## 🚀 معرفی نرم‌افزار
**استودیو هوشمند (Smart Studio)** یک اپلیکیشن پیشرفته‌ی اندرویدی است که به منظور تسهیل فرآیند تولید محتوا برای شبکه‌های اجتماعی (نظیر اینستاگرام، تیک‌تاک، یوتیوب و...) طراحی شده است. این نرم‌افزار با بهره‌گیری از مدل‌های پیشرفته هوش مصنوعی (مانند **Google Gemini**)، مانند یک دستیار خلاق و همه‌فن‌حریف در کنار شماست.
از نوشتن سناریوهای جذاب گرفته تا رسم فلوچارت‌های استراتژیک، تولید تصاویر با هوش مصنوعی و خواندن متن‌ها به صورت صوتی (TTS)، همگی در این نرم‌افزار با بالاترین کیفیت و در یک محیط کاربری بسیار مدرن ارائه شده‌اند.

## 👑 توسعه‌دهنده: تیم ویرانگر (Wirangar)
این نرم‌افزار به طور کامل و با افتخار توسط **تیم برنامه‌نویسی و هوش مصنوعی ویرانگر (Wirangar Team)** طراحی و توسعه یافته است.
تمام تلاش ما در تیم ویرانگر این بوده است که با ترکیب هنر طراحی رابط کاربری مدرن (UI/UX) و کدهای بهینه‌سازی شده، ابزاری خلق کنیم که نیازهای واقعی تولیدکنندگان محتوا را با سرعت و دقت بسیار بالا برطرف سازد.

## 🌟 قابلیت‌ها و امکانات نرم‌افزار
کارهایی که در این پروژه انجام شده و قابلیت‌هایی که به آن اضافه گردیده، به شرح زیر است:

- **🤖 تولید محتوای متنی پیشرفته:** دریافت کلیدواژه‌ها، لحن و پلتفرم از کاربر و ساخت سناریو و اسکریپت‌های ویدئویی ساختاریافته توسط هوش مصنوعی.
- **🎨 تولید تصویر با هوش مصنوعی:** اتصال به API هوش مصنوعی برای درک محتوای کاربر و تولید خودکار تصاویر جذاب و مرتبط با پروژه.
- **🎙️ تبدیل متن به گفتار (Text-to-Speech):** خواندن سناریوهای نوشته شده و تبدیل آن‌ها به فایل صوتی با استفاده از موتور پردازش صوتی بومی اندروید.
- **📊 رسم فلوچارت استراتژیک:** استخراج گام‌به‌گام مراحل اجرایی سناریو و ترسیم خودکار فلوچارت تعاملی در رابط کاربری نرم‌افزار.
- **🗣️ دستیار صوتی هوشمند (Voice Overlay):** یک دستیار صوتی شناور که در همه‌جای اپلیکیشن در دسترس است، صدای کاربر را ضبط می‌کند، درخواست‌ها را به هوش مصنوعی می‌فرستد و پاسخ‌ها را بازمی‌گرداند.
- **🌐 پشتیبانی کامل از بومی‌سازی (چندزبانگی):** قابلیت سوئیچ کردن زبان برنامه بین فارسی و انگلیسی به صورت آنی، به همراه راست‌چین (RTL) شدن خودکار تمام المان‌ها.
- **💾 ذخیره‌سازی محلی پروژه‌ها:** استفاده از دیتابیس قدرتمند برای ذخیره‌ی تمام پروژه‌های تولید شده، جهت دسترسی‌های بعدی به صورت آفلاین.
- **📱 رابط کاربری مدرن (Glassmorphism):** استفاده از المان‌های شیشه‌ای، رنگ‌بندی‌های چشم‌نواز نئونی و گرادیانت‌های جذاب با تکنولوژی Jetpack Compose.

## 🛠️ تکنولوژی‌ها و معماری
تیم ویرانگر برای ساخت این اپلیکیشن از به‌روزترین ابزارهای توسعه‌ی اندروید استفاده کرده است:
- **زبان برنامه‌نویسی:** Kotlin
- **طراحی رابط کاربری:** Jetpack Compose
- **معماری پروژه:** MVVM + StateFlow
- **تزریق وابستگی (Dependency Injection):** Dagger Hilt
- **مدیریت پایگاه داده:** Room Database
- **ارتباطات شبکه:** Retrofit & Moshi
- **مدیریت تصاویر:** Coil-Compose
- **سرویس‌های هوش مصنوعی:** Google Gemini API

## ⚙️ راهنمای اجرا و نصب
برای اجرای این پروژه روی سیستم خود مراحل زیر را طی کنید:
1. نرم‌افزار [Android Studio](https://developer.android.com/studio) را باز کرده و پوشه‌ی پروژه را `Open` کنید.
2. در مسیر اصلی پروژه (محل فایل `build.gradle.kts`) یک فایل به نام `.env` بسازید.
3. کلید API اختصاصی جمینای خود را در آن قرار دهید:
   ```env
   GEMINI_API_KEY=کلید_شما_در_اینجا
   ```
4. پروژه را سینک (Sync) کرده و سپس روی دستگاه یا شبیه‌ساز (Emulator) خود بیلد و اجرا کنید.

---
<p align="center">
  <b>توسعه یافته با نوآوری و قدرت توسط <a href="#">تیم ویرانگر (Wirangar)</a></b>
</p>

---

<h1 align="center">Smart Studio AI</h1>

<p align="center">
  <strong>A powerful AI assistant for content ideation, generation, and management</strong>
</p>

## 🚀 Introduction
**Smart Studio** is an advanced Android application designed to facilitate the content creation process for social media platforms (like Instagram, TikTok, YouTube, etc.). By leveraging advanced AI models (such as **Google Gemini**), this software acts as an all-around creative assistant right by your side.
From writing engaging scripts to drawing strategic flowcharts, generating AI images, and converting text to speech (TTS), everything is provided in this application with the highest quality and in a highly modern user interface.

## 👑 Developer: Wirangar Team
This software was fully designed and developed with pride by the **Wirangar Programming and AI Team**.
Our main goal at Wirangar has been to combine modern UI/UX design with optimized code to create a tool that meets the real needs of content creators with incredible speed and precision.

## 🌟 Features & Capabilities
The work done in this project and the features added include:

- **🤖 Advanced Text Generation:** Receives keywords, tone, and platform from the user and builds structured video scripts and scenarios using AI.
- **🎨 AI Image Generation:** Connects to AI image APIs to understand user context and automatically generate attractive and relevant images for the project.
- **🎙️ Text-to-Speech (TTS):** Reads the written scenarios and converts them to audio files using Android's native text-to-speech engine.
- **📊 Strategic Flowcharts:** Extracts step-by-step execution phases of the scenario and automatically draws interactive flowcharts in the app interface.
- **🗣️ Smart Voice Assistant (Voice Overlay):** A floating voice assistant available everywhere in the app. It records user commands via microphone, sends them to AI, and returns the responses.
- **🌐 Full Localization Support (Multilingual):** Ability to switch the app language between Persian and English instantly, along with automatic Right-to-Left (RTL) support for all elements.
- **💾 Local Project Storage:** Uses a powerful database to store all generated projects for future offline access.
- **📱 Modern UI (Glassmorphism):** Uses glass-like elements, eye-catching neon color schemes, and attractive gradients using Jetpack Compose technology.

## 🛠️ Technologies & Architecture
The Wirangar team has used the most up-to-date Android development tools to build this application:
- **Programming Language:** Kotlin
- **UI Toolkit:** Jetpack Compose
- **Architecture:** MVVM + StateFlow
- **Dependency Injection:** Dagger Hilt
- **Database Management:** Room Database
- **Network Communications:** Retrofit & Moshi
- **Image Management:** Coil-Compose
- **AI Services:** Google Gemini API

## ⚙️ Installation & Setup Guide
To run this project on your system, follow these steps:
1. Open [Android Studio](https://developer.android.com/studio) and `Open` the project folder.
2. In the root directory of the project (where the `build.gradle.kts` file is located), create a file named `.env`.
3. Place your dedicated Gemini API key in it:
   ```env
   GEMINI_API_KEY=your_api_key_here
   ```
4. Sync the project and then Build & Run it on your physical device or Emulator.

---
<p align="center">
  <b>Developed with innovation and power by the <a href="#">Wirangar Team</a></b>
</p>

---

<h1 align="center">Smart Studio AI (Italiano)</h1>

<p align="center">
  <strong>Un potente assistente AI per l'ideazione, la generazione e la gestione dei contenuti</strong>
</p>

## 🚀 Introduzione
**Smart Studio** è un'applicazione Android avanzata progettata per facilitare il processo di creazione di contenuti per le piattaforme di social media (come Instagram, TikTok, YouTube, ecc.). Sfruttando modelli AI avanzati (come **Google Gemini**), questo software agisce come un assistente creativo a tutto tondo sempre al tuo fianco.
Dalla scrittura di script coinvolgenti al disegno di diagrammi di flusso strategici, dalla generazione di immagini AI alla conversione del testo in voce (TTS), tutto è fornito in questa applicazione con la massima qualità e in un'interfaccia utente altamente moderna.

## 👑 Sviluppatore: Team Wirangar
Questo software è stato interamente progettato e sviluppato con orgoglio dal **Team di Programmazione e AI Wirangar**.
Il nostro obiettivo principale in Wirangar è stato quello di combinare il design UI/UX moderno con un codice ottimizzato per creare uno strumento che soddisfi le reali esigenze dei creatori di contenuti con una velocità e precisione incredibili.

## 🌟 Funzionalità e Capacità
Il lavoro svolto in questo progetto e le funzionalità aggiunte includono:

- **🤖 Generazione Avanzata di Testi:** Riceve parole chiave, tono e piattaforma dall'utente e costruisce script video strutturati e scenari utilizzando l'IA.
- **🎨 Generazione di Immagini AI:** Si connette alle API di immagini AI per comprendere il contesto dell'utente e generare automaticamente immagini attraenti e pertinenti per il progetto.
- **🎙️ Sintesi Vocale (TTS):** Legge gli scenari scritti e li converte in file audio utilizzando il motore nativo text-to-speech di Android.
- **📊 Diagrammi di Flusso Strategici:** Estrae le fasi di esecuzione passo-passo dello scenario e disegna automaticamente diagrammi di flusso interattivi nell'interfaccia dell'app.
- **🗣️ Assistente Vocale Intelligente (Voice Overlay):** Un assistente vocale fluttuante disponibile ovunque nell'app. Registra i comandi dell'utente tramite microfono, li invia all'IA e restituisce le risposte.
- **🌐 Supporto Completo alla Localizzazione (Multilingue):** Possibilità di cambiare la lingua dell'app tra persiano e inglese istantaneamente, insieme al supporto automatico da destra a sinistra (RTL) per tutti gli elementi.
- **💾 Archiviazione Locale dei Progetti:** Utilizza un potente database per archiviare tutti i progetti generati per futuri accessi offline.
- **📱 UI Moderna (Glassmorphism):** Utilizza elementi simili al vetro, schemi di colori neon accattivanti e gradienti attraenti utilizzando la tecnologia Jetpack Compose.

## 🛠️ Tecnologie e Architettura
Il team Wirangar ha utilizzato gli strumenti di sviluppo Android più aggiornati per costruire questa applicazione:
- **Linguaggio di Programmazione:** Kotlin
- **UI Toolkit:** Jetpack Compose
- **Architettura:** MVVM + StateFlow
- **Iniezione delle Dipendenze:** Dagger Hilt
- **Gestione del Database:** Room Database
- **Comunicazioni di Rete:** Retrofit & Moshi
- **Gestione delle Immagini:** Coil-Compose
- **Servizi AI:** Google Gemini API

## ⚙️ Guida all'Installazione e Configurazione
Per eseguire questo progetto sul tuo sistema, segui questi passaggi:
1. Apri [Android Studio](https://developer.android.com/studio) e clicca su `Open` per aprire la cartella del progetto.
2. Nella directory principale del progetto (dove si trova il file `build.gradle.kts`), crea un file chiamato `.env`.
3. Inserisci la tua chiave API Gemini dedicata:
   ```env
   GEMINI_API_KEY=inserisci_qui_la_tua_api_key
   ```
4. Sincronizza il progetto (Sync) e poi compila (Build) ed esegui (Run) sul tuo dispositivo fisico o emulatore.

---
<p align="center">
  <b>Sviluppato con innovazione e potenza dal <a href="#">Team Wirangar</a></b>
</p>
