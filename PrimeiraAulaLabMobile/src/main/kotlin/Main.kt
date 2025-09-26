package org.example

fun main() {
    val nota : Int = 10
    print("- Digite seu nome: ")
    val nome : String? = readlnOrNull()
    val result : Int = soma(10, 20)
    println("- Olá $nome, sua nota final é $result")
}

fun soma(a : Int, b : Int) : Int {
    return a + b
}