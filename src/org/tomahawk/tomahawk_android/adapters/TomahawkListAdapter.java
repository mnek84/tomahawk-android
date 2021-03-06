/* == This file is part of Tomahawk Player - <http://tomahawk-player.org> ===
 *
 *   Copyright 2012, Enno Gottschalk <mrmaffen@googlemail.com>
 *
 *   Tomahawk is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Tomahawk is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with Tomahawk. If not, see <http://www.gnu.org/licenses/>.
 */
package org.tomahawk.tomahawk_android.adapters;

import com.emilsjolander.components.stickylistheaders.StickyListHeadersAdapter;

import org.tomahawk.libtomahawk.collection.Album;
import org.tomahawk.libtomahawk.collection.Artist;
import org.tomahawk.libtomahawk.collection.CustomPlaylist;
import org.tomahawk.libtomahawk.collection.Track;
import org.tomahawk.libtomahawk.utils.TomahawkUtils;
import org.tomahawk.tomahawk_android.R;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * @author Enno Gottschalk <mrmaffen@googlemail.com>
 */
public class TomahawkListAdapter extends TomahawkBaseAdapter implements StickyListHeadersAdapter {

    private LayoutInflater mLayoutInflater;

    private ResourceHolder mSingleLineListItemResourceHolder;

    private ResourceHolder mDoubleLineListItemResourceHolder;

    private ResourceHolder mCategoryHeaderResourceHolder;

    private ResourceHolder mAddButtonResourceHolder;

    private boolean mShowCategoryHeaders = false;

    private boolean mShowPlaylistHeader = false;

    private boolean mShowContentHeader = false;

    private TomahawkBaseAdapter.TomahawkListItem mContentHeaderTomahawkListItem;

    private boolean mShowHighlightingAndPlaystate = false;

    private boolean mShowResolvedBy = false;

    private boolean mShowAddButton = false;

    /**
     * Constructs a new {@link TomahawkListAdapter} to display list items with a single line of text
     * and categoryHeaders
     */
    public TomahawkListAdapter(Activity activity, List<List<TomahawkListItem>> listArray) {
        mActivity = activity;
        mLayoutInflater = mActivity.getLayoutInflater();
        mListArray = listArray;
        mSingleLineListItemResourceHolder = new ResourceHolder();
        mSingleLineListItemResourceHolder.resourceId = R.layout.single_line_list_item;
        mSingleLineListItemResourceHolder.textViewId1 = R.id.single_line_list_textview;
        mDoubleLineListItemResourceHolder = new ResourceHolder();
        mDoubleLineListItemResourceHolder.resourceId = R.layout.double_line_list_item;
        mDoubleLineListItemResourceHolder.imageViewId = R.id.double_line_list_imageview;
        mDoubleLineListItemResourceHolder.imageViewId2 = R.id.double_line_list_imageview2;
        mDoubleLineListItemResourceHolder.textViewId1 = R.id.double_line_list_textview;
        mDoubleLineListItemResourceHolder.textViewId2 = R.id.double_line_list_textview2;
        mDoubleLineListItemResourceHolder.textViewId3 = R.id.double_line_list_textview3;
    }

    public void setShowCategoryHeaders(boolean showCategoryHeaders) {
        mCategoryHeaderResourceHolder = new ResourceHolder();
        mCategoryHeaderResourceHolder.resourceId = R.layout.single_line_list_header;
        mCategoryHeaderResourceHolder.imageViewId = R.id.single_line_list_header_icon_imageview;
        mCategoryHeaderResourceHolder.textViewId1 = R.id.single_line_list_header_textview;
        mShowCategoryHeaders = showCategoryHeaders;
    }

    public void setShowPlaylistHeader(boolean showPlaylistHeader) {
        mCategoryHeaderResourceHolder = new ResourceHolder();
        mCategoryHeaderResourceHolder.resourceId = R.layout.show_playlist_header;
        mShowPlaylistHeader = showPlaylistHeader;
    }

