package org.example.ConceitosBasicos

fun main() {
    printFinalTemperature(27.0, "Celsius", "Fahrenheit", ::celsiusToFahrenheit)
    printFinalTemperature(350.0, "Kelvin", "Celsius", ::kelvinToCelsius)
    printFinalTemperature(10.0, "Fahrenheit", "Kelvin", ::fahrenheitToKelvin)
}

fun celsiusToFahrenheit(temperature : Double) : Double {
    var result : Double = 0.0

    result = ((9*temperature)/5) + 32

    return result
}

fun kelvinToCelsius(temperature : Double) : Double {
    var result : Double = 0.0

    result = temperature - 273.15

    return result
}

fun fahrenheitToKelvin(temperature : Double) : Double {
    var result : Double = 0.0

    result = ((5*(temperature - 32))/9) + 273.15

    return result
}

fun printFinalTemperature(
    initialMeasurement: Double,
    initialUnit: String,
    finalUnit: String,
    conversionFormula: (Double) -> Double
) {
    val finalMeasurement = String.format("%.2f", conversionFormula(initialMeasurement)) // two decimal places
    println("$initialMeasurement degrees $initialUnit is $finalMeasurement degrees $finalUnit.")
}
