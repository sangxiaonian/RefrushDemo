package sang.com.refrushdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sang.com.easyrefrush.RefrushLayoutView;
import sang.com.easyrefrush.inter.OnRefreshListener;


public class MainActivity extends AppCompatActivity {

    private SwipeRefreshLayout refreshLayout;
    private RefrushLayoutView refrushLayoutView;
    private RecyclerView recyclerView;
    private RecyclerView rv;

    private List<String> datas;
    private List<String> datas1;
    private MyAdapter myAdapter;
    private MyAdapter xAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        intData();


    }

    private void intData() {
        datas = new ArrayList<>();
        datas1 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            datas.add("测试数据" + i);
            datas1.add("测试数据" + i);
        }
        myAdapter = new MyAdapter(datas);
        xAdapter = new MyAdapter(datas1);
        recyclerView.setAdapter(myAdapter);
        rv.setAdapter(xAdapter);

        refrushLayoutView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                refrushLayoutView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refrushLayoutView.finishRefrush();
                    }
                }, 1520);
            }
        });

    }

    private void initView() {
        refreshLayout = findViewById(R.id.refresh);
        recyclerView = findViewById(R.id.rv);
        refrushLayoutView = findViewById(R.id.xrefresh);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);


        rv = findViewById(R.id.rv1);
        LinearLayoutManager mal = new LinearLayoutManager(this);
        mal.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(mal);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                refreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int size = datas.size();
                        for (int i = size; i < size + 1; i++) {
                            datas.add("测试数据" + i);
                        }
                        myAdapter.notifyItemRangeChanged(size, datas.size());

                        refreshLayout.setRefreshing(false);

                    }
                }, 1000);
            }
        });
    }


    private class MyAdapter extends RecyclerView.Adapter {


        private List<String> datas;

        public MyAdapter(List<String> datas1) {
            this.datas = datas1;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.item, parent, false));
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof MHolder) {
                ((MHolder) holder).initView(datas.get(position), position);
            }
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }
    }

    private class MHolder extends RecyclerView.ViewHolder {

        private TextView tv;

        public MHolder(View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv);
        }

        public void initView(String data, int position) {
            tv.setText(data);
        }
    }

}
