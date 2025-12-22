# TubeArchivist Share - Android App

An Android application written in Kotlin that allows you to share YouTube links directly to your self-hosted TubeArchivist download queue.

## Features

- 📱 Share YouTube videos from any app (YouTube, browsers, etc.)
- 🔗 Handles all YouTube URL formats (youtube.com, youtu.be, m.youtube.com)
- ⚙️ Simple configuration interface
- ✅ Connection testing
- 📊 Activity log to track all shares and API call statuses
- 📝 Bulk add multiple videos from a list
- 🚀 Autostart option to begin downloads immediately
- 🚀 Background processing with instant feedback
- 🔒 Secure token-based authentication

## Requirements

- Android 7.0 (API 24) or higher
- A running TubeArchivist instance with API access
- TubeArchivist API token

## Getting Your TubeArchivist API Token

1. Log in to your TubeArchivist web interface
2. Go to **Settings** → **Users**
3. Find or create an API token
4. Copy the token for use in the app

## Building the App

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer
- JDK 8 or higher
- Android SDK with API 34

### Build Steps

1. Clone or download this repository
2. Open the project in Android Studio
3. Wait for Gradle sync to complete
4. Build the APK:
   - For debug: `Build` → `Build Bundle(s) / APK(s)` → `Build APK(s)`
   - For release: `Build` → `Generate Signed Bundle / APK`

Or via command line:

```bash
# Debug build
./gradlew assembleDebug

# Release build (requires signing)
./gradlew assembleRelease
```

The APK will be located in: `app/build/outputs/apk/`

## Installation

1. Enable "Install from Unknown Sources" in Android settings
2. Transfer the APK to your device
3. Install the APK
4. Open the app

## Usage

### Initial Setup

1. Open the TubeArchivist Share app
2. Tap **Settings**
3. Enter your TubeArchivist server URL (e.g., `https://tubearchivist.example.com`)
4. Enter your API token
5. Tap **Test Connection** to verify
6. Tap **Save Settings**

### Sharing Videos

There are multiple ways to share videos:

#### Method 1: From YouTube App
1. Open a video in YouTube
2. Tap the **Share** button
3. Select **Share to TubeArchivist**
4. Done! The video is added to your download queue

#### Method 2: From Browser
1. Browse to a YouTube video
2. Tap the browser's share button
3. Select **Share to TubeArchivist**
4. Done!

#### Method 3: From Any Text
1. Copy a YouTube URL from anywhere
2. Share the text to TubeArchivist
3. The app will extract and send the URL

### Viewing Activity Log

1. Open the TubeArchivist Share app
2. Tap **Activity Log**
3. View all shared videos with their status:
   - ✓ Success (green) - Video successfully added to queue
   - ✗ Failed (red) - API call failed with error message
   - ⏳ Pending (orange) - Request in progress
4. See statistics: Total, Success, Failed, and Pending counts
5. Tap **Clear All Logs** to remove all entries

The app stores the last 100 log entries automatically.

### Bulk Adding Videos

1. Open the TubeArchivist Share app
2. Tap **Bulk Add Videos**
3. Enter YouTube URLs (one per line) in the text area
4. The app will show how many valid YouTube URLs it detected
5. Check **Autostart Download** if you want videos to start downloading immediately (otherwise they're added to pending queue)
6. Tap **Add to Queue**
7. Monitor progress as each video is processed
8. Check the Activity Log for detailed results

**Autostart Option:**
- **Checked**: Videos start downloading immediately
- **Unchecked**: Videos are added to the pending queue (default TubeArchivist behavior)
- This setting is remembered for future bulk adds and share intents

## Supported URL Formats

The app supports all common YouTube URL formats:

- `https://www.youtube.com/watch?v=VIDEO_ID`
- `https://youtube.com/watch?v=VIDEO_ID`
- `https://m.youtube.com/watch?v=VIDEO_ID`
- `https://youtu.be/VIDEO_ID`
- `https://www.youtube.com/embed/VIDEO_ID`
- `https://www.youtube.com/v/VIDEO_ID`

## TubeArchivist API Endpoints

The app uses the following TubeArchivist API endpoints:

- `GET /api/ping/` - Connection testing
- `POST /api/download/` - Add video to download queue
- `POST /api/download/?autostart=true` - Add video and start download immediately

### API Request Format

**Endpoint:** `POST /api/download/` or `POST /api/download/?autostart=true`

**Request Body:**
```json
{
  "data": [
    {
      "youtube_id": "VIDEO_ID",
      "status": "pending"
    }
  ]
}
```

**URL Parameters:**
- `autostart` (optional): Add `?autostart=true` to the URL to start downloads immediately (default: videos are added to pending queue)

**Body Parameters:**
- `data` (required): Array of video objects with `youtube_id` and `status`

Note: The `data` field is an array that can accept multiple video IDs in a single request. The `autostart` parameter is passed as a URL query parameter, not in the request body.

## Troubleshooting

### Connection Failed

- Verify your server URL is correct (include `http://` or `https://`)
- Check that your TubeArchivist instance is accessible from your phone
- Ensure your API token is valid
- Check firewall/network settings

### Videos Not Adding to Queue

- Test the connection in Settings first
- Check TubeArchivist logs for errors
- Verify the YouTube URL is valid
- Ensure you have space in your TubeArchivist instance

### App Not Showing in Share Menu

- Try force-stopping and reopening the app
- Clear Android's default app preferences
- Reinstall the app if necessary

## Security Notes

- API tokens are stored securely in Android's SharedPreferences
- The app supports both HTTP and HTTPS (use HTTPS in production)
- Network traffic is handled on background threads
- Sensitive data is not logged

### Adding Features

Some ideas for enhancements:

- ~~Activity log with status tracking~~ ✓ Implemented
- ~~Bulk add multiple videos~~ ✓ Implemented
- ~~Autostart downloads option~~ ✓ Implemented
- Playlist support (extract all videos from playlist URL)
- Queue management (view/cancel pending downloads)
- Video metadata preview before adding
- Multiple server profiles
- Download priority selection
- Export/import activity logs
- Notification on successful add
- Retry failed uploads from activity log
- Schedule downloads for specific times

## License

This project is open source. Feel free to modify and distribute.

## Contributing

Contributions are welcome! Please feel free to submit issues or pull requests.

## Acknowledgments

- Built with Kotlin and Android SDK
