# Verse Track

Verse Track is a modern, local-first Android application designed for organizing and storing Bible verses by theme. It features seamless synchronization with Supabase and robust offline support.

## 🚀 Features

### 📖 Organise by Theme
- Group your favorite verses under custom themes (e.g., *Faith*, *Hope*, *Strength*).
- **Dashboard View**: A card-based grid layout showing a preview of recently added verses for each theme.

### 📝 Versatile Verse Input
- **Manual Entry**: Structured input with a searchable Catholic Bible book autocomplete (all 73 books included).
- **Smart Share Integration**: Share text directly from external Bible apps (like YouVersion). The app automatically:
    - Parses the reference and content.
    - Cleans up Bible version abbreviations (e.g., "RSV-C").
    - Strips unnecessary links.
- **Structured Formatting**: Support for multi-line content and proper chapter/verse range formatting.

### 🔄 Local-First & Cloud Sync
- **Local Storage**: Powered by **Room Database** for instant access and offline usability.
- **Supabase Integration**: Real-time cloud backup using Supabase Postgrest.
- **Background Sync**: Uses **WorkManager** to handle retries and ensure data is synced only when a connection is available, without draining your battery.

### 🛠️ Data Management
- **Edit & Update**: Full support for editing existing verses.
- **Safety First**: Confirmation dialogs for all deletion actions to prevent accidental data loss.
- **UUIDs**: Uses universally unique identifiers to prevent ID conflicts during synchronization.

## 🛠 Technical Details

### Architecture & Libraries
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Database**: Room Persistence Library
- **Backend-as-a-Service**: Supabase (via `supabase-kt`)
- **Networking**: Ktor Client
- **Background Tasks**: WorkManager
- **Serialization**: Kotlinx Serialization
- **Concurrency**: Kotlin Coroutines & Flow

### Key Components
- **Repository Pattern**: Centralised data logic managing the flow between Room and Supabase.
- **ViewModel**: State management using `StateFlow` and `SharingStarted.WhileSubscribed`.
- **Custom Search logic**: Searchable dropdowns for book selection using `OutlinedTextField` and `DropdownMenu`.
- **Intent Handling**: Capturing `ACTION_SEND` intents for seamless data import.

## 📦 Setup & Installation

1. **Supabase Configuration**:
    - Update `SupabaseConfig.kt` with your `URL` and `ANON_KEY`.
    - Set up the `notes` and `verses` tables in your Supabase project (SQL scripts available in the project documentation).
2. **Environment**:
    - Minimum SDK: 24
    - Target SDK: 36
    - Kotlin: 2.0.21
    - Gradle: 8.13.2

---
Developed as a Catholic-friendly tool for scripture study and meditation.
