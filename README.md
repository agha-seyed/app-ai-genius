# AI Content Genius 🚀

AI Content Genius is a comprehensive, AI-powered android application built with Jetpack Compose designed to supercharge your content creation for various social media platforms like YouTube, Instagram, and TikTok. It acts as your personal AI content team—from brainstorming topics to writing scripts, generating visual assets, and even creating voiceovers.

## ✨ Key Features & Capabilities

Our application is not just a wrapper for a single API. It integrates multiple technologies to deliver a complete content generation pipeline:

### 🧠 1. Smart Content Strategy (Powered by Google Gemini / OpenRouter)
- **Customizable AI Providers:** Use Google Gemini, Groq, or OpenRouter for content generation.
- **Dynamic System Prompts:** Fine-tune the AI's behavior with custom system prompts.
- **Platform-Specific Outputs:** Generates optimized captions, scripts, and hashtags specifically tailored for your target platform (Instagram, YouTube, TikTok).
- **AI Personas:** Quick setup presets like "Tech Reviewer," "Motivational Speaker," or "Comedian" to instantly adjust tone, style, and platform settings.

### 🎨 2. AI Image & Visual Assets (Powered by Pollinations.ai)
- **Cinematic Poster Generation:** Automatically creates high-quality, cinematic posters based on your project topic.
- **Dynamic Aspect Ratios:** Generates images in the correct aspect ratio for your platform (e.g., 16:9 for YouTube, 9:16 for Reels/TikTok, 1:1 for Instagram).
- **YouTube Thumbnails:** Dedicated generation of thumbnail covers.

### 🗣️ 3. Advanced Voice Assistant & Text-to-Speech (TTS)
- **Local TTS Integration:** Uses Android's native Text-to-Speech engine.
- **Voice Customization:** Select your preferred voice, gender, and tone directly from the Settings.
- **Audio Export:** Download the generated AI voiceover directly to your local storage (`.wav` format) for use in video editors.
- **Floating Voice Assistant:** Navigate and interact with the app using voice commands.

### 💎 4. Premium UI/UX & Glassmorphism
- **Stunning Design:** Built with modern Glassmorphism aesthetics, featuring translucent cards, deep shadows, and ambient blurs.
- **Smooth Animations:** Includes slide-in and fade transitions powered by Jetpack Compose Navigation for a seamless user experience.
- **Dynamic Theming:** Supports both Dark Mode (custom amber/dark theme) and Light Mode seamlessly based on system settings.
- **Interactive Messaging:** Uses Snackbars and Toasts for elegant error handling and user feedback.

### 🔄 5. Export & Share
- **Instant Sharing:** Easily share your generated scripts, ideas, and AI images to other apps (WhatsApp, Telegram, etc.) using Android's native Share Intent.
- **Mock Cloud Sync & Billing:** Scalable architecture with placeholder repositories ready for Firebase Cloud Sync and Google Play Billing integration.

## 🛠️ Tech Stack
- **UI Toolkit:** Jetpack Compose (Material 3)
- **Architecture:** MVVM (Model-View-ViewModel) + Clean Architecture principles
- **Dependency Injection:** Hilt / Dagger
- **Local Database:** Room Database
- **Image Loading:** Coil
- **AI Integration:** Retrofit2 for REST APIs (OpenRouter, Groq, Pollinations)
- **Navigation:** Compose Navigation

## 🚀 Getting Started

1. Clone the repository.
2. Open the project in **Android Studio**.
3. Build and run the app on an emulator or physical device.
4. Go to **Settings** to configure your API Keys (Groq/OpenRouter) or use the default setup.
5. Create a new project, select a Persona, and watch the AI do the heavy lifting!
