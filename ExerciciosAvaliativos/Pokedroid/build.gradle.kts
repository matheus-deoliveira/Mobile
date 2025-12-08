// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    // Adicionando a definição do plugin Safe Args para o projeto inteiro
    id("androidx.navigation.safeargs.kotlin") version "2.7.7" apply false
}
