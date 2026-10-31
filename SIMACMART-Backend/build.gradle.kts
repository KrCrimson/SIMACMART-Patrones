plugins {
    kotlin("jvm") version "1.9.22"
    application
}

group = "pe.edu.upt.simacmart"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    // Kotlin Standard Library
    implementation(kotlin("stdlib"))

    // Driver JDBC PostgreSQL (Central Cloud)
    implementation("org.postgresql:postgresql:42.7.2")

    // Driver JDBC SQLite (Búfer Local Offline Store-and-Forward)
    implementation("org.xerial:sqlite-jdbc:3.45.1.0")

    // Serialización JSON liviana (Jackson o Gson)
    implementation("com.google.code.gson:gson:2.10.1")

    // Seguridad de contraseñas (bcrypt para cuentas de usuario)
    implementation("org.mindrot:jbcrypt:0.4")

    // Pruebas Unitarias
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
}

application {
    mainClass.set("pe.edu.upt.simacmart.MainKt")
}

tasks.test {
    useJUnitPlatform()
}
