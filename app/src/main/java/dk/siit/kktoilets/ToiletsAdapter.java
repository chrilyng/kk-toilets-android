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

    private List<Toilet> mToilets;

    public ToiletsAdapter(List<Toilet> toilets) {
        mToilets = toilets;
    }

    public void addToilets(List<Toilet> toilets) {
        mToilets.addAll(toilets);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public ViewHolder(View v) {
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

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.text_row_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.getTextView().setText(mToilets.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return mToilets.size();
    }

}
