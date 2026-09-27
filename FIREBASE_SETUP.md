# BYNEST realtime backend setup

The browser game is hosted on GitHub Pages, which is static hosting. To make lobbies and the leaderboard work between different devices, BYNEST uses Firebase Authentication (anonymous) and Firebase Realtime Database.

## 1. Create a Firebase project
Create a Firebase project, add a Web App, enable **Anonymous** sign-in, and create a **Realtime Database**.

## 2. Copy the web app config
Firebase provides a web configuration object containing values such as `apiKey`, `authDomain`, `databaseURL`, `projectId`, `storageBucket`, `messagingSenderId`, and `appId`.

Open `firebase-config.js` and replace the `PASTE_...` placeholders with that web config.

## 3. Set Realtime Database rules
In the Realtime Database Rules editor, use the contents of `firebase-rules.json`. These rules require a signed-in Firebase user for reads and writes; BYNEST signs players in anonymously.

## 4. Upload to GitHub Pages
Keep `index.html` and `firebase-config.js` in the repository root. Upload the whole BYNEST package to the same repository you are already using.

## 5. Test across devices
Open the GitHub Pages URL on device A and device B. Give the two players different names. Create a public lobby on device A; it should appear in **Available public rooms** on device B. The 5-character code can also be entered manually. Completed practice and multiplayer rounds are written to the global leaderboard.

## Notes
- GitHub Pages itself does not provide a writable shared database, so a realtime backend is required for cross-device multiplayer.
- The Firebase web configuration is placed in the public frontend. Database security comes from Firebase Authentication and the Realtime Database Rules, not from hiding the web config.
- Old lobbies are ignored after 24 hours by the client; you can add server-side cleanup later if needed.
