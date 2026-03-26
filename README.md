# 🎬 ASCII Media Renderer 
CLI tool for rendering images and videos as ASCII in terminal.

## ⚙️ Dependencies
- Java 25.0.2 ☕  
- Gradle 9.4.0 🛠️  
- yt-dlp 2026.03.17 📺  

## ✨ Features
* 🖼️ Image to ASCII
* 🎥 Video to ASCII
* 📺 YouTube URL to ASCII
* 🔎 YouTube search to ASCII

## 📦 Installation
```bash id="0whp2z"
git clone https://github.com/zapdy/ascii-media-renderer.git 
cd ascii-media-renderer 
./gradlew installDist
./app/build/install/ascii-media-renderer/bin/ascii-media-renderer
```

## 🚀 Usage
```bash id="4cz2yf"
ascii-media-renderer [mode] <file-path | url> [flags]
```

### 🎛️ Modes
| Mode                      | Description                                   |
| ------------------------- | ----------------------------------------------|
| `--image`, `-i`           | Render an image as ASCII art                  |
| `--video`, `-v`           | Render a video as ASCII art                  |
| `--youtube`, `-y`         | Render a YouTube video as ASCII art          |
| `--youtube-search`, `-ys` | Render a searched YouTube video as ASCII art |

### ⚙️ Flags 
| Flag               | Description        |
| ------------------ | -------------------|
| `--reversed`, `-r` | Reverse brightness |

### ❓ Help 
| Option         | Description |
| -------------- | ------------|
| `--help`, `-h` | Show help   |
