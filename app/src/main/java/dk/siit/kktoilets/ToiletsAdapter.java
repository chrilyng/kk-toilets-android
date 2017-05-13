package dk.siit.kktoilets;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import dk.siit.kktoilets.model.Toilet;

public class ToiletsAdapter extends RecyclerView.Adapter<ToiletsAdapter.ViewHolder> {
    private static final String TAG = ToiletsAdapter.class.getName();
    public static final int PROGRESS_TYPE = 100;

    private List<Toilet> mToilets;
    private boolean mLoadComplete = false;

    public ToiletsAdapter(List<Toilet> toilets) {
        mToilets = toilets;
    }

    public void addToilets(List<Toilet> toilets) {
        mToilets.addAll(toilets);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder vh;
        if(viewType==PROGRESS_TYPE) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.progress_row_item, parent, false);
            vh = new ProgressViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.text_row_item, parent, false);
            vh = new ToiletViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(holder instanceof ToiletViewHolder) {
            ((ToiletViewHolder)holder).getTextView().setText(mToilets.get(position).toString());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(!mLoadComplete && position==mToilets.size())
            return PROGRESS_TYPE;
        else
            return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mLoadComplete ? mToilets.size() : mToilets.size()+1;
    }

    public void setLoadComplete(boolean complete) {
        mLoadComplete = complete;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View v) {
            super(v);
        }
    }

    private static class ProgressViewHolder extends ViewHolder {

        public ProgressViewHolder(View itemView) {
            super(itemView);
        }
    }


    private static class ToiletViewHolder extends ViewHolder {
        private final TextView textView;

        public ToiletViewHolder(View v) {
            super(v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getPosition() + " clicked.");
                }
            });
            textView = (TextView) v.findViewById(R.id.textView);
        }

        public TextView getTextView() {
            return textView;
        }
    }
}
