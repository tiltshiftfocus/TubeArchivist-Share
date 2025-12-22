# Quick Start Guide

## For Users

### 1. Install the App
- Download the APK from releases
- Install on your Android device
- Grant necessary permissions

### 2. Configure
- Open the app
- Tap "Settings"
- Enter your TubeArchivist server URL: `https://your-server.com`
- Enter your API token (from TubeArchivist Settings → Users)
- Tap "Test Connection"
- Tap "Save Settings"

### 3. Use

**Single Video:**
- Open any YouTube video
- Tap Share → "Share to TubeArchivist"
- Done! Check your TubeArchivist download queue

**Multiple Videos (Bulk Add):**
- Open the app
- Tap "Bulk Add Videos"
- Paste your list of YouTube URLs (one per line)
- Check "Autostart" if you want immediate downloads
- Tap "Add to Queue"
- Monitor progress and check Activity Log for results

## For Developers

### Quick Build

```bash
# Clone the project
git clone <repository-url>
cd TubeArchivist-Share

# Open in Android Studio
# OR build via command line:

# Debug build
./gradlew assembleDebug

# APK location:
# app/build/outputs/apk/debug/app-debug.apk
```

### Testing Locally

If testing with a local TubeArchivist instance:

1. Make sure your phone and server are on the same network
2. Use your server's local IP: `http://192.168.1.100:8000`
3. Enable clear text traffic (already configured in manifest)

### API Endpoints Used

**Test Connection:**
```bash
curl -H "Authorization: Token YOUR_TOKEN" \
     https://your-server.com/api/ping/
```

**Add to Queue:**
```bash
curl -X POST \
     -H "Authorization: Token YOUR_TOKEN" \
     -H "Content-Type: application/json" \
     -d '{"data":[{"youtube_id":"VIDEO_ID","status":"pending"}],"autostart":true}' \
     https://your-server.com/api/download/
```

Set `autostart` to `true` to begin downloads immediately, or `false` (or omit) to add to pending queue.

## Common Issues

### "Server URL must start with http:// or https://"
- Include the protocol: ✓ `https://example.com` ✗ `example.com`

### "Connection failed"
- Check server is running and accessible
- Verify API token is correct
- Check firewall settings
- Test from browser first

### App not in share menu
- Force stop and reopen the app
- Clear defaults: Settings → Apps → TubeArchivist Share → Clear Defaults

## Configuration Examples

### Local Network (Development)
```
Server URL: http://192.168.1.100:8000
API Token: your-token-here
```

### Remote Server (Production)
```
Server URL: https://tubearchivist.yourdomain.com
API Token: your-token-here
```

### With Reverse Proxy
```
Server URL: https://media.yourdomain.com/ta
API Token: your-token-here
```

## Need Help?

1. Check the full README.md
2. Test connection in Settings
3. Check TubeArchivist logs
4. Create an issue with error details
