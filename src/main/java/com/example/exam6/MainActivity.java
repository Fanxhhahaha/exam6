package com.example.exam6;

import android.content.Intent;
import android.os.Bundle;

import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isLoading = false;
    private int currentPage = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupViews();
        setupRecyclerView();

        // 修改这里的监听器设置
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchNews(false);
            }
        });

        // 首次加载数据
        fetchNews(false);
    }

    private void setupViews() {
        recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
    }


    private void setupRecyclerView() {
        newsAdapter = new NewsAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(newsAdapter);

        // 添加滚动监听
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    // 检查是否滑动到底部
                    if (!isLoading
                            && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        loadMoreNews();
                    }
                }
            }
        });

        // 设置点击事件监听
        newsAdapter.setOnItemClickListener(newsItem -> {
            Intent intent = new Intent(MainActivity.this, NewsDetailActivity.class);
            intent.putExtra(NewsDetailActivity.EXTRA_NEWS_URL, newsItem.getUrl());
            startActivity(intent);
        });
    }

    private void loadMoreNews() {
        isLoading = true;
        currentPage++;
        fetchNews(true);
    }

    private void fetchNews(final boolean isLoadingMore) {
        if (!isLoadingMore) {
            currentPage = 1;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://apis.tianapi.com/")
                .addConverterFactory(GsonConverterFactory.create()) // 这里添加了Gson转换器
                .build();

        NewsApi newsApi = retrofit.create(NewsApi.class);

        Call<NewsResponse> call = newsApi.getNews(
                "d7bf57b1178acb3d35a842f28421cc9a",
                10,  // 每页数量
                currentPage  // 当前页码
        );

        //执行异步网络请求
        call.enqueue(new Callback<NewsResponse>() {
            // 请求成功的处理
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                swipeRefreshLayout.setRefreshing(false);
                isLoading = false;

                if (response.isSuccessful() && response.body() != null) {
                    NewsResponse newsResponse = response.body();
                    if (newsResponse.getCode() == 200) {
                        if (isLoadingMore) {
                            // 加载更多时追加数据
                            newsAdapter.addNews(newsResponse.getResult().getNewslist());
                        } else {
                            // 首次加载或刷新时替换数据
                            newsAdapter.setNewsList(newsResponse.getResult().getNewslist());
                        }
                    }
                }
            }
            // 请求失败的处理
            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                isLoading = false;
                Toast.makeText(MainActivity.this,
                        "获取新闻失败: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}