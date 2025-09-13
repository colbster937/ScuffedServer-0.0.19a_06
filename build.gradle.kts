plugins { 
    application
    java 
}

group = "com.mojang.minecraft"
version = "1.0.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
        }
    }

sourceSets {
    named("main") {
        java.setSrcDirs(listOf("src/game", "src/main"))
    }
}

application {
    mainClass.set("com.mojang.minecraft.server.MinecraftServer")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.mojang.minecraft.server.MinecraftServer"
    }
    archiveFileName.set("server.jar")
}