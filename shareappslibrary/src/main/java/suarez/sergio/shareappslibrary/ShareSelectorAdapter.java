package suarez.sergio.shareappslibrary;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ShareSelectorAdapter extends RecyclerView.Adapter<ShareSelectorAdapter.ShareItemViewHolder> {

	private List<ResolveInfo> apps;
	private LayoutInflater inflater;
	private PackageManager packageManager;
	private OnShareAppClicked listener;

	public ShareSelectorAdapter(Context context, OnShareAppClicked listener, List<ResolveInfo> apps) {
		this.packageManager = context.getPackageManager();
		this.inflater = LayoutInflater.from(context);
		this.apps = apps;
		this.listener = listener;
	}

	@Override
	public ShareItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.share_selector_item, parent, false);
		return new ShareItemViewHolder(view);
	}

	@Override
	public void onBindViewHolder(ShareItemViewHolder holder, int position) {
		ResolveInfo app = apps.get(position);
		holder.onBind(app);
	}

	@Override
	public int getItemCount() {
		return apps.size();
	}

	public class ShareItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

		private ResolveInfo app;
		private TextView name;
		private ImageView appImage;

		public ShareItemViewHolder(View itemView) {
			super(itemView);
			this.name = itemView.findViewById(R.id.new_share_selector_title);
			this.appImage = itemView.findViewById(R.id.share_selector_image);
			itemView.setOnClickListener(this);
		}

		public void onBind(final ResolveInfo app) {
			this.app = app;
			this.name.setText(app.activityInfo.applicationInfo.loadLabel(packageManager));
			this.appImage.setImageDrawable(app.activityInfo.applicationInfo.loadIcon(packageManager));
		}

		@Override
		public void onClick(View view) {
			listener.onClick(this.app);
		}

	}

	public interface OnShareAppClicked {
		void onClick(ResolveInfo app);
	}

}
