package suarez.sergio.shareappslibrary;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by s.suarez.linares on 28/09/2017.
 */

public class ShareSelectorFragment extends Fragment implements View.OnClickListener, ShareSelectorAdapter.OnShareAppClicked {

	public static final String SHARE_SELECTOR_FRAGMENT_TAG = "SHARE_SELECTOR_FRAGMENT";
	public static final String SHARE_SELECTOR_FRAGMENT_TITLE = "SHARE_SELECTOR_TITLE";
	public static final String SHARE_SELECTOR_FRAGMENT_INTENT = "SHARE_SELECTOR_INTENT";
	private static final int COLUMNS_NUMBER = 4;
	private static final int ANIM_DURATION = 375;

	private FrameLayout background;
	private RelativeLayout layoutBox;
	private TextView title;
	private RecyclerView recyclerView;

	private Activity activity;
	private ShareSelectorCallbackListener listener;
	private ShareSelectorAdapter adapter;

	private ArrayList<ResolveInfo> apps;

	private boolean isSlidingDown = false;

	public static void newInstance(Activity activity, ShareSelectorCallbackListener listener, String title, Intent intent) {
		//To ensure not multiple instances are shown
		if (activity.getFragmentManager().findFragmentByTag(ShareSelectorFragment.SHARE_SELECTOR_FRAGMENT_TAG) == null) {
			ShareSelectorFragment fragment = new ShareSelectorFragment();
			Bundle args = new Bundle();
			args.putString(SHARE_SELECTOR_FRAGMENT_TITLE, title);
			args.putParcelable(SHARE_SELECTOR_FRAGMENT_INTENT, intent);
			fragment.setArguments(args);
			fragment.setActivity(activity);
			fragment.setListener(listener);
			activity.getFragmentManager().beginTransaction().add(android.R.id.content, fragment, SHARE_SELECTOR_FRAGMENT_TAG).commit();
		}
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.share_selector_fragment, container, false);

		//Default title
		String titleText = "Share";

		if (getArguments() != null) {
			titleText = getArguments().getString(SHARE_SELECTOR_FRAGMENT_TITLE);
			Intent intent = getArguments().getParcelable(SHARE_SELECTOR_FRAGMENT_INTENT);
			apps = (ArrayList<ResolveInfo>) activity.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		}

		background = view.findViewById(R.id.new_share_selector_background);
		layoutBox = view.findViewById(R.id.new_share_selector_box);
		title = view.findViewById(R.id.new_share_selector_title);
		recyclerView = view.findViewById(R.id.new_share_selector_recycler_view);

		title.setText(titleText);

		background.setOnClickListener(this);

		adapter = new ShareSelectorAdapter(getActivity(), this, apps != null ? apps.subList(0, 4) : new ArrayList<ResolveInfo>());
		recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), COLUMNS_NUMBER));
		recyclerView.setAdapter(adapter);

		//Waiting till view finished on first creation
		ShareSelectorViewUtils.runOnGlobalLayout(view, new Runnable() {
			@Override
			public void run() {
				slideUpAnimation();
			}
		});

		//On key back pressed this listener intercepts it and fires the animations
		view.setFocusableInTouchMode(true);
		view.requestFocus();
		view.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				//Listener called twice: ACTION_DOWN & ACTION_UP
				if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
					slideDownAnimation();
					return true;
				}
				return false;
			}
		});

		return view;
	}

	@Override
	public void onPause() {
		super.onPause();
		slideDownAnimation();
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public void setListener(ShareSelectorCallbackListener listener) {
		this.listener = listener;
	}

	private void slideUpAnimation() {
		ValueAnimator valueAnimator = ValueAnimator.ofFloat(1, 0);
		valueAnimator.setDuration(ANIM_DURATION);
		valueAnimator.setInterpolator(new DecelerateInterpolator());
		valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				float progress = (float) valueAnimator.getAnimatedValue();
				layoutBox.setTranslationY(layoutBox.getHeight() * progress);
				layoutBox.setAlpha(1 - progress);

				background.setAlpha(1 - progress);
			}
		});
		valueAnimator.start();
	}

	private void slideDownAnimation() {
		if (!isSlidingDown) {
			isSlidingDown = true;
			ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
			valueAnimator.setDuration(ANIM_DURATION);
			valueAnimator.setInterpolator(new AccelerateInterpolator());
			valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator valueAnimator) {
					float progress = (float) valueAnimator.getAnimatedValue();
					layoutBox.setTranslationY(layoutBox.getHeight() * progress);
					layoutBox.setAlpha(1 - progress);

					background.setAlpha(1 - progress);
				}
			});
			valueAnimator.addListener(new Animator.AnimatorListener() {
				@Override
				public void onAnimationStart(Animator animator) {

				}

				@Override
				public void onAnimationEnd(Animator animator) {
					//Reset alpha value once ended
					layoutBox.setAlpha(1);
					//Remove fragment
					getFragmentManager().beginTransaction()
							.remove(getFragmentManager().findFragmentByTag(ShareSelectorFragment.SHARE_SELECTOR_FRAGMENT_TAG))
							.commitAllowingStateLoss();

				}

				@Override
				public void onAnimationCancel(Animator animator) {

				}

				@Override
				public void onAnimationRepeat(Animator animator) {

				}
			});
			valueAnimator.start();
		}
	}

	@Override
	public void onClick(ResolveInfo app) {
		if (listener != null) {
			listener.appSelected(app);
		}
		slideDownAnimation();
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.new_share_selector_background) {
			slideDownAnimation();
		}
	}

	public interface ShareSelectorCallbackListener {
		void appSelected(ResolveInfo app);
	}

}
