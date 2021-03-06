package it.matteolobello.palazzovenezia.ui.adapter.recyclerview;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import it.matteolobello.palazzovenezia.R;
import it.matteolobello.palazzovenezia.data.asset.AssetImageSetter;
import it.matteolobello.palazzovenezia.data.bundle.BundleKeys;
import it.matteolobello.palazzovenezia.data.model.Painting;
import it.matteolobello.palazzovenezia.ui.activity.PaintingActivity;
import it.matteolobello.palazzovenezia.util.DpPxUtil;
import it.matteolobello.palazzovenezia.util.SystemBarsUtil;

public class PaintingsRecyclerViewAdapter extends RecyclerView.Adapter<PaintingsRecyclerViewAdapter.ViewHolder> {

    @Nullable
    private View mPaintingsFragmentRootView;

    private final ArrayList<Painting> mPaintingArrayList;

    public PaintingsRecyclerViewAdapter(ArrayList<Painting> paintingArrayList) {
        this(null, paintingArrayList);
    }

    public PaintingsRecyclerViewAdapter(@Nullable View paintingsFragmentRootView, ArrayList<Painting> paintingArrayList) {
        mPaintingsFragmentRootView = paintingsFragmentRootView;
        mPaintingArrayList = paintingArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_painting_rv, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Painting painting = mPaintingArrayList.get(position);
        final ImageView paintingImageView = holder.getPaintingImageView();

        AssetImageSetter.setImageByPaintingId(paintingImageView, painting.getId());

        final View.OnClickListener onPaintingClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), PaintingActivity.class);
                intent.putExtra(BundleKeys.EXTRA_PAINTING, painting);
                view.getContext().startActivity(intent);
            }
        };

        final View.OnLongClickListener onPaintingLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mPaintingsFragmentRootView != null) {
                    Snackbar snackbar = Snackbar.make(mPaintingsFragmentRootView, painting.getName(), Snackbar.LENGTH_SHORT)
                            .setAction(R.string.info, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    onPaintingClickListener.onClick(view);
                                }
                            })
                            .setActionTextColor(Color.WHITE);
                    snackbar.getView().setTranslationY(-(
                            SystemBarsUtil.getNavigationBarHeight(mPaintingsFragmentRootView.getContext())
                                    + DpPxUtil.convertDpToPixel(56)));
                    snackbar.show();
                } else {
                    Toast.makeText(view.getContext(), painting.getName(), Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        };

        holder.itemView.setOnClickListener(onPaintingClickListener);
        holder.itemView.setOnLongClickListener(onPaintingLongClickListener);
    }

    @Override
    public int getItemCount() {
        return mPaintingArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mPaintingImageView;

        ViewHolder(View itemView) {
            super(itemView);

            mPaintingImageView = itemView.findViewById(R.id.painting_image_view);
        }

        ImageView getPaintingImageView() {
            return mPaintingImageView;
        }
    }
}