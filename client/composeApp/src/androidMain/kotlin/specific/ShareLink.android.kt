package specific

import androidx.core.app.ShareCompat
import com.poetofcode.freshapp.FreshApp

actual fun shareLink(url: String) {
    ShareCompat.IntentBuilder(FreshApp.instance)
        .setType("text/plain")
        .setChooserTitle("Share URL")
        .setText(url)
        .startChooser()
}