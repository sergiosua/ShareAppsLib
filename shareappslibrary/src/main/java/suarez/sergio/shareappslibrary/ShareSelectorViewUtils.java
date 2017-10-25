package suarez.sergio.shareappslibrary;

import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by s.suarez.linares on 28/09/2017.
 */

public class ShareSelectorViewUtils {

	public static void runOnGlobalLayout(final View view, final Runnable runnable) {
		ViewTreeObserver vto = view.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				runnable.run();
			}
		});
	}

}
