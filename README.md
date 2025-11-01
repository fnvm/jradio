# jradio


<img width="346" height="263" alt="image" src="https://github.com/user-attachments/assets/7c360292-5064-49c3-96b0-9bda9ef7160b" />

---

A simple console radio written in Java  
➔ [Releases page](https://github.com/fnvm/jradio/releases)

---

During the build, the project expects the `ffplay` binary in:

- `src/main/resources/ffmpeg/`

You can either include the FFmpeg binaries for your operating system in the appropriate directory, or simply install FFmpeg on your system and make sure that `ffplay` is available in `PATH`.


To build the project:

```bash
mvn clean package
```