    public void setShowContentHeader(boolean showContentHeader, ListView list,
            TomahawkBaseAdapter.TomahawkListItem contentHeaderTomahawkListItem) {
        mContentHeaderTomahawkListItem = contentHeaderTomahawkListItem;
        mShowContentHeader = showContentHeader;
        View contentHeaderView = mLayoutInflater.inflate(R.layout.content_header, null);
        if (contentHeaderView != null && list.getHeaderViewsCount() == 0) {
            if (mContentHeaderTomahawkListItem instanceof Album) {
                ((Album) mContentHeaderTomahawkListItem).loadBitmap(mActivity,
                        (ImageView) contentHeaderView.findViewById(R.id.content_header_image));
            } else if (mContentHeaderTomahawkListItem instanceof Artist) {
                //((Artist) mContentHeaderTomahawkListItem).loadBitmap(mContext, (ImageView) contentHeaderView.findViewById(R.id.content_header_image));
            }
            ((TextView) contentHeaderView.findViewById(R.id.content_header_textview))
                    .setText(contentHeaderTomahawkListItem.getName());
            if (contentHeaderTomahawkListItem.getArtist() != null
                    && contentHeaderTomahawkListItem instanceof Album) {
                ((TextView) contentHeaderView.findViewById(R.id.content_header_textview2))
                        .setText(contentHeaderTomahawkListItem.getArtist().getName());
            }
            list.addHeaderView(contentHeaderView);
        }
    }

    public void setShowHighlightingAndPlaystate(boolean showHighlightingAndPlaystate) {
        this.mShowHighlightingAndPlaystate = showHighlightingAndPlaystate;
    }

    public void setShowResolvedBy(boolean showResolvedBy) {
        this.mShowResolvedBy = showResolvedBy;
    }

    public void setShowAddButton(boolean showAddButton, ListView list, String buttonText) {
        mShowAddButton = showAddButton;
        View addButtonFooterView = mLayoutInflater.inflate(R.layout.add_button_layout, null);
        if (addButtonFooterView != null && list.getFooterViewsCount() == 0) {
            ((TextView) addButtonFooterView.findViewById(R.id.add_button_textview))
                    .setText(buttonText);
            list.addFooterView(addButtonFooterView);
        }
    }

