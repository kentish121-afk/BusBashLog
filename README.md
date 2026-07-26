# Bus Bash Log

Personal **bus spotting / bash logbook** for Android.

Log fleet numbers and registrations you see or photograph, automatically look up details from [bustimes.org](https://bustimes.org), add notes, location and photos, then share straight to the [WM Bus Photos Forum](https://wmbusphotos.com/forum/).

## Features

- Quick add by fleet code or registration
- Auto-lookup of operator, type, livery, garage via bustimes.org
- Local log with date/time, notes, optional geotag
- Photo attachment support (gallery / camera)
- One-tap share to WM Bus Photos Forum (opens WMBP-Forum-Android or browser)
- Deep-link friendly with NextStopRealtime (copy fleet from a live departure and paste here)
- Material 3 UI

## Links to your other apps

- Pull vehicle info from the same data source as **NextStopRealtime** and **WMLiveBuses**
- Share / open in **WMBP-Forum-Android**

## Tech

- Kotlin + Jetpack Compose + Material 3
- Retrofit + kotlinx.serialization
- Local persistence (simple file / DataStore for starters – easy to upgrade to Room)
- Min SDK 26

## Build

Open in Android Studio → Sync → Run.

## Licence

Educational. Credit bustimes.org for vehicle data.
