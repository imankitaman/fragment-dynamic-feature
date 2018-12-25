package ankitaman.`in`.dynamicfeature

import android.content.Context
import android.support.v4.app.Fragment
import com.google.android.play.core.splitcompat.SplitCompat

/**
 * Created by Ankit Aman
 */
open class BaseSplitFragment : Fragment() {

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        SplitCompat.install(activity)
    }
}