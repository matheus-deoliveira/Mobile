package org.example.ConceitosBasicos

class Song(
    val title: String,
    val artist: String,
    val year: Int,
    var contReproductions: Int
)
{
    fun show(): String {
        return "$title, de $artist, lançado em $year"
    }

    fun famousMusic(): Boolean {
        return contReproductions > 1000
    }

    fun play() {
        contReproductions++
        println("Playing the song '$title' by $artist...")
    }
}

fun main () {
    val song = Song("Imagine", "John Lennon", 1971, 999)
    println(song.show())
    println("Is the song famous? ${if (song.famousMusic()) "Yes" else "No"}")
}