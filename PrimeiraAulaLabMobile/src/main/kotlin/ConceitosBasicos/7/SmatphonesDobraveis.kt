package org.example.ConceitosBasicos.`7`

open class Phone(var isScreenLightOn: Boolean = false){
    open fun switchOn() {
        isScreenLightOn = true
    }

    fun switchOff() {
        isScreenLightOn = false
    }

    fun checkPhoneScreenLight() {
        val phoneScreenLight = if (isScreenLightOn) "on" else "off"
        println("The phone screen's light is $phoneScreenLight.")
    }
}

class FoldablePhone() : Phone(){
    var isFolded: Boolean = true
    fun fold() {
        println("Folding the phone...")
        isFolded = true
        switchOff()
    }
    fun unfold() {
        println("Unfolding the phone...")
        isFolded = false
        switchOn()
    }
    override fun switchOn(){
        if(isFolded){
            isScreenLightOn = false
            println("The phone is folded. Unfold it before switching on the screen.")
            return
        }
        isScreenLightOn = true
    }
}

fun main () {
    val phone = Phone()
    phone.switchOn()
    phone.checkPhoneScreenLight()
    phone.switchOff()
    phone.checkPhoneScreenLight()

    println("---")

    val foldablePhone = FoldablePhone()
    foldablePhone.checkPhoneScreenLight()
    foldablePhone.switchOn() // Tentativa de ligar dobrado
    foldablePhone.unfold()   // Desdobrando e ligando
    foldablePhone.checkPhoneScreenLight()
    foldablePhone.fold()     // Dobrando e desligando
    foldablePhone.checkPhoneScreenLight()
}