    /*
     * (non-Javadoc)
     * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        Object item = getItem(position);

        if (item != null) {
            ViewHolder viewHolder;
            if (((item instanceof Artist || item instanceof CustomPlaylist) && convertView == null)
                    || ((item instanceof Artist || item instanceof CustomPlaylist)
                    && ((ViewHolder) convertView.getTag()).viewType
                    != R.id.tomahawklistadapter_viewtype_singlelinelistitem)) {
                view = mLayoutInflater.inflate(mSingleLineListItemResourceHolder.resourceId, null);
                viewHolder = new ViewHolder();
                viewHolder.viewType = R.id.tomahawklistadapter_viewtype_singlelinelistitem;
                viewHolder.textFirstLine = (TextView) view
                        .findViewById(mSingleLineListItemResourceHolder.textViewId1);
                view.setTag(viewHolder);
            } else if (!mShowHighlightingAndPlaystate && !mShowResolvedBy && (
                    (item instanceof Track && convertView == null) || (item instanceof Track
                            && ((ViewHolder) convertView.getTag()).viewType
                            != R.id.tomahawklistadapter_viewtype_doublelinelistitem))) {
                view = mLayoutInflater.inflate(mDoubleLineListItemResourceHolder.resourceId, null);
                viewHolder = new ViewHolder();
                viewHolder.viewType = R.id.tomahawklistadapter_viewtype_doublelinelistitem;
                viewHolder.textFirstLine = (TextView) view
                        .findViewById(mDoubleLineListItemResourceHolder.textViewId1);
                viewHolder.textSecondLine = (TextView) view
                        .findViewById(mDoubleLineListItemResourceHolder.textViewId2);
                viewHolder.textThirdLine = (TextView) view
                        .findViewById(mDoubleLineListItemResourceHolder.textViewId3);
                view.setTag(viewHolder);
            } else if ((mShowResolvedBy || mShowHighlightingAndPlaystate) && (
                    (item instanceof Track && convertView == null) || (item instanceof Track
                            && ((ViewHolder) convertView.getTag()).viewType
                            != R.id.tomahawklistadapter_viewtype_doublelineplaystateimagelistitem))) {
                view = mLayoutInflater.inflate(mDoubleLineListItemResourceHolder.resourceId, null);
                viewHolder = new ViewHolder();
                viewHolder.viewType
                        = R.id.tomahawklistadapter_viewtype_doublelineplaystateimagelistitem;
                viewHolder.imageViewLeft = (ImageView) view
                        .findViewById(mDoubleLineListItemResourceHolder.imageViewId);
                viewHolder.imageViewRight = (ImageView) view
                        .findViewById(mDoubleLineListItemResourceHolder.imageViewId2);
                viewHolder.textFirstLine = (TextView) view
                        .findViewById(mDoubleLineListItemResourceHolder.textViewId1);
                viewHolder.textSecondLine = (TextView) view
                        .findViewById(mDoubleLineListItemResourceHolder.textViewId2);
                viewHolder.textThirdLine = (TextView) view
                        .findViewById(mDoubleLineListItemResourceHolder.textViewId3);
                view.setTag(viewHolder);
            } else if ((item instanceof Album && convertView == null) || (item instanceof Album
                    && ((ViewHolder) convertView.getTag()).viewType
                    != R.id.tomahawklistadapter_viewtype_doublelineimagelistitem)) {
                view = mLayoutInflater.inflate(mDoubleLineListItemResourceHolder.resourceId, null);
                viewHolder = new ViewHolder();
                viewHolder.viewType = R.id.tomahawklistadapter_viewtype_doublelineimagelistitem;
                viewHolder.imageViewLeft = (ImageView) view
                        .findViewById(mDoubleLineListItemResourceHolder.imageViewId);
                viewHolder.textFirstLine = (TextView) view
                        .findViewById(mDoubleLineListItemResourceHolder.textViewId1);
                viewHolder.textSecondLine = (TextView) view
                        .findViewById(mDoubleLineListItemResourceHolder.textViewId2);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }

            if (viewHolder.viewType == R.id.tomahawklistadapter_viewtype_singlelinelistitem) {
                if (item instanceof Artist) {
                    viewHolder.textFirstLine.setText(((Artist) item).getName());
                } else if (item instanceof CustomPlaylist) {
                    viewHolder.textFirstLine.setText(((CustomPlaylist) item).getName());
                }
            } else if (viewHolder.viewType
                    == R.id.tomahawklistadapter_viewtype_doublelinelistitem) {
                if (item instanceof Track) {
                    viewHolder.textFirstLine.setText(((Track) item).getName());
                    viewHolder.textSecondLine.setText(((Track) item).getArtist().getName());
                    if (((Track) item).getDuration() > 0) {
                        viewHolder.textThirdLine.setText(
                                TomahawkUtils.durationToString(((Track) item).getDuration()));
                    } else {
                        viewHolder.textThirdLine.setText(mActivity.getResources().getString(
                                R.string.playbackactivity_seekbar_completion_time_string));
                    }
                }
            } else if (viewHolder.viewType
                    == R.id.tomahawklistadapter_viewtype_doublelineimagelistitem) {
                if (item instanceof Album) {
                    viewHolder.textFirstLine.setText(((Album) item).getName());
                    if (((Album) item).getArtist() != null) {
                        viewHolder.textSecondLine.setText(((Album) item).getArtist().getName());
                    }
                    viewHolder.imageViewLeft.setVisibility(ImageView.VISIBLE);
                    ((Album) item).loadBitmap(mActivity, viewHolder.imageViewLeft);
                }
            } else if (viewHolder.viewType
                    == R.id.tomahawklistadapter_viewtype_doublelineplaystateimagelistitem) {
                if (item instanceof Track) {
                    if (!((Track) item).isResolved()) {
                        viewHolder.textFirstLine.setTextColor(
                                mActivity.getResources().getColor(R.color.disabled_grey));
                        viewHolder.textSecondLine.setTextColor(
                                mActivity.getResources().getColor(R.color.disabled_grey));
                        viewHolder.textThirdLine.setTextColor(
                                mActivity.getResources().getColor(R.color.disabled_grey));
                    } else {
                        viewHolder.textFirstLine.setTextColor(
                                mActivity.getResources().getColor(R.color.primary_textcolor));
                        viewHolder.textSecondLine.setTextColor(
                                mActivity.getResources().getColor(R.color.secondary_textcolor));
                        viewHolder.textThirdLine.setTextColor(
                                mActivity.getResources().getColor(R.color.secondary_textcolor));
                    }
                    viewHolder.textFirstLine.setText(((Track) item).getName());
                    viewHolder.textSecondLine.setText(((Track) item).getArtist().getName());
                    if (((Track) item).getDuration() > 0) {
                        viewHolder.textThirdLine.setText(
                                TomahawkUtils.durationToString(((Track) item).getDuration()));
                    } else {
                        viewHolder.textThirdLine.setText(mActivity.getResources().getString(
                                R.string.playbackactivity_seekbar_completion_time_string));
                    }
                    if (mShowHighlightingAndPlaystate && position == mHighlightedItemPosition) {
                        view.setBackgroundResource(R.color.pressed_tomahawk);
                        if (mHighlightedItemIsPlaying) {
                            viewHolder.imageViewLeft.setVisibility(ImageView.VISIBLE);
                            viewHolder.imageViewLeft
                                    .setImageResource(R.drawable.ic_playlist_is_playing);
                        } else {
                            viewHolder.imageViewLeft.setVisibility(ImageView.GONE);
                        }
                    } else {
                        viewHolder.imageViewLeft.setVisibility(ImageView.GONE);
                        view.setBackgroundResource(R.drawable.selectable_background_tomahawk);
                    }
                    if (mShowResolvedBy && ((Track) item).isResolved()) {
                        viewHolder.imageViewRight.setVisibility(ImageView.VISIBLE);
                        Drawable resolverIcon = null;
                        if (((Track) item).getResolver() != null) {
                            resolverIcon = ((Track) item).getResolver().getIcon();
                        }
                        if (resolverIcon != null) {
                            viewHolder.imageViewRight
                                    .setImageDrawable(((Track) item).getResolver().getIcon());
                        } else if (((Track) item).isLocal()) {
                            viewHolder.imageViewRight
                                    .setImageResource(R.drawable.ic_action_collection);
                        } else {
                            viewHolder.imageViewRight
                                    .setImageResource(R.drawable.ic_resolver_default);
                        }
                    } else {
                        viewHolder.imageViewRight.setVisibility(ImageView.GONE);
                    }
                }
            }
        }
        return view;
    }

    /*
     * (non-Javadoc)
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        if ((mFiltered ? mFilteredListArray : mListArray) == null) {
            return 0;
        }
        int displayedListArrayItemsCount = 0;
        int displayedContentHeaderCount = 0;
        for (List<TomahawkListItem> list : (mFiltered ? mFilteredListArray : mListArray)) {
            displayedListArrayItemsCount += list.size();
        }
        return displayedListArrayItemsCount + displayedContentHeaderCount;
    }

    /*
     * (non-Javadoc)
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int position) {
        Object item = null;
        int offsetCounter = 0;
        for (int i = 0; i < (mFiltered ? mFilteredListArray : mListArray).size(); i++) {
            List<TomahawkListItem> list = (mFiltered ? mFilteredListArray : mListArray).get(i);
            if (position - offsetCounter < list.size()) {
                item = list.get(position - offsetCounter);
                break;
            }
            offsetCounter += list.size();
        }
        return item;
    }

    /*
     * (non-Javadoc)
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        if (mShowPlaylistHeader || mShowCategoryHeaders) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = mLayoutInflater
                        .inflate(mCategoryHeaderResourceHolder.resourceId, null);
                viewHolder = new ViewHolder();
                viewHolder.viewType = R.id.tomahawklistadapter_viewtype_header;
                viewHolder.imageViewLeft = (ImageView) convertView
                        .findViewById(mCategoryHeaderResourceHolder.imageViewId);
                viewHolder.textFirstLine = (TextView) convertView
                        .findViewById(mCategoryHeaderResourceHolder.textViewId1);
                convertView.setTag(viewHolder);
            }
            viewHolder = (ViewHolder) convertView.getTag();
            if (mShowCategoryHeaders && getItem(position) != null) {
                if (getItem(position) instanceof Track) {
                    viewHolder.imageViewLeft.setImageResource(R.drawable.ic_action_track);
                    viewHolder.textFirstLine.setText(R.string.tracksfragment_title_string);
                } else if (getItem(position) instanceof Artist) {
                    viewHolder.imageViewLeft.setImageResource(R.drawable.ic_action_artist);
                    viewHolder.textFirstLine.setText(R.string.artistsfragment_title_string);
                } else if (getItem(position) instanceof Album) {
                    viewHolder.imageViewLeft.setImageResource(R.drawable.ic_action_album);
                    viewHolder.textFirstLine.setText(R.string.albumsfragment_title_string);
                }
            }
            return convertView;
        } else {
            return new View(mActivity);
        }
    }

    //remember that these have to be static, position=1 should always return the same Id that is.
    @Override
    public long getHeaderId(int position) {
        long result = 0;
        int sizeSum = 0;
        if (mShowContentHeader) {
            sizeSum += 1;
        }
        for (List<TomahawkListItem> list : mFiltered ? mFilteredListArray : mListArray) {
            sizeSum += list.size();
            if (position < sizeSum) {
                break;
            } else {
                result++;
            }
        }
        return result;
    }

    public TomahawkListItem getContentHeaderTomahawkListItem() {
        return mContentHeaderTomahawkListItem;
    }

}
