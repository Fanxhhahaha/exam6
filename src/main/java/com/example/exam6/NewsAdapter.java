package com.example.exam6;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private List<NewsItem> newsList;
    private Context context;
    private OnItemClickListener listener;

    // 定义接口
    public interface OnItemClickListener {
        void onItemClick(NewsItem newsItem);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public NewsAdapter(Context context) {
        this.context = context;
        this.newsList = new ArrayList<>();
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsItem news = newsList.get(position);
        holder.titleTextView.setText(news.getTitle());
        holder.descriptionTextView.setText(news.getDescription());
        holder.sourceTextView.setText(news.getSource());
        holder.timeTextView.setText(news.getCtime());

        Glide.with(context)
                .load(news.getPicUrl())
                .centerCrop()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public void setNewsList(List<NewsItem> newsList) {
        this.newsList = newsList;
        notifyDataSetChanged();
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView;
        TextView descriptionTextView;
        TextView sourceTextView;
        TextView timeTextView;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.newsImage);
            titleTextView = itemView.findViewById(R.id.newsTitle);
            descriptionTextView = itemView.findViewById(R.id.newsDescription);
            sourceTextView = itemView.findViewById(R.id.newsSource);
            timeTextView = itemView.findViewById(R.id.newsTime);

            // 设置点击事件
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(newsList.get(position));
                }
            });
        }

        public void bind(NewsItem newsItem) {
            titleTextView.setText(newsItem.getTitle());
            // 其他绑定...
        }

    }

    // 添加更多数据的方法
    public void addNews(List<NewsItem> moreNews) {
        int startPosition = newsList.size();
        newsList.addAll(moreNews);
        notifyItemRangeInserted(startPosition, moreNews.size());
    }

}