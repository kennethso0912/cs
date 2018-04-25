package com.ubtechinc.cruiser.wayguide.adapter;

import java.util.Calendar;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;


import android.text.format.DateFormat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ubtechinc.cruiser.wayguide.R;


/**
 *  正在下载游戏列表数据适配
 * @author
 *
 */
public class DownLoadingAdapter extends CursorAdapter {


	private static final String TAG = "DownLoadingAdapter";

    private Context mContext;
    private Cursor mCursor;
    private int mIdColumnId = 0;
    private int mStatusColumnId = 0;
    private int mTotalBytesColumnId = 0;
    private int mCurrentBytesColumnId = 0;
    private int mLocalUriColumnId = 0;
    private int mMediaTypeColumnId = 0;
    private int mReasonColumnId = 0;
    private int mDescriptionColumnId = 0;
    private int mUriColumnId = 0;

    private static class ViewHolder{
        TextView tvName;
        TextView tvProgress;

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public DownLoadingAdapter(Context context, Cursor c) {
        super(context, c,true);
        this.mContext = context;
        this.mCursor = c;

        mIdColumnId = mCursor.getColumnIndexOrThrow(DownloadManager.COLUMN_ID);
        mStatusColumnId = mCursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS);
        mTotalBytesColumnId = mCursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
        mCurrentBytesColumnId = mCursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
        mLocalUriColumnId = mCursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI);
        mMediaTypeColumnId =  mCursor.getColumnIndexOrThrow(DownloadManager.COLUMN_MEDIA_TYPE);
        mReasonColumnId = mCursor.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON);
        mDescriptionColumnId = mCursor.getColumnIndexOrThrow(DownloadManager.COLUMN_DESCRIPTION);

        mUriColumnId = mCursor.getColumnIndexOrThrow(DownloadManager.COLUMN_URI);

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        long downloadId = cursor.getLong(mIdColumnId);
        long totalBytes = cursor.getLong(mTotalBytesColumnId);
        long currentBytes = cursor.getLong(mCurrentBytesColumnId);
        int status = cursor.getInt(mStatusColumnId);
        String uri=cursor.getString(mUriColumnId);
        
        int progress = getProgressValue(totalBytes, currentBytes);

        ViewHolder holder = (ViewHolder)view.getTag();
        holder.tvName.setText(uri.substring(uri.lastIndexOf("/")+1));
        holder.tvProgress.setText(progress+"%");

    }

    private int getProgressValue(long totalBytes,long currentBytes){
        if(totalBytes <=0 ){
            return 0;
        }

        return (int)(currentBytes*100 / totalBytes);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewgroup) {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.griditem_download, viewgroup, false);

        final ViewHolder holder = new ViewHolder();
        holder.tvName = (TextView)view.findViewById(R.id.tv_name);
        holder.tvProgress = (TextView)view.findViewById(R.id.tv_progress);
        view.setTag(holder);
        return view;
    }

    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        return super.getView(arg0, arg1, arg2);
    }
}
