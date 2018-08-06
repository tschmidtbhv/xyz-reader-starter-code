package com.example.xyzreader.ui;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * xyz-reader-starter-code
 * Created by Thomas Schmidt on 05.08.2018.
 */
public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private static final String TAG = Adapter.class.getSimpleName();
    private Cursor mCursor;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
    // Use default locale format
    private SimpleDateFormat outputFormat = new SimpleDateFormat();
    // Most time functions can only handle 1902 - 2037
    private GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2, 1, 1);

    public Adapter(Cursor cursor, Context context) {
        mCursor = cursor;
    }

    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getLong(ArticleLoader.Query._ID);
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_article, parent, false);
        final ViewHolder vh = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ((SelectedListItem) parent.getContext()).itemSelected(vh.thumbnailView, getItemId(vh.getAdapterPosition()));

                } else {
                    Toast.makeText(parent.getContext(), "Wrong Version", Toast.LENGTH_SHORT).show();
                    //parent.getContext().startActivity(new Intent(Intent.ACTION_VIEW,
                    //        ItemsContract.Items.buildItemUri(getItemId(vh.getAdapterPosition()))));
                }
            }
        });
        return vh;
    }

    private Date parsePublishedDate() {
        try {
            String date = mCursor.getString(ArticleLoader.Query.PUBLISHED_DATE);
            return dateFormat.parse(date);
        } catch (ParseException ex) {
            Log.e(TAG, ex.getMessage());
            Log.i(TAG, "passing today's date");
            return new Date();
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.titleView.setText(mCursor.getString(ArticleLoader.Query.TITLE));
        Date publishedDate = parsePublishedDate();
        if (!publishedDate.before(START_OF_EPOCH.getTime())) {

            holder.subtitleView.setText(Html.fromHtml(
                    DateUtils.getRelativeTimeSpanString(
                            publishedDate.getTime(),
                            System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                            DateUtils.FORMAT_ABBREV_ALL).toString()
                            + "<br/>" + " by "
                            + mCursor.getString(ArticleLoader.Query.AUTHOR)));
        } else {
            holder.subtitleView.setText(Html.fromHtml(
                    outputFormat.format(publishedDate)
                            + "<br/>" + " by "
                            + mCursor.getString(ArticleLoader.Query.AUTHOR)));
        }

        holder.thumbnailView.setImageUrl(
                mCursor.getString(ArticleLoader.Query.THUMB_URL),
                ImageLoaderHelper.getInstance(holder.itemView.getContext()).getImageLoader());
        holder.thumbnailView.setAspectRatio(mCursor.getFloat(ArticleLoader.Query.ASPECT_RATIO));
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View itemView;
        public DynamicHeightNetworkImageView thumbnailView;
        public TextView titleView;
        public TextView subtitleView;


        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            thumbnailView = (DynamicHeightNetworkImageView) itemView.findViewById(R.id.thumbnail);
            titleView = (TextView) itemView.findViewById(R.id.article_title);
            subtitleView = (TextView) itemView.findViewById(R.id.article_subtitle);
        }
    }

}

