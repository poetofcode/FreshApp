package utils

import java.awt.Desktop
import java.net.URI
import java.net.URISyntaxException
import java.net.URL


fun openWebpage(uri: URI?): Boolean {
    val desktop = if (Desktop.isDesktopSupported()) Desktop.getDesktop() else null
    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
        try {
            desktop.browse(uri)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return false
}

fun openWebpage(url: URL): Boolean {
    try {
        return openWebpage(url.toURI())
    } catch (e: URISyntaxException) {
        e.printStackTrace()
    }
    return false
}

fun openWebpage(url: String) : Boolean {
    try {
        return openWebpage(URL(url))
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return false
}