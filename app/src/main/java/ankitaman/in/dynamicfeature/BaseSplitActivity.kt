package ankitaman.`in`.dynamicfeature
import android.content.Context
import android.support.v7.app.AppCompatActivity
import com.google.android.play.core.splitcompat.SplitCompat

/**
 * Created by Ankit Aman
 */
abstract class BaseSplitActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        SplitCompat.install(this)
    }
}