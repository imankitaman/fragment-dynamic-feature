package ankitaman.`in`.details_feature

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ankitaman.`in`.dynamicfeature.BaseSplitFragment

/**
 * Created by Ankit Aman
 */
class DetailsFragment : BaseSplitFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.details_view, container, false)
        return view
    }
}