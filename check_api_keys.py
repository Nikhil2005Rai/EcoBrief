import requests
import os

API_KEY = "AIzaSyDmUsdddnBbe4vQ0XWcUOjrEtuDCpgm1rM"

#AIzaSyBZvPGV7yvZP3xoo5RyCvLdQ1zIHW5h7nc
#AIzaSyDmUsdddnBbe4vQ0XWcUOjrEtuDCpgm1rM

if not API_KEY:
    print("Set GEMINI_API_KEY first")
    exit(1)

AUDIO_FILE = "audio.mp3"

# =========================
# STEP 1: Upload file
# =========================
upload_url = f"https://generativelanguage.googleapis.com/upload/v1beta/files?key={API_KEY}"

with open(AUDIO_FILE, "rb") as f:
    files = {
        "file": (AUDIO_FILE, f, "audio/mpeg")
    }

    print("📤 Uploading audio...")
    upload_response = requests.post(upload_url, files=files)

if upload_response.status_code != 200:
    print("❌ Upload failed")
    print(upload_response.text)
    exit(1)

upload_json = upload_response.json()
print("Upload response:", upload_json)

file_uri = upload_json["file"]["uri"]
print("✅ Uploaded:", file_uri)


# =========================
# STEP 2: Generate content
# =========================
generate_url = f"https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key={API_KEY}"

payload = {
    "contents": [
        {
            "role": "user",  # ✅ FIX
            "parts": [
                {
                    "file_data": {
                        "mime_type": "audio/mpeg",  # ✅ FIX
                        "file_uri": file_uri
                    }
                },
                {
                    "text": "Transcribe this audio clearly."
                }
            ]
        }
    ],
    "generationConfig": {
        "temperature": 0.2
    }
}

print("🧠 Generating transcription...")
response = requests.post(generate_url, json=payload)

if response.status_code != 200:
    print("❌ Generation failed")
    print(response.text)
    exit(1)

data = response.json()

try:
    text = data["candidates"][0]["content"]["parts"][0]["text"]
    print("\n✅ Transcription:\n")
    print(text)
except Exception:
    print("⚠️ Unexpected response:")
    print(data)