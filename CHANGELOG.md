# Changelog

## Version 1.2 - Bulk Add & Autostart Feature (2024-12-22)

### New Features

#### Bulk Add Videos Page
- **New Bulk Add screen** for adding multiple videos at once
- Multi-line text input supporting various formats:
  - One URL per line
  - Extracts YouTube URLs from mixed text
  - Automatic duplicate removal
  - Real-time count of detected videos
- **Progress tracking** with visual progress bar
- **Batch processing** with individual status logging
- Success/failure summary after completion

#### Autostart Download Option
- **Autostart checkbox** in Bulk Add screen
- When enabled, videos start downloading immediately
- When disabled, videos are added to pending queue (default)
- **Preference persistence** - setting is remembered across sessions
- Applied to both bulk adds and share intents
- Shows autostart status in activity log messages

#### API Enhancements
- Added `autostart` parameter support to API calls
- Bulk operation method for processing multiple URLs
- Individual error handling per video in bulk operations
- Progress callbacks for UI updates

### Technical Changes

#### New Components
- `BulkAddActivity.kt` - Bulk video addition interface
- `activity_bulk_add.xml` - Layout for bulk add screen

#### Modified Components
- `AppPreferences.kt` - Added autostart preference storage
- `TubeArchivistApi.kt` - Added autostart parameter and bulk processing method
- `ShareHandlerActivity.kt` - Uses autostart preference for shares
- `MainActivity.kt` - Added Bulk Add button
- `AndroidManifest.xml` - Registered BulkAddActivity

#### API Changes
- All API calls now support optional `autostart` parameter
- New `addMultipleToDownloadQueue()` method for bulk operations
- Enhanced error handling with per-video logging

### User Experience
- Easy bulk video addition from copied lists
- Flexible URL input (mixed text supported)
- Visual progress feedback during processing
- Complete activity logging for all operations
- Persistent autostart preference

---

## Version 1.1 - Activity Log Feature (2024-12-22)

### New Features

#### Activity Log Page
- **New Activity Log screen** accessible from the main menu
- Tracks all YouTube share attempts with detailed information:
  - Timestamp of each share
  - YouTube video ID
  - Full YouTube URL
  - Status indicators (Success ✓, Failed ✗, Pending ⏳)
  - Error messages for failed attempts
  
#### Visual Status Tracking
- **Color-coded status indicators**:
  - Green: Successfully added to queue
  - Red: Failed with error message
  - Orange: Request in progress
- **Real-time statistics** showing:
  - Total share attempts
  - Success count
  - Failure count
  - Pending count

#### Log Management
- Automatic storage of up to 100 most recent entries
- Clear all logs functionality with confirmation dialog
- Persistent storage across app restarts
- Chronological ordering (newest first)

### Technical Changes

#### New Components
- `ActivityLog.kt` - Data model for log entries
- `ActivityLogManager.kt` - Persistence manager using Gson
- `ActivityLogActivity.kt` - Activity log viewer with RecyclerView
- `activity_log.xml` - Layout for log screen
- `item_activity_log.xml` - Layout for individual log entries

#### Modified Components
- `MainActivity.kt` - Added Activity Log button
- `ShareHandlerActivity.kt` - Integrated logging for all share attempts
- `AndroidManifest.xml` - Registered new Activity
- `build.gradle.kts` - Added Gson and RecyclerView dependencies

#### Dependencies Added
- `com.google.code.gson:gson:2.10.1` - JSON serialization
- `androidx.recyclerview:recyclerview:1.3.2` - List display

### User Experience Improvements
- Users can now review their share history
- Easy troubleshooting with error messages
- Confirmation of successful shares
- No additional configuration required

### Storage
- Logs stored in SharedPreferences as JSON
- Maximum 100 entries (oldest auto-deleted)
- Survives app restarts
- Can be cleared manually by user

### Bug Fixes
- **Fixed API payload format**: Changed `data` parameter from object to array format to match TubeArchivist API specification
  - Old: `{"data": {"youtube_id": "...", "status": "pending"}}`
  - New: `{"data": [{"youtube_id": "...", "status": "pending"}]}`

---

## Version 1.0 - Initial Release

### Features
- Share YouTube videos to TubeArchivist
- Support for all YouTube URL formats
- Server configuration interface
- API token authentication
- Connection testing
- Background processing
- Toast notifications for feedback

### Components
- MainActivity
- SettingsActivity  
- ShareHandlerActivity
- AppPreferences
- TubeArchivistApi